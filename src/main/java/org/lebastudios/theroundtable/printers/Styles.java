package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.escpos.image.Bitonal;
import com.github.anastaciocintra.escpos.image.BitonalThreshold;
import com.github.anastaciocintra.escpos.image.RasterBitImageWrapper;
import com.github.anastaciocintra.output.PrinterOutputStream;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.TheRoundTableApplication;

import javax.print.PrintService;
import java.io.File;

public class Styles
{
    public static final Style BOLD = new Style().setBold(true);
    public static final Style CENTERED = new Style().setJustification(EscPosConst.Justification.Center);
    public static final Style SUBTITLE = new Style().setBold(true).setFontSize(Style.FontSize._1, Style.FontSize._1)
            .setJustification(EscPosConst.Justification.Center);
    public static final Style TITLE = new Style().setBold(true).setFontSize(Style.FontSize._2, Style.FontSize._2)
            .setJustification(EscPosConst.Justification.Center);
    public static final Style RIGHT = new Style().setJustification(EscPosConst.Justification.Right);
    public static final Style LEFT = new Style().setJustification(EscPosConst.Justification.Left_Default);

    @SneakyThrows
    public static void main(String[] args)
    {
        PrintService printService = PrinterManager.getInstance().getDefaultPrintService();
        EscPos escpos = new EscPos(new PrinterOutputStream(printService));

        new LineFiller("=").print(escpos);
        new LineFiller(";").print(escpos);
        new LineFiller("-").print(escpos);
        new LineFiller("_").print(escpos);
        new LineFiller("__").print(escpos);
        new LineFiller("___").print(escpos);
        new LineFiller("____").print(escpos);

        Bitonal algorithm = new BitonalThreshold(170);
        RasterBitImageWrapper imageWrapper = new RasterBitImageWrapper();
        imageWrapper.setJustification(EscPosConst.Justification.Center);

        new ImagePrinter(
                new File(TheRoundTableApplication.class.getResource("icons/the-round-table-logo.png").toURI())
                , imageWrapper, algorithm)
                .print(escpos);

        new InLinePrinter().concatLeft("123", 4, EscPosConst.Justification.Center)
                .concatLeft("Tomate", 24, EscPosConst.Justification.Center)
                .concatRight("12.00 €", 8, EscPosConst.Justification.Center).print(escpos);

        new InLinePrinter().concatLeft("1", 4, EscPosConst.Justification.Center)
                .concatLeft("Coca-Cola", 24, EscPosConst.Justification.Center)
                .concatRight("2.10 €", 8, EscPosConst.Justification.Center).print(escpos);

        new InLinePrinter().concatLeft("3", 4, EscPosConst.Justification.Center)
                .concatLeft("Zanahorias", 24, EscPosConst.Justification.Center)
                .concatRight("9.90 €", 8, EscPosConst.Justification.Center).print(escpos);

        escpos.feed(4).cut(EscPos.CutMode.PART);

        escpos.close();
    }
}

