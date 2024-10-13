package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;

import java.io.IOException;

public interface IPrinter
{
    EscPos print(EscPos escpos) throws IOException;
}
