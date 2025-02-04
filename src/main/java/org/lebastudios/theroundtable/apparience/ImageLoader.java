package org.lebastudios.theroundtable.apparience;

import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ImageLoader
{
    private static final Map<String, WeakReference<Image>> loadedIcons = new WeakHashMap<>();
    private static final Map<String, Image> loadedTextures = new HashMap<>();
    private static final Map<String, WeakReference<Image>> loadedSavedImages = new WeakHashMap<>();

    private static final ConcurrentHashMap<String, Object> lockMap = new ConcurrentHashMap<>();
    
    public static Image getIcon(String iconName)
    {
        Object lock = lockMap.computeIfAbsent(iconName, _ -> new Object());
        
        synchronized (lock)
        {
            Image image = getImage(iconName, loadedIcons);
            
            if (image != null) return image;

            image = loadImage(iconName, ImageType.ICON);

            if (image == null) image = getIcon("icon-not-found.png");

            synchronized (loadedIcons)
            {
                loadedIcons.put(iconName, new WeakReference<>(image));
            }

            return image;
        }
    }

    public synchronized static Image getTexture(String textureName)
    {
        Image image = loadedTextures.get(textureName);

        if (image != null) return image;

        image = loadImage(textureName, ImageType.TEXTURE);

        if (image == null) return getTexture("texture-not-found.png");

        loadedTextures.put(textureName, image);
        return image;
    }

    public static Image getSavedImage(String filePath)
    {
        Image image;
        synchronized (loadedSavedImages)
        {
            image = getImage(filePath, loadedSavedImages);
        }
        if (image != null) return image;

        try (FileInputStream resource = new FileInputStream(filePath))
        {
            image = new Image(resource, 100, 100, true, true);
        }
        catch (IOException _) {}
        
        if (image == null) return getIcon("blank-image.png");

        synchronized (loadedSavedImages)
        {
            loadedSavedImages.put(filePath, new WeakReference<>(image));
        }
        return image;
    }

    private static Image getImage(String filePath, Map<String, WeakReference<Image>> loadedSavedImages)
    {
        final WeakReference<Image> reference = loadedSavedImages.get(filePath);

        if (reference != null) return reference.get();
        return null;
    }

    @SneakyThrows
    private synchronized static Image loadImage(String iconName, ImageType imageType)
    {
        for (var individualClass : PluginLoader.getRessourcesObjects())
        {
            var folder = imageType == ImageType.ICON ? "icons/" : "textures/";

            try (var resource = individualClass.getClass().getResourceAsStream(folder + iconName))
            {
                if (resource == null) continue;

                return new Image(resource);
            }
        }

        return null;
    }

    public static File saveImageInSpecialFolder(File imgFile)
    {
        try (var inputStream = new FileInputStream(imgFile))
        {
            var image = new Image(inputStream);
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

    private enum ImageType
    {
        ICON,
        TEXTURE
    }
    
    private static boolean imageChooserIsOpen = false;
    
    public synchronized static ImageChooserResult showImageChooser(Window owner)
    {
        if (imageChooserIsOpen) return null;
        
        var fileChooser = new FileChooser();

        fileChooser.setTitle(LangFileLoader.getTranslation("title.imagechooser"));
        fileChooser.setInitialDirectory(new File(ImageLoader.SavedImagesDirectory()));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg")
        );

        imageChooserIsOpen = true;
        var imageFile = fileChooser.showOpenDialog(owner);
        imageChooserIsOpen = false;
        
        if (imageFile == null) return null;
        
        try (var inputStream = new FileInputStream(imageFile))
        {
            var image = new Image(inputStream);
            if (image.isError()) return null;
            
            return new ImageChooserResult(imageFile, image);
        }
        catch (IOException e)
        {
            return null;
        }
    }    
    
    public record ImageChooserResult(File imageFile, Image image) { }
}
