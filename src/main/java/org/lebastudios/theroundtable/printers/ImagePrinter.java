package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.image.*;

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
        
        if (image.getWidth() > Printer80.MAX_IMG_WIDTH)
        {
            BufferedImage scaled = new BufferedImage(
                    Printer80.MAX_IMG_WIDTH,
                    image.getHeight() * Printer80.MAX_IMG_WIDTH / image.getWidth(),
                    image.getType()
            );
            Graphics2D g2d = scaled.createGraphics();
            g2d.drawImage(image, -50, 0, 700, 700, null);
            g2d.dispose();

            var escposImage = new EscPosImage(new CoffeeImageImpl(scaled), bitonalAlgorithm);
            escpos.write(imageWrapperInterface, escposImage);
        }
        else
        {
            var escposImage = new EscPosImage(new CoffeeImageImpl(image), bitonalAlgorithm);
            escpos.write(imageWrapperInterface, escposImage);
        }

        return escpos;
    }
}
