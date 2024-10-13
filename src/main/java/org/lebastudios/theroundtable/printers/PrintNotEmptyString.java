package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
import lombok.Setter;

import java.io.IOException;

@Setter
public class PrintNotEmptyString implements IPrinter
{
    private String text;
    private boolean newLine;
    private Style style;

    public PrintNotEmptyString(String text, Style style)
    {
        this(text, !text.isBlank(), style);
    }

    public PrintNotEmptyString(String text, boolean newLine, Style style)
    {
        this.text = text;
        this.newLine = newLine;
        this.style = style;
    }

    public PrintNotEmptyString(String text)
    {
        this(text, true, new Style());
    }

    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        if (!text.isBlank())
        {
            if (newLine)
            {
                escpos.writeLF(style, text);
            }
            else
            {
                escpos.write(style, text);
            }
        }

        return escpos;
    }
}
