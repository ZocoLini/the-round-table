package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;

import java.io.IOException;

public class OpenCashDrawer implements IPrinter
{
    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        byte[] command = new JSONFile<>(PrintersConfigData.class).get().getOpenCashDrawerCommand();
        
        escpos.write(command, 0, command.length);
        return escpos;
    }
}
