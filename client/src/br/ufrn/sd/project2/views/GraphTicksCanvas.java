/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.sd.project2.views;

import br.ufrn.sd.project2.models.Limits;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 *
 * @author dhiogoboza
 */
public class GraphTicksCanvas extends Canvas {
	
	private static final String TAG = "GraphTicksCanvas";
	
    private static final int STEP = 30;
    
	public GraphTicksCanvas(double d, double d1) {
		super(d, d1);
	}
	
	public Limits init() {
        Limits limits = new Limits();
        limits.setLeft(100);
        limits.setTop(100);
                
		GraphicsContext gc = getGraphicsContext2D();
		
        // BORDERS
		gc.setLineWidth(3);
		gc.setStroke(new Color(0.86666666666, 0.86666666666, 0.86666666666, 1));
		gc.strokeLine(0, 0, getWidth(), 0);
		gc.strokeLine(0, 0, 0, getHeight());
		gc.strokeLine(0, getHeight(), getWidth(), getHeight());
		gc.strokeLine(getWidth(), 0, getWidth(), getHeight());
		
        // AXIS
		gc.setLineWidth(2);
		gc.setStroke(Color.DARKGRAY);
		gc.strokeLine(100, 80, 100, getHeight() - (100 - STEP));
		gc.strokeLine(100 - STEP, getHeight() - 100, getWidth() - 100, getHeight() - 100);
        
        // TICKS
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
		
		int stepX = 90;
		
        int maxY = (int) (getHeight() - 100);
        int maxX = (int) 100 + (10 * stepX);
        int y, x = 0;
        int  j = 10;
        
        limits.setBottom(maxY);
        
        for (int i = 100 + STEP; i <= maxY; i+= STEP) {
            y = (int) (getHeight() - i);
            gc.fillText(String.valueOf(j), j == 100? 70:80, y);
            gc.strokeLine(100, y, maxX, y);
            
            j+=10;
        }
        
        //maxY = (int) (getHeight() - 100);
        //maxX = (int) (getWidth() - (100 - STEP));
        j = 1;
        int axisXNumberY = (int) (getHeight() - 70);
        for (int i = 100 + stepX; j <= 10; i+= stepX) {
            x = i;
            gc.fillText(String.valueOf(j), x, axisXNumberY);
            gc.strokeLine(x, 100, x, maxY);
            
            j++;
        }
        
        limits.setRight(x);
        
        return limits;
	}
	
	
}
