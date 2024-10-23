package org.lebastudios.theroundtable.config.data;

import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import org.hibernate.cache.CacheException;
import org.lebastudios.theroundtable.events.UserEvents;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class JSONFile<T extends FileRepresentator>
{
    private static final Map<Class<?>, Object> cache = new HashMap<>();

    static {
        UserEvents.OnAccountLogOut.addListener(_ -> cache.clear());
    }
    
    private final T data;

    public JSONFile(Class<T> clazz)
    {
        data = load(clazz);
    }

    @SneakyThrows
    private <T extends FileRepresentator> T load(Class<T> clazz)
    {
        if (cache.containsKey(clazz))
        {
            var cached = cache.get(clazz);
            
            if (cached.getClass().equals(clazz)) return (T) cache.get(clazz);

            throw new CacheException("The cached object is not of the same class as the requested one.");
        }

        var file = clazz.getConstructor().newInstance().getFile();

        if (!file.exists()) return clazz.getConstructor().newInstance();

        var fileContent = new GsonBuilder().create().fromJson(new FileReader(file), clazz);

        cache.put(clazz, fileContent);

        return fileContent;
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
