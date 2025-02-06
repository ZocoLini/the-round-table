package org.lebastudios.theroundtable.config.data;

import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JSONFile<T extends FileRepresentator>
{
    private static final Map<Class<?>, File> filesCache = new HashMap<>();
    private static final Set<Class<?>> noCacheableClasses = new HashSet<>();
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
        File file;
        
        if (noCacheableClasses.contains(clazz)) 
        {
            file = clazz.getConstructor().newInstance().getFile();
        }
        else
        {
            if (!filesCache.containsKey(clazz))
            {
                filesCache.put(clazz, clazz.getConstructor().newInstance().getFile());
            }

            file = filesCache.get(clazz);
        }

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
    
    public static void addNoCacheableClass(Class<?> clazz)
    {
        noCacheableClasses.add(clazz);
    }
}
