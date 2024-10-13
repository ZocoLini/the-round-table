package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import org.lebastudios.theroundtable.database.entities.Order;
import org.lebastudios.theroundtable.language.LangFileLoader;

import java.io.IOException;
import java.math.RoundingMode;

public final class DefaultOrderPrinter implements IOrderPrinter
{
    @Override
    public EscPos print(EscPos escpos, Order order) throws IOException
    {
        // Top Label
        var linePrinter = new InLinePrinter();
        linePrinter.concatLeft("", 3)
                .concatLeft("Qty", 6)
                .concatLeft(" ")
                .concatLeft(LangFileLoader.getTranslation("word.product"), 20)
                .concatRight(LangFileLoader.getTranslation("word.price"), 8, EscPosConst.Justification.Left_Default)
                .concatRight(LangFileLoader.getTranslation("word.import"), 10, EscPosConst.Justification.Left_Default)
                .print(escpos);

        new LineFiller("-").print(escpos);

        for (var entry : order.getProducts().entrySet())
        {
            var total = entry.getKey().getPrice().multiply(entry.getValue()).toString();
            var price = entry.getKey().getPrice().toString();
            var productQty = entry.getValue().toString();
            var productName = entry.getKey().getName();

            linePrinter = new InLinePrinter();
            linePrinter.concatLeft("", 3)
                    .concatLeft(productQty, 6)
                    .concatLeft(" ")
                    .concatLeft(productName, 20)
                    .concatRight(price, 8, EscPosConst.Justification.Left_Default)
                    .concatRight(total, 10, EscPosConst.Justification.Left_Default).print(escpos);
        }

        new LineFiller("-").print(escpos);

        linePrinter = new InLinePrinter(Style.FontSize._2);
        linePrinter.concatLeft("TOTAL")
                .concatRight(order.getTotal().setScale(2, RoundingMode.FLOOR).toString())
                .concatRight("EUR").print(escpos);

        return escpos;
    }
}
