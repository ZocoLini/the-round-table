package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.output.PrinterOutputStream;
import lombok.Getter;
import lombok.Setter;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;

import javax.print.PrintService;

public class PrinterManager
{
    private static PrinterManager instance;
    @Getter @Setter private IPrinter headerPrinter = new DefaultHeaderPrinter();
    @Getter @Setter private IOrderPrinter orderPrinter = new DefaultOrderPrinter();
    @Getter @Setter private IPrinter footerPrinter = new DefaultFooterPrinter();
    private PrinterManager() {}

    public static PrinterManager getInstance()
    {
        if (instance == null) instance = new PrinterManager();

        return instance;
    }

    public PrintService getDefaultPrintService()
    {
        var defaultPrinterName = new JSONFile<>(PrintersConfigData.class).get().defaultPrinter;
        return PrinterOutputStream.getPrintServiceByName(defaultPrinterName);
    }

    public String[] getAvailablePrinters()
    {
        return PrinterOutputStream.getListPrintServicesNames();
    }
}
