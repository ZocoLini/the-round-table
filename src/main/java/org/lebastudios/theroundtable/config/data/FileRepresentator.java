package org.lebastudios.theroundtable.config.data;

import java.io.File;

public interface FileRepresentator
{
    File getFile();

    default void save()
    {
        new JSONFile<>(this).save();
    }
    
    default void load() {
        
    }
}
