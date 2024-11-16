package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.config.Settings;

import javax.print.PrintServiceLookup;
import java.io.File;

public class PrintersConfigData implements FileRepresentator
{
    public String defaultPrinter =
            PrintServiceLookup.lookupDefaultPrintService() == null
                    ? ""
                    : PrintServiceLookup.lookupDefaultPrintService().getName();
    
    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/printers.json");
    }
}
