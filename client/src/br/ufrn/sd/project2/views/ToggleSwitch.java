package br.ufrn.sd.project2.views;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * https://gist.github.com/TheItachiUchiha/12e40a6f3af6e1eb6f75
 * 
 * @author dhiogoboza
 */
public class ToggleSwitch extends HBox {
	
	private final Label label = new Label();
	private final Button button = new Button();
	
	private final SimpleBooleanProperty switchedOn = new SimpleBooleanProperty(false);
	
    private EventHandler<ActionEvent> handler;
	
	public ToggleSwitch() {
		init();
		switchedOn.addListener((a,b,c) -> {
			if (c) {
				label.setText("ON");
				setStyle("-fx-background-color: green;");
				label.toFront();
			} else {
				label.setText("OFF");
				setStyle("-fx-background-color: grey;");
				button.toFront();
			}
		});
	}
	
	private void init() {
		
		label.setText("OFF");
		
		getChildren().addAll(label, button);	
		button.setOnAction((t) -> {
			onChange();
		});
		
		label.setOnMouseClicked((t) -> {
			onChange();
		});
		
		setStyle();
		bindProperties();
	}
	
	private void setStyle() {
		//Default Width
		setWidth(80);
		label.setAlignment(Pos.CENTER);
		setStyle("-fx-background-color: grey; -fx-text-fill:black; -fx-background-radius: 4;");
		setAlignment(Pos.CENTER_LEFT);
	}
	
	private void bindProperties() {
		label.prefWidthProperty().bind(widthProperty().divide(2));
		label.prefHeightProperty().bind(heightProperty());
		button.prefWidthProperty().bind(widthProperty().divide(2));
		button.prefHeightProperty().bind(heightProperty());
	}
    
    public boolean isOn() {
        return switchedOn.get();
    }
	
	public void on() {
		switchedOn.set(true);
	}
	
	public void off() {
		switchedOn.set(false);
	}

	private void onChange() {
		switchedOn.set(!switchedOn.get());
        
        if (handler != null) {
            ActionEvent event = new ActionEvent(this, null);
            handler.handle(event);
        }
	}

    public void setHandler(EventHandler<ActionEvent> handler) {
        this.handler = handler;
    }

}