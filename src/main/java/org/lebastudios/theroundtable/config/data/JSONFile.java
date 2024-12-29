package org.lebastudios.theroundtable.config.data;

import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class JSONFile<T extends FileRepresentator>
{
    private static final Map<String, File> filesCache = new HashMap<>();
    private final T data;

    public JSONFile(Class<T> clazz)
    {
        data = load(clazz);
    }

    public JSONFile(T data)
    {
        this.data = data;
    }

    @SneakyThrows
    private <T extends FileRepresentator> T load(Class<T> clazz)
    {
        if (!filesCache.containsKey(clazz.getName())) 
        {
            filesCache.put(clazz.getName(), clazz.getConstructor().newInstance().getFile());
        }
        
        var file = filesCache.get(clazz.getName());

        if (!file.exists()) return clazz.getConstructor().newInstance();

        try (var reader = new FileReader(file))
        {
            return new GsonBuilder().create().fromJson(reader, clazz);
        }
    }

    public T get()
    {
        return data;
    }

    @SneakyThrows
    public void save()
    {
        var file = data.getFile();

        if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
        if (!file.exists()) file.createNewFile();

        var fileContent = new GsonBuilder().setPrettyPrinting().create().toJson(data);

        try (var writer = new FileWriter(file))
        {
            writer.write(fileContent);
        }
    }
}
