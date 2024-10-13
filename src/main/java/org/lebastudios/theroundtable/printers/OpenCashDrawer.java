package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;

import java.io.IOException;

public class OpenCashDrawer implements IPrinter
{
    private static final byte[] OPEN_CASH_DRAWER_DEFAULT_COMMAND 
            = new byte[]{0x1B, 0x70, 0x00, 0x19, (byte) 0xFA};
    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        escpos.write(OPEN_CASH_DRAWER_DEFAULT_COMMAND, 0, OPEN_CASH_DRAWER_DEFAULT_COMMAND.length);
        return escpos;
    }
}
