package org.lebastudios.theroundtable.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;

public class TitleBuilder
{
    private String iconName;
    private String text;
    private String weight = "normal";
    private int size = 23;
    
    public TitleBuilder(String text, String iconName)
    {
        this.text = text;
        this.iconName = iconName;
    }
    
    public TitleBuilder(String text)
    {
        this(text, "");
    }

    public TitleBuilder()
    {
        this("", "");
    }

    public TitleBuilder setText(String text)
    {
        this.text = text;
        return this;
    }

    public TitleBuilder setIconName(String iconName)
    {
        this.iconName = iconName;
        return this;
    }

    public TitleBuilder setWeight(String weight)
    {
        this.weight = weight;
        return this;
    }

    public TitleBuilder setSize(int size)
    {
        this.size = size;
        return this;
    }

    public Node build()
    {
        Label label = new Label(text, new IconView(iconName));
        label.setStyle(String.format("-fx-font-weight: %s; -fx-font-size: %d", weight, size));
        return label;
    }
}
