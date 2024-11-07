package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import org.lebastudios.theroundtable.plugincashregister.entities.Order;

import java.io.IOException;

public interface IOrderPrinter
{
    EscPos print(EscPos escpos, Order order) throws IOException;
}
