package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;

import java.io.IOException;

public class PrintWrappeableText implements IPrinter
{
    private final String text;
    private final Style style;

    public PrintWrappeableText(String text)
    {
        this(text, new Style());
    }

    public PrintWrappeableText(String text, Style style)
    {
        this.text = text;
        this.style = style;
    }

    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        style.setFontSize(Style.FontSize._1, Style.FontSize._1);
        var charsPerLine = Printer80.characterLimitPerLine(Style.FontSize._1);

        var start = 0;
        var validPoint = 0;

        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == ' ')
            {
                validPoint = i - 1;
            }

            if (validPoint - start >= charsPerLine)
            {
                escpos.writeLF(style, text.substring(start, validPoint));
                start = validPoint + 2;
            }
        }

        return escpos;
    }
}
