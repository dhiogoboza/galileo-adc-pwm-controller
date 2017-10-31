/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.sd.project2.views;

import br.ufrn.sd.project2.Main;
import br.ufrn.sd.project2.models.Limits;
import br.ufrn.sd.project2.models.Line;
import br.ufrn.sd.project2.stages.MainStage;
import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

/**
 *
 * @author dhiogoboza
 */
public class GraphCanvas extends GridPane implements EventHandler<MouseEvent> {
	private static final String TAG = "GraphCanvas";
	
    private final List<Line> lines = new ArrayList<>();
    
    private int X_LIMIT_LEFT;
    private int X_LIMIT_RIGHT;
    private int Y_LIMIT_TOP;
    private int Y_LIMIT_BOTTOM;
    
    private final MainStage mainStage;
    
	private double previousX = -1, previousY;
	private boolean addline = false;
    private final double prefH;
    private final double prefW;
    private GraphicsContext mainCanvas;
    private GraphicsContext sketchCanvas;
    
    private final EventHandler<? super MouseEvent> mouseMovedListener = new EventHandler<MouseEvent>() {
        int roundX, roundY;
		
        @Override
        public void handle(MouseEvent mouseEvent) {
            if (addline) {
                roundX = Line.round(mouseEvent.getX());
                roundY = Line.round(mouseEvent.getY());
                
                sketchCanvas.clearRect(0, 0, prefW, prefH);
                
                if (previousX == -1) {
                    if (roundY >= Y_LIMIT_TOP && roundY <= Y_LIMIT_BOTTOM) {
                        sketchCanvas.fillOval(98, roundY - 2, 4, 4);
                    }
                } else {
                    sketchCanvas.strokeLine(previousX, previousY,
                            roundX < X_LIMIT_RIGHT? roundX : X_LIMIT_RIGHT,
                            roundY > Y_LIMIT_TOP? (roundY < Y_LIMIT_BOTTOM? roundY:Y_LIMIT_BOTTOM) : Y_LIMIT_TOP);
                }
                
                mainStage.updateGraphCursorPosition(roundX, roundY);
            }
        }
    };

	public GraphCanvas(MainStage mainStage, double w, double h) {
        setPrefWidth(w);
		setPrefHeight(h);
        
        this.mainStage = mainStage;
        this.prefW = w;
        this.prefH = h;
	}
	
	public void init(Limits limits) {
        X_LIMIT_LEFT = limits.getLeft();
        X_LIMIT_RIGHT = limits.getRight();//(int) (prefW - 100);
        Y_LIMIT_TOP = limits.getTop();
        Y_LIMIT_BOTTOM = limits.getBottom();//(int) (prefH - 100);
    
        Canvas main = new Canvas(prefW, prefH);
        Canvas sketch = new Canvas(prefW, prefH);
        
        mainCanvas = main.getGraphicsContext2D();
        sketchCanvas = sketch.getGraphicsContext2D();
        
        add(main, 0, 0);
        add(sketch, 0, 0);
        
		setOnMouseClicked(this);
        setOnMouseMoved(mouseMovedListener);
	}
	
	@Override
	public void handle(MouseEvent mouseEvent) {
		if (addline) {
            int roundX = Line.round(mouseEvent.getX());
            int roundY = Line.round(mouseEvent.getY());
            
            if (previousX == -1) {
                mainCanvas.fillOval(98, roundY - 2, 4, 4);
            
                previousX = 100;
                previousY = roundY;
            } else {
                if (roundX > previousX) {
                    roundX = roundX < X_LIMIT_RIGHT? roundX : X_LIMIT_RIGHT;
                    roundY = roundY > Y_LIMIT_TOP? (roundY < Y_LIMIT_BOTTOM? roundY:Y_LIMIT_BOTTOM) : Y_LIMIT_TOP;
                    
                    mainCanvas.fillOval(roundX - 2, roundY - 2, 4, 4);
                    lines.add(new Line(previousX, previousY, roundX, roundY));
                    mainStage.updateGraphInfo();

                    mainCanvas.setLineWidth(2);
                    mainCanvas.strokeLine(previousX, previousY, roundX, roundY);

                    previousX = roundX;
                    previousY = roundY;

                    if (roundX == X_LIMIT_RIGHT) {
                        mainStage.graphFinalized();
                        previousX = -1;
                        cancel();
                    }
                    //}
                } else {
                    Main.getInstance().showToast("A coordenada X deve ser maior que a do ponto anterior");
                }
			}
		}
	}

	public void clearGraph() {
        setCursor(Cursor.DEFAULT);
		mainCanvas.clearRect(0, 0, prefW, prefH);
        sketchCanvas.clearRect(0, 0, prefW, prefH);
        lines.clear();
		previousX = -1;
		addline = false;
	}

	public void addLine() {
		setCursor(Cursor.CROSSHAIR);
        sketchCanvas.clearRect(0, 0, prefW, prefH);
		addline = true;
	}
    
	public void addLine(Line line) {
		lines.add(line);
	}
	
    public void cancel() {
        setCursor(Cursor.DEFAULT);
        sketchCanvas.clearRect(0, 0, prefW, prefH);
		addline = false;
    }

    public List<Line> getLines() {
        return lines;
    }

	public String getExportedData() {
		StringBuilder linesBuilder = new StringBuilder();
		
		for (Line line: lines) {
			linesBuilder.append(line.getX1()).append(",")
					.append(line.getY1()).append(",")
					.append(line.getX2()).append(",")
					.append(line.getY2()).append(",");
		}
		
		String string = linesBuilder.toString();
		
		return string.substring(0, string.length() -1);
	}
	
	public void drawLines() {
		mainCanvas.setLineWidth(2);
		mainCanvas.fillOval(98, lines.get(0).getY1() - 2, 4, 4);
		
		for (Line line: lines) {
			mainCanvas.strokeLine(line.getX1(), line.getY1(), line.getX2(), line.getY2());
			mainCanvas.fillOval(line.getX2() - 2, line.getY2() - 2, 4, 4);
		}
		
		mainStage.updateGraphInfo();
	}
	
}
