package org.lebastudios.theroundtable.config.data;

import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.io.FileWriter;

public class JSONFile<T extends FileRepresentator>
{
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
        var file = clazz.getConstructor().newInstance().getFile();

        if (!file.exists()) return clazz.getConstructor().newInstance();

        return new GsonBuilder().create().fromJson(new FileReader(file), clazz);
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
