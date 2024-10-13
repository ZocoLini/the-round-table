package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;

import java.io.IOException;

public class LineFiller implements IPrinter
{
    private final String fillString;
    private final Style.FontSize size;

    public LineFiller(String fillString)
    {
        this(fillString, Style.FontSize._1);
    }

    public LineFiller(String fillString, Style.FontSize size)
    {
        this.fillString = fillString;
        this.size = size;
    }

    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        var charsPerLine = Printer80.characterLimitPerLine(size);

        var text = fillString.repeat(Math.ceilDiv(charsPerLine, fillString.length()));
        escpos.writeLF(new Style().setFontSize(size, size), text);
        return escpos;
    }
}
