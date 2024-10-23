package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.image.BitImageWrapper;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.ImageWrapperInterface;
import org.lebastudios.theroundtable.config.data.EstablishmentConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;

import java.io.File;
import java.io.IOException;

public final class DefaultHeaderPrinter implements IPrinter
{
    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        var establishmentDat = new JSONFile<>(EstablishmentConfigData.class).get();
        var printerConfig = new JSONFile<>(PrintersConfigData.class).get();

        var logoFile = new File(establishmentDat.logoImgPath);

        escpos.feed(1);

        if (!printerConfig.hideEstablishmentLogo && logoFile.isFile())
        {
            ImageWrapperInterface<?> wrapper = new BitImageWrapper();
            wrapper.setJustification(EscPosConst.Justification.Center);

            BitonalThreshold bitonalThreshold = new BitonalThreshold(170);

            new ImagePrinter(logoFile, wrapper, bitonalThreshold).print(escpos);
            escpos.feed(1);
        }

        new PrintNotEmptyString(establishmentDat.name, Styles.TITLE).print(escpos);
        new PrintNotEmptyString(establishmentDat.id, Styles.SUBTITLE).print(escpos);

        escpos.feed(1);

        
        PrintNotEmptyString printer = new PrintNotEmptyString(establishmentDat.address, Styles.CENTERED);
        printer.setNewLine(false);
        
        printer.print(escpos);
        escpos.write(" ");
        
        printer.setText(establishmentDat.city);
        printer.print(escpos);
        escpos.write(" ");
        
        printer.setText(establishmentDat.zipCode);
        printer.setNewLine(true);
        printer.print(escpos);
        
        new PrintNotEmptyString(establishmentDat.phone, Styles.CENTERED).print(escpos);

        return escpos;
    }
}
