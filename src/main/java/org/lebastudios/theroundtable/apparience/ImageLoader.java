package org.lebastudios.theroundtable.apparience;

import javafx.scene.image.Image;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.plugins.PluginLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader
{
    private static final Map<String, Image> loadedIcons = new HashMap<>();
    private static final Map<String, Image> loadedTextures = new HashMap<>();
    private static final Map<String, Image> loadedSavedImages = new HashMap<>();

    public static Image getIcon(String iconName)
    {
        Image image = loadedIcons.get(iconName);

        if (image != null) return image;

        image = loadImage(iconName, ImageType.ICON);

        if (image == null) return getIcon("icon-not-found.png");

        loadedIcons.put(iconName, image);
        return image;
    }

    public static Image getTexture(String textureName)
    {
        Image image = loadedTextures.get(textureName);

        if (image != null) return image;

        image = loadImage(textureName, ImageType.TEXTURE);

        if (image == null) return getTexture("texture-not-found.png");
        ;

        loadedTextures.put(textureName, image);
        return image;
    }

    public static Image getSavedImage(String filePath)
    {
        Image image = loadedSavedImages.get(filePath);

        if (image != null) return image;

        image = loadSavedImage(new File(filePath));

        if (image == null) return getIcon("blank-image.png");

        loadedSavedImages.put(filePath, image);
        return image;
    }

    @SneakyThrows
    private static Image loadImage(String iconName, ImageType imageType)
    {
        for (var individualClass : PluginLoader.getRessourcesObjects())
        {
            var folder = imageType == ImageType.ICON ? "icons/" : "textures/";

            var resource = individualClass.getClass().getResourceAsStream(folder + iconName);

            if (resource == null) continue;

            return new Image(resource);
        }

        return null;
    }

    public static File saveImageInSpecialFolder(File imgFile)
    {
        try
        {
            var image = new Image(new FileInputStream(imgFile));
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("This file can't be loaded as an image.");
        }

        var directory = new File(SavedImagesDirectory());
        var fileExtension = imgFile.getName().substring(imgFile.getName().lastIndexOf("."));
        if (!directory.exists()) directory.mkdir();
        var savedImgPath = directory + "/" + System.currentTimeMillis() + fileExtension;

        // Copy the image to the saved-images folder
        try
        {
            Files.copy(imgFile.toPath(), Paths.get(savedImgPath), StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("This file can't be saved as an image.");
        }

        return new File(savedImgPath);
    }

    public static String SavedImagesDirectory()
    {
        var dir = TheRoundTableApplication.getUserDirectory() + "/saved-images";

        var file = new File(dir);

        if (!file.exists()) file.mkdirs();

        return dir;
    }

    private static Image loadSavedImage(File imgFile)
    {
        try
        {
            FileInputStream resource = new FileInputStream(imgFile);
            return new Image(resource);
        }
        catch (FileNotFoundException e)
        {
            return null;
        }
    }

    private enum ImageType
    {
        ICON,
        TEXTURE
    }
}
