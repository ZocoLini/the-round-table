package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;

import java.io.IOException;

public class InLinePrinter implements IPrinter
{
    private String leftText = "";
    private String rightText = "";
    private Style.FontSize size = Style.FontSize._1;

    public InLinePrinter(Style.FontSize size)
    {
        this.size = size;
    }

    public InLinePrinter() {}

    public InLinePrinter concatLeft(String text, int spacesReserved)
    {
        return concatLeft(text, spacesReserved, EscPosConst.Justification.Left_Default);
    }

    public InLinePrinter concatLeft(String text, int spacesReserved, EscPosConst.Justification justification)
    {
        text = formatText(text, spacesReserved, justification);

        leftText += text;

        return this;
    }

    private static String formatText(String text, int spacesReserved, EscPosConst.Justification justification)
    {
        if (text.length() > spacesReserved) return text.substring(0, spacesReserved);

        final var blankSpaces = " ".repeat(spacesReserved - text.length());
        return switch (justification)
        {
            case EscPosConst.Justification.Left_Default -> text + blankSpaces;
            case EscPosConst.Justification.Center -> blankSpaces.substring(0, Math.floorDiv(blankSpaces.length(), 2))
                    + text
                    + blankSpaces.substring(Math.ceilDiv(blankSpaces.length(), 2));
            case EscPosConst.Justification.Right -> blankSpaces + text;
        };

    }

    public InLinePrinter concatLeft(String text)
    {
        return concatLeft(text, text.length(), EscPosConst.Justification.Left_Default);
    }

    public InLinePrinter concatRight(String text, int spacesReserved)
    {
        return concatRight(text, spacesReserved, EscPosConst.Justification.Right);
    }

    public InLinePrinter concatRight(String text, int spacesReserved, EscPosConst.Justification justification)
    {
        text = formatText(text, spacesReserved, justification);

        rightText += text;

        return this;
    }

    public InLinePrinter concatRight(String text)
    {
        return concatRight(text, text.length(), EscPosConst.Justification.Right);
    }

    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        int availableSpaces = Printer80.characterLimitPerLine(size);

        int emptySpaces = availableSpaces - leftText.length() - rightText.length();

        if (emptySpaces < 0)
        {
            throw new IllegalStateException("Text is too long for the available spaces");
        }

        String text = leftText + " ".repeat(emptySpaces) + rightText;

        if (text.length() > availableSpaces) text = text.substring(0, availableSpaces);

        escpos.writeLF(new Style().setFontSize(size, size), text);

        return escpos;
    }
}
