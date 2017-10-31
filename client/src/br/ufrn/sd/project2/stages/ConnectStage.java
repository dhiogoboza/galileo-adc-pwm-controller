/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.sd.project2.stages;

import br.ufrn.sd.project2.Main;
import br.ufrn.sd.project2.network.ClientConfig;
import br.ufrn.sd.project2.util.AsyncTask;
import br.ufrn.sd.project2.util.Config;
import br.ufrn.sd.project2.util.Log;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author dhiogoboza
 */
public class ConnectStage {
	
	private static final String TAG = "ConnectStage";
	
	private final String IP_CONFIG = "config.ip";
	private final String PORT_CONFIG = "config.port";
	private final String PASSWORD_CONFIG = "config.password";
	
	final private Stage myStage;
	private ProgressIndicator progressIndicator;
	
	private TextField ipTextField;
	private TextField portTextField;
	private PasswordField passwordTextField;
	private Button connectButton;
	
	public ConnectStage(Stage myStage) {
		this.myStage = myStage;
	}
	
	public void show() {
		Group root = new Group();
		Scene scene = new Scene(root);
        myStage.setScene(scene);
		
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		
		int line = 0;
		
		Text scenetitle = new Text("Digite os dados para conexão");
		scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		grid.add(scenetitle, 0, line, 2, 1);
		
		progressIndicator = new ProgressIndicator();
		progressIndicator.setVisible(false);
		progressIndicator.setPrefHeight(25);
		progressIndicator.setPrefWidth(25);
		
		TilePane progressIndicatorPane = new TilePane(Orientation.HORIZONTAL);
		progressIndicatorPane.setAlignment(Pos.BASELINE_RIGHT);
		progressIndicatorPane.getChildren().add(progressIndicator);
		progressIndicatorPane.setPadding(new Insets(0, 10, 0, 0));
		
		grid.add(progressIndicatorPane, 1, line, 2, 1);

		line++;
		Label ipLabel = new Label("IP:");
		grid.add(ipLabel, 0, line);

		ipTextField = new TextField();
		grid.add(ipTextField, 1, line);
		
		line++;
		Label portLabel = new Label("Porta:");
		grid.add(portLabel, 0, line);
		
		portTextField = new TextField();
		grid.add(portTextField, 1, line);

		line++;
		Label passwordLabel = new Label("Senha:");
		grid.add(passwordLabel, 0, line);

		passwordTextField = new PasswordField();
		grid.add(passwordTextField, 1, line);
		
		fillFields();
		
		line++;
		connectButton = new Button("Conectar");
		connectButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				new AsyncTask() {

					@Override
					public void onPreExecute() {
						Log.d(TAG, "onPreExecute");
						
						connectButton.setDisable(true);
						
						progressIndicator.setVisible(true);
					}

					@Override
					public void doInBackground() throws Exception {
						Log.d(TAG, "doInBackground");
						
						Main.getInstance().initClient(new ClientConfig(ipTextField.getText(),
                                Integer.parseInt(portTextField.getText()),
                                passwordTextField.getText()));
					}
					
					@Override
					public void progressCallback(Object... params) {
						
					}

					@Override
					public void onPostExecute() {
						if (!Main.getInstance().getClient().isConnected()) {
                            progressIndicator.setVisible(false);
                            connectButton.setDisable(false);
        
                            Main.getInstance().showToast("Erro ao conectar, verifique os campos");
                        }
					}

					@Override
					public void onFail(Exception e) {
						Log.e(TAG, "Error initializing client", e);
						
						Main.getInstance().showToast("Erro ao conectar, verifique os campos");
						
						progressIndicator.setVisible(false);
						connectButton.setDisable(false);
					}
				}.execute();
			}
		});
		TilePane buttonPane = new TilePane(Orientation.HORIZONTAL);
		buttonPane.setAlignment(Pos.BASELINE_RIGHT);
		buttonPane.getChildren().add(connectButton);
		
		
		grid.add(buttonPane, 1, line);
		
		root.getChildren().add(grid);
		
		myStage.show();
	}

	private void fillFields() {
		ipTextField.setText(Config.getConfig(IP_CONFIG));
		portTextField.setText(Config.getConfig(PORT_CONFIG));
		passwordTextField.setText(Config.getConfig(PASSWORD_CONFIG));
	}
	
	private void saveConfigs() {
		Config.saveConfig(IP_CONFIG, ipTextField.getText());
		Config.saveConfig(PORT_CONFIG, portTextField.getText());
		Config.saveConfig(PASSWORD_CONFIG, passwordTextField.getText());
	}
    
    public void onLoginFinished() {
        Log.d(TAG, "onLoginFinished");
		
        saveConfigs();
        
        progressIndicator.setVisible(false);
        connectButton.setDisable(false);

        if (Main.getInstance().getClient().isConnected()) {
            Main.getInstance().showMainStage();
            myStage.close();
        } else {
            Main.getInstance().showToast("Erro ao conectar, verifique os campos");
        }
    }
}
