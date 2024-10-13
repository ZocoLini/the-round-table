package org.lebastudios.theroundtable.ui;

import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseEvent;
import org.lebastudios.theroundtable.config.ConfigStageController;

public class SettingsTreeCell extends TreeCell<String>
{
    public SettingsTreeCell()
    {
        super();

        this.setOnMouseClicked(this::onClick);
        this.setOnTouchPressed(_ -> onClick(null));
    }

    private void onClick(MouseEvent event)
    {
        if (this.getTreeItem() == null) return;

        if (this.getTreeItem() instanceof TreeIconItem item)
        {
            var newPane = item.getAction().get();
            if (newPane == null) return;

            ConfigStageController.configStageRoot.controller().swapMainPane(newPane);
        }
    }

    @Override
    protected void updateItem(String item, boolean empty)
    {
        super.updateItem(item, empty);

        if (empty || item == null)
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            setText(item);
            setGraphic(this.getTreeItem().getGraphic());
        }
    }
}
