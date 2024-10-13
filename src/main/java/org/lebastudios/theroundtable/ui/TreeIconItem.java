package org.lebastudios.theroundtable.ui;

import javafx.scene.control.TreeItem;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.lebastudios.theroundtable.ControllableUIObject;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.config.SettingsPaneController;

import java.util.function.Supplier;

public class TreeIconItem extends TreeItem<String>
{
    private String iconName;
    @Setter @Getter private Supplier<ControllableUIObject<SettingsPaneController>> action = () -> null;

    public TreeIconItem(String value, String iconName,
            Supplier<ControllableUIObject<SettingsPaneController>> action)
    {
        super(value);
        this.iconName = iconName;

        this.action = action;

        updateIcon();
    }

    private void updateIcon()
    {
        var imageView = new ImageView(ImageLoader.getIcon(iconName));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        this.setGraphic(imageView);
    }

    public TreeIconItem(String value, String iconName)
    {
        super(value);
        this.iconName = iconName;

        updateIcon();
    }

    public TreeIconItem()
    {
    }

    public void setIconName(String iconName)
    {
        this.iconName = iconName;

        updateIcon();
    }
}
