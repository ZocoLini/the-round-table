package org.lebastudios.theroundtable.ui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class LabeledIconButton extends VBox
{
    private final Label label;
    private final IconView icon;
    private final EventHandler<MouseEvent> handler;
    
    public LabeledIconButton(String text, IconView icon, EventHandler<MouseEvent> handler)
    {
        super();

        this.label = new Label(text);
        this.icon = icon;
        this.handler = handler;
        
        label.setWrapText(true);
        
        this.setOnMouseClicked(handler);
        this.setSpacing(10);
        this.setPadding(new Insets(10));
        this.getStyleClass().add("button");
        this.setAlignment(Pos.CENTER);
        
        this.getChildren().addAll(this.icon, this.label);
        
    }
}
