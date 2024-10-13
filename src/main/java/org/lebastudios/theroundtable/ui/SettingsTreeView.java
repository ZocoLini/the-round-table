package org.lebastudios.theroundtable.ui;

import javafx.scene.control.TreeView;

public class SettingsTreeView extends TreeView<String>
{
    public SettingsTreeView()
    {
        this(new TreeIconItem("Root", "settings.png"));
    }

    public SettingsTreeView(TreeIconItem root)
    {
        super(root);

        this.setStyle("-fx-border-color: none; -fx-border-style: none; -fx-background-color: transparent;");

        this.setCellFactory(treeView -> new SettingsTreeCell());

        this.setShowRoot(false);
    }
}
