package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import org.lebastudios.theroundtable.plugincashregister.entities.Order;
import org.lebastudios.theroundtable.plugincashregister.entities.Receipt;

import java.io.IOException;

public interface IReceiptPrinter
{
    EscPos print(EscPos escPos, Receipt receipt, Order order) throws IOException;
}
