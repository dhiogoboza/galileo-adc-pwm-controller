package br.ufrn.sd.project2;

import br.ufrn.sd.project2.network.Client;
import br.ufrn.sd.project2.network.ClientConfig;
import br.ufrn.sd.project2.stages.ConnectStage;
import br.ufrn.sd.project2.stages.MainStage;
import br.ufrn.sd.project2.util.LogPrintWriter;
import java.awt.Dimension;
import java.awt.Toolkit;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
 
/**
 * 
 */
public class Main extends Application {
	
	private static Main instance;

	public static Main getInstance() {
		return instance;
	}
	
	private MainStage mainStage;
	private Client client;
    private Window currentStage;
    private ConnectStage connectStage;
 
    public void showMainStage() {
        currentStage = mainStage;
        
		mainStage.init();
		mainStage.show();
		
		LogPrintWriter logPrintWriter = new LogPrintWriter(mainStage.getLogTextArea());
		
		if (logPrintWriter.isInitialized()) {
			System.setOut(logPrintWriter.getDebugPrintWriter());
			System.setErr(logPrintWriter.getErrorPrintWriter());
		}
    }
 
    @Override
	public void start(Stage primaryStage) throws Exception {
		instance = this;
		
		mainStage = new MainStage();
		
        currentStage = primaryStage;
        
        connectStage = new ConnectStage(primaryStage);
        connectStage.show();
        //primaryStage.show();
		
		//showMainStage();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX 
     * application. main() serves only as fallback in case the 
     * application can not be launched through deployment artifacts,
     * e.g., in IDEs with limited FX support. NetBeans ignores main().
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

	public void initClient(ClientConfig clientConfig) {
		client = new Client(clientConfig);
		client.connect();
	}

	public Client getClient() {
		return client;
	}
    
    public ConnectStage getConnectionStage() {
        return connectStage;
    }
    
    public MainStage getMainStage() {
        return mainStage;
    }

	public void showToast(String toastMsg) {
		final Stage toastStage = new Stage();
        toastStage.initOwner(currentStage);
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);
        
        //Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        
		Label text = new Label(toastMsg);
        text.setFont(Font.font("Verdana", 16));
        //text.setFill(Color.RED);
		
		StackPane root = new StackPane();
		root.getChildren().add(text);
        root.setStyle("-fx-background-radius: 5; -fx-background-color: rgba(0, 0, 0, 0.3); -fx-padding: 10px;");
		
		Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
        toastStage.show();
        
        Dimension bounds = Toolkit.getDefaultToolkit().getScreenSize();
        
        toastStage.setX((bounds.getWidth() - toastStage.getWidth()) / 2);
        toastStage.setY(300);
        //toastStage.setY((bounds.getHeight() - toastStage.getHeight()) / 2);
		
		Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(100), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1)); 
        fadeInTimeline.getKeyFrames().add(fadeInKey1);   
        fadeInTimeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Timeline fadeOutTimeline = new Timeline();
						KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(200), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0)); 
						fadeOutTimeline.getKeyFrames().add(fadeOutKey1);   
						fadeOutTimeline.setOnFinished(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent t) {
								toastStage.close();
							}
						}); 
						fadeOutTimeline.play();
					}
					
				}.start();
			}
		}); 
        fadeInTimeline.play();
	}
}