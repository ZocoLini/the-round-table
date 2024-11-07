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
}

