package org.lebastudios.theroundtable.config.data;

import lombok.Getter;
import lombok.Setter;
import org.lebastudios.theroundtable.config.Settings;

import javax.print.PrintServiceLookup;
import java.io.File;

public class PrintersConfigData implements FileRepresentator
{
    public static final byte[] OPEN_CASH_DRAWER_DEFAULT_COMMAND = new byte[]{0x1B, 0x70, 0x00, 0x19, (byte) 0xFA};
    
    public String defaultPrinter =
            PrintServiceLookup.lookupDefaultPrintService() == null
                    ? ""
                    : PrintServiceLookup.lookupDefaultPrintService().getName();
    @Getter @Setter private boolean useOpenCashDrawerDefaultCommand = true;
    @Setter private byte[] openCashDrawerCommand = OPEN_CASH_DRAWER_DEFAULT_COMMAND;
    
    public byte[] getOpenCashDrawerCommand()
    {
        return useOpenCashDrawerDefaultCommand ? OPEN_CASH_DRAWER_DEFAULT_COMMAND : openCashDrawerCommand;
    }
    
    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/printers.json");
    }
}
