package org.lebastudios.theroundtable.ui;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class LabeledTextField extends HBox
{
    private final Label label = new Label();
    private final TextField textField = new TextField();
    public String labelText;

    public LabeledTextField()
    {
        this("");
    }

    public LabeledTextField(String labelText)
    {
        this.labelText = labelText;

        this.getChildren().addAll(label, textField);

        updateNode();
    }

    private void updateNode()
    {
        label.setText(labelText);
    }

    public void setLabelText(String text)
    {
        labelText = text;
        updateNode();
    }

    public String getText()
    {
        return textField.getText();
    }
}
