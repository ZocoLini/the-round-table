package org.lebastudios.theroundtable.language;

import org.lebastudios.theroundtable.plugins.PluginLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LangFileLoader
{
    private static final Map<String, String> translations = new HashMap<>();

    public static void loadLang(String lang, String country)
    {
        translations.clear();

        for (var resourceObject : PluginLoader.getRessourcesObjects())
        {
            var resource = resourceObject.getClass().getResourceAsStream("languagesData.csv");

            if (resource == null)
            {
                System.err.println("No language file found in " + resourceObject.getClass().getName());
                continue;
            }

            computeTranslations(lang, country, resource);
        }
    }

    private static void computeTranslations(String lang, String country, InputStream fileToCompute)
    {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(fileToCompute)))
        {
            int index = searchColumn(reader.readLine(), lang, country);

            String line;
            while ((line = reader.readLine()) != null)
            {
                var columns = line.split(",");

                translations.put(
                        columns[0],
                        index == -1 ? columns[1] : columns[index]
                );
            }
        }
        catch (IOException exception)
        {
            System.err.println("Error reading file languagesData.csv");
        }
    }

    private static int searchColumn(String header, String langDesired, String countryDesired)
    {
        String[] columns = header.split(",");
        var langCode = langDesired + "_" + countryDesired;
        int alternativeIndex = -1;

        for (int i = 1; i < columns.length; i++)
        {
            if (columns[i].equals(langCode)) return i;
            if (columns[i].startsWith(langDesired)) alternativeIndex = i;
        }

        return alternativeIndex;
    }

    public static String getTranslation(String key)
    {
        return translations.getOrDefault(key, key);
    }
}
