package org.lebastudios.theroundtable;

import javafx.scene.Node;

public record ControllableUIObject<T>(Node root, T controller)
{
}
