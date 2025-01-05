package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.Style;

public class Printer80
{
    public static final int MAX_IMG_WIDTH = 400;

    public static int characterLimitPerLine(Style.FontSize fontSize)
    {
        return switch (fontSize)
        {
            case _1 -> 48;
            case _2 -> 24;
            case _3 -> 16;
            case _4 -> 12;
            case _5 -> 9;
            case _6 -> 8;
            case _7, _8 -> 6;
        };
    }
}
