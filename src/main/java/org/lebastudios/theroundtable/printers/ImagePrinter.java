package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.*;
import lombok.Setter;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePrinter implements IPrinter
{
    private final File imgFile;
    private final ImageWrapperInterface<?> imageWrapperInterface;
    private final Bitonal bitonalAlgorithm;
    @Setter private int width = Printer80.MAX_IMG_WIDTH;
    
    public ImagePrinter(File imgFile)
    {
        this(imgFile, new RasterBitImageWrapper(), new BitonalThreshold(170));
    }

    public ImagePrinter(File imgFile, ImageWrapperInterface<?> imageWrapperInterface, Bitonal bitonalAlgorithm)
    {
        this.imgFile = imgFile;
        this.imageWrapperInterface = imageWrapperInterface;
        this.bitonalAlgorithm = bitonalAlgorithm;
    }

    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        BufferedImage image;

        try
        {
            image = ImageIO.read(imgFile);
        }
        catch (IIOException exception)
        {
            System.err.println("Error reading image file while trying to print it: " + imgFile);
            return escpos;
        }

        float realHeight = image.getHeight();
        float realWidth = image.getWidth();
        
        float scale = width / Math.max(realWidth, realHeight);
        
        float newWidth = realWidth * scale;
        float newHeight = realHeight * scale;
        
        BufferedImage scaled = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = scaled.createGraphics();
        g2d.drawImage(image, 0, 0, (int) newWidth, (int) newHeight, null);
        g2d.dispose();

        var escposImage = new EscPosImage(new CoffeeImageImpl(scaled), bitonalAlgorithm);
        escpos.write(imageWrapperInterface, escposImage);

        return escpos;
    }
}
