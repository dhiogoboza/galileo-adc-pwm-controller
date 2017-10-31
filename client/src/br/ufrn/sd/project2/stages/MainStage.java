package br.ufrn.sd.project2.stages;

import br.ufrn.sd.project2.Main;
import br.ufrn.sd.project2.models.Function;
import br.ufrn.sd.project2.models.Limits;
import br.ufrn.sd.project2.models.Line;
import br.ufrn.sd.project2.network.Client;
import br.ufrn.sd.project2.util.Config;
import br.ufrn.sd.project2.util.FileUtils;
import br.ufrn.sd.project2.util.Log;
import br.ufrn.sd.project2.views.GraphCanvas;
import br.ufrn.sd.project2.views.GraphTicksCanvas;
import br.ufrn.sd.project2.views.ToggleSwitch;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *
 * @author dhiogoboza
 */
public class MainStage extends Stage implements EventHandler<ActionEvent> {
	
	private static final String TAG = "MainStage";
    
    private static final String COLOR_GREEN = "#009900";
    private static final String COLOR_RED = "#990000";
	
	private static final int MAX_DATA_POINTS = 600;
	
	private TabPane center;
	private TextArea logTextArea;
	private Scene stageScene;
	
	private BorderPane layoutPane;
    
    private Label state;
	private Label pwmValue;
	private Label adc;
	private Label functionValue;
    
    private boolean systemRunning = false;
	
    private double prefWidth;
	private double prefHeight;
	
    private GraphCanvas graphCanvas;
    
    private Button cancelButton;
    private Button addLineButton;
    private Button sendButton;
	private Button exportButton;
	//private Button importButton;
    
    private Label graphX;
    private Label graphY;
    private Label graphLinesCount;
    
	private LineChart<Number, Number> realTimeChart;
	private NumberAxis xRTAxis;
	private XYChart.Series rtSeries;
	private int currentGraphPosition  = 0;
    
    private Limits limits;
	private ToggleSwitch switchOnOff;
    

	public MainStage() {
		
	}
	
	public void init() {
		logTextArea = new TextArea();//new TextArea("");
		logTextArea.setWrapText(true);
		logTextArea.setEditable(false);
		
		//Stage primaryStage = new Stage(StageStyle.DECORATED);
		//setFullScreen(true);
        Group root = new Group();
		stageScene = new Scene(root);
        setScene(stageScene);
		
		layoutPane = new BorderPane();
		
		//borderPane.setAlignment(Pos.TOP_CENTER);
		//grid.setHgap(0);
		//grid.setVgap(0);
		layoutPane.setPadding(new Insets(0, 0, 0, 0));
		layoutPane.setBottom(logTextArea);
		
		updateDimensions();
		
		initLeft();
		initCenter();
		initTop();
	
		root.getChildren().add(layoutPane);
		
		Log.d(TAG, "finalizado");
	}

	private void initTop() {
		
		TilePane topPane = new TilePane(Orientation.HORIZONTAL);
		
		MenuBar menuBar = new MenuBar();
 
        // --- Menu File
        Menu menuFile = new Menu("File");
		MenuItem add = new MenuItem("Exit");
		add.setId("exit");
        add.setOnAction(this);
        menuFile.getItems().addAll(add);
		
        // --- Menu Edit
        //Menu menuEdit = new Menu("Edit");
 
        // --- Menu View
        Menu menuView = new Menu("View");
		add = new MenuItem("Fullscreen");
		add.setId("fs");
        add.setOnAction(this);
        menuView.getItems().addAll(add);
 
        menuBar.getMenus().addAll(menuFile, menuView);
		
		//pane.setPrefHeight(100);
		//pane.setPrefWidth(getWidth());
		menuBar.setPrefWidth(getPrefWidth());
		
		topPane.getChildren().add(menuBar);
		
		layoutPane.setTop(topPane);
	}
	
	private void initLeft() {
		VBox vBox = new VBox(0);
		
		vBox.setPrefWidth(100);
		
        state = new Label("DESLIGADO");
        state.setTextFill(Color.web(COLOR_RED));
        pwmValue = new Label();
		adc = new Label();
		functionValue = new Label();
        
        switchOnOff = new ToggleSwitch();
		switchOnOff.setHandler(this);
		
        //VBox socInfo = new VBox(switchOnOff, state, pwmValue, adc, functionValue);
        GridPane socInfo = new GridPane();
        int row = 0;
        socInfo.add(switchOnOff, 0, row, 2, 1);
        row++;
        socInfo.add(state, 0, row, 2, 1);
        row++;
        socInfo.add(new Label("ADC: "), 0, row);
        socInfo.add(adc, 1, row);
		row++;
        socInfo.add(new Label("PWM: "), 0, row);
        socInfo.add(pwmValue, 1, row);
        row++;
        socInfo.add(new Label("F(x): "), 0, row);
        socInfo.add(functionValue, 1, row);
        
		addTitledPanel(vBox, "Informações", socInfo);
        
        graphX = new Label();
        graphY = new Label();
        graphLinesCount = new Label("0 linhas");
        
        GridPane graphInfo = new GridPane();
        
        graphInfo.add(new Label("X:"), 0, 0);
        graphInfo.add(graphX, 1, 0);
        graphInfo.add(new Label("Y:"), 0, 1);
        graphInfo.add(graphY, 1, 1);
        graphInfo.add(graphLinesCount, 0, 2, 2, 1);
        addTitledPanel(vBox, "Gráfico", graphInfo);
        
		//addTitledPanel(vBox, "Sensor de luz", ldr);
		
		
		layoutPane.setLeft(vBox);
	}
	
	private void initCenter() {
		center = new TabPane();
		
		initTabEditFx();
		initTabRT();
		
		layoutPane.setCenter(center);
	}

	private void updateDimensions() {
		//double width, height;
		
		if (!isFullScreen()) {
			Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
			
			prefWidth = primaryScreenBounds.getWidth();
			prefHeight = primaryScreenBounds.getHeight();
		} else {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			
			prefWidth = screenSize.getWidth();
			prefHeight = screenSize.getHeight();
		}
		
		layoutPane.setPrefSize(prefWidth, prefHeight);
		logTextArea.setPrefWidth(prefWidth);
		
		Log.d(TAG, "width: "+ prefWidth);
	}

	private void addTitledPanel(VBox vBox, String title, Node content) {
		TilePane pane = new TilePane();
		pane.getChildren().add(content);
		pane.setPrefWidth(100);
		pane.setPrefHeight(13);
		
		vBox.getChildren().add(new TitledPane(title, pane));
	}

	private void initTabEditFx() {
		HBox hboxTools = new HBox();
		
		addButton(hboxTools, "Limpar gráfico", "clear", "/resources/button-clear.png");
		
        addLineButton = addButton(hboxTools, "Inserir linha", "addline", "/resources/line-chart.png");
        
        sendButton = addButton(hboxTools, "Enviar função", "send", "/resources/send.png");
        sendButton.setDisable(true);
        
        cancelButton = addButton(hboxTools, "Cancelar inserção de linha", "cancelline", "/resources/cancel.png");
        cancelButton.setDisable(true);
		
		exportButton = addButton(hboxTools, "Exportar gráfico", "exportgraph", "/resources/save.png");
        exportButton.setDisable(true);
		
		addButton(hboxTools, "Importar gráfico", "importgraph", "/resources/open.png");
		
		GridPane grid = new GridPane();
		
		double canvasWidth = 1150;//getPrefWidth() - 200;
		double canvasHeight = 500;//getPrefHeight() - 300;
        
        Log.d(TAG, "canvasWidth: " + canvasWidth);
        Log.d(TAG, "canvasHeight: " + canvasHeight);
		
        GraphTicksCanvas graphTicksCanvas = new GraphTicksCanvas(canvasWidth, canvasHeight);
		
        limits = graphTicksCanvas.init();
        
		graphCanvas = new GraphCanvas(this, canvasWidth, canvasHeight);
		graphCanvas.init(limits);
		
		grid.add(graphTicksCanvas, 0, 0);
		grid.add(graphCanvas, 0, 0);
		
		VBox vbox = new VBox();
		vbox.getChildren().addAll(hboxTools, grid);
		
		addTab("Editar função", vbox);
	}
	
	private void initTabRT() {
		xRTAxis = new NumberAxis(0, MAX_DATA_POINTS, 60);
		
		realTimeChart = new LineChart<>(xRTAxis, new NumberAxis(0, 100, 25));
		realTimeChart.setAnimated(false);
		
		rtSeries = new XYChart.Series();
		
		realTimeChart.getData().add(rtSeries);
		
		HBox box = new HBox(realTimeChart);
		box.setPadding(new Insets(100));
		
		addTab("Tempo real", realTimeChart);
	}
	
	@Override
	public void handle(ActionEvent actionEvent) {
		String clickedId;
		
        if (actionEvent.getSource() instanceof ToggleSwitch) {
            try {
                
                Main.getInstance().getClient()
                        .sendData("2," + (switchOnOff.isOn()? "1":"0"));
                
            } catch (IOException ex) {
                Log.e(TAG, "Switching on/off system", ex);
            }
            
            return;
        } if (actionEvent.getSource() instanceof MenuItem) {
			clickedId = ((MenuItem) actionEvent.getSource()).getId();
		} else {
			clickedId = ((Button) actionEvent.getSource()).getId();
		}
		
		switch (clickedId) {
			case "clear":
				clearGraph();
				break;
			case "exit":
				close();
				break;
			case "fs":
				setFullScreen(!isFullScreen());
				updateDimensions();
				break;
			case "addline":
				graphCanvas.addLine();
                cancelButton.setDisable(false);
				break;
            case "cancelline":
				graphCanvas.cancel();
                cancelButton.setDisable(true);
                updateGraphInfo();
				break;
            case "send":
                sendGraph();
                break;
			case "exportgraph":
				exportGraph();
				break;
			case "importgraph":
				importGraph();
				break;
		}
	}
	
	private void addTab(String tabTitle, Node tabContent) {
		Tab tab = new Tab();
		tab.setText(tabTitle);
		tab.setContent(tabContent);
		tab.setClosable(false);
		
		center.getTabs().add(tab);
	}

	private Button addButton(HBox hboxTools, String buttonTooltip, String buttonId, String imagePath) {
		Button button = new Button();
		
		Image image = new Image(MainStage.class.getResource(imagePath).toExternalForm(),
				40, 40, true, true);
		
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(40);
		imageView.setFitHeight(40);
		
		button.setTooltip(new Tooltip(buttonTooltip));
		button.setGraphic(imageView);
		button.setId(buttonId);
		button.setOnAction(this);
		
		hboxTools.getChildren().add(button);
        
        return button;
	}

	private void clearGraph() {
		graphCanvas.clearGraph();
		addLineButton.setDisable(false);
		sendButton.setDisable(true);
		updateGraphInfo();
		exportButton.setDisable(true);
	}
	
    public void graphFinalized() {
        addLineButton.setDisable(true);
        sendButton.setDisable(false);
        cancelButton.setDisable(true);
		exportButton.setDisable(false);
        
        updateGraphInfo();
    }
    
    public void updateGraphCursorPosition(int x, int y) {//, int X_LIMIT_LEFT, double prefW, int Y_LIMIT_TOP, double prefH) {
        Platform.runLater(() -> {
            graphX.setText(String.valueOf((int) ((x - limits.getLeft()) / 1.5)) + "s");
            graphY.setText(String.valueOf((int) (300 - (y - limits.getTop())) / 3) + "%");
        });
    }

    public void updateGraphInfo() {
        graphLinesCount.setText(graphCanvas.getLines().size() + " linhas");
        graphX.setText("");
        graphY.setText("");
    }

    private void sendGraph() {
		String data = getGraphData();
		
        Log.d(TAG, "Data to send: " + data);
		
		try {
			Main.getInstance().getClient().sendData(data);
		} catch (IOException ex) {
			Log.e(TAG, "Data send fails", ex);
		}
        
    }
	
	public String getGraphData() {
		List<Line> lines = graphCanvas.getLines();
        
        double x1, y1, x2, y2;
        Function f;
        
        StringBuilder builder = new StringBuilder(Client.CODE_SEND_FUNTION);
        builder.append(",").append(lines.size() * 4);
        
        for (Line line : lines) {
            x1 = ((line.getX1() - limits.getLeft()) / 1.5);
            x2 = ((line.getX2() - limits.getLeft()) / 1.5);
            
            y1 = (300.0 - (line.getY1() - limits.getTop())) / 3.0;
            y2 = (300.0 - (line.getY2() - limits.getTop())) / 3.0;
            
            f = new Function();
            
            f.setStart(x1);
            f.setEnd(x2);
            
            f.setA((y1 - y2) / (x1 - x2));
            f.setB(y1 - (f.getA() * x1));
            
            builder.append(",").append((int)(f.getStart())).append(',');
            builder.append((int)(f.getEnd())).append(',');
            builder.append((int)(f.getA() * 100)).append(',');
            builder.append((int)(f.getB() * 100));
            
            Log.d(TAG, "f: " + f + " from line " + new Line(x1, y1, x2, y2));
        }
        
        return builder.toString();
	}
	
	private void exportGraph() {
		//Show save file dialog
		File file = showFileChooser(false);

		if (file != null) {
			FileUtils.saveFile(graphCanvas.getExportedData(), file);
		}
	}
	
	private void importGraph() {
		//Show save file dialog
		File file = showFileChooser(true);

		if (file != null) {
			String data = FileUtils.readFile(file);
			
			if (data != null) {
				String[] dataArray = data.split(",");
				
				graphCanvas.clearGraph();
				
				for (int i = 0; i < dataArray.length; i+=4) {
					graphCanvas.addLine(new Line(Integer.parseInt(dataArray[i]),
							Integer.parseInt(dataArray[i + 1]),
							Integer.parseInt(dataArray[i + 2]),
							Integer.parseInt(dataArray[i + 3])));
				}
				
				graphCanvas.drawLines();
				graphFinalized();
			}
		}
	}
    
	private File showFileChooser(boolean open) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialFileName("graph.gph");
		
		String lastDir = Config.getConfig(Config.CONFIG_EXPORT_GRAPH_DIR);
		if (lastDir != null) {
			fileChooser.setInitialDirectory(new File(lastDir).getParentFile());
		}
		
		//Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Graph files (*.gph)", "*.gph");
		fileChooser.getExtensionFilters().add(extFilter);

		//Show save file dialog
		File file = open? fileChooser.showOpenDialog(this) : fileChooser.showSaveDialog(this);
		
		if (file != null) {
			Config.saveConfig(Config.CONFIG_EXPORT_GRAPH_DIR, file.getAbsolutePath());
		}
		
		return file;
	}
    
    public void updateData(String data) {
        Platform.runLater(() -> {
            updateServerData(data);
        });
    }
    
    private void updateServerData(String data) {
		if (state == null) {
			return;
		}
		
        String dataSplit[] = data.split(",");
        
        Log.d(TAG,"dataSplit[1]: " + dataSplit[1]);
        
        if (dataSplit[1].equals("1") && !systemRunning) {
            state.setText("LIGADO");
            state.setTextFill(Color.web(COLOR_GREEN));
            systemRunning = true;
			
			switchOnOff.on();
        } else if (dataSplit[1].equals("0") && systemRunning) {
            state.setText("DESLIGADO");
            state.setTextFill(Color.web(COLOR_RED));
            systemRunning = false;
			
			switchOnOff.off();
        }
        
		double pwmDoubleValue = Float.parseFloat(dataSplit[2]) / 100.0;
		
		adc.setText(String.valueOf(dataSplit[3]));
        pwmValue.setText(String.valueOf(pwmDoubleValue));
        functionValue.setText(String.valueOf(dataSplit[4]));
        
        if (currentGraphPosition > MAX_DATA_POINTS) {	
			rtSeries.getData().remove(0);
		}
		
		Rectangle rect = new Rectangle(0, 0);
		rect.setVisible(false);
		
		XYChart.Data pwmData = new XYChart.Data(currentGraphPosition, pwmDoubleValue);
		pwmData.setNode(rect);
		
		rtSeries.getData().add(pwmData);
		
		if (currentGraphPosition > MAX_DATA_POINTS) {
			xRTAxis.setLowerBound(currentGraphPosition - MAX_DATA_POINTS);
			xRTAxis.setUpperBound(currentGraphPosition);
		}
        
		currentGraphPosition += 2;
    }
	
	public TextArea getLogTextArea() {
		return logTextArea;
	}

	private double getPrefWidth() {
		return prefWidth;
	}

	public double getPrefHeight() {
		return prefHeight;
	}

	
}