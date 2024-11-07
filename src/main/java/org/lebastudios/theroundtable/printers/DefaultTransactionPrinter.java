package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import org.lebastudios.theroundtable.plugincashregister.entities.Transaction;
import org.lebastudios.theroundtable.locale.LangFileLoader;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

public class DefaultTransactionPrinter implements IPrinter
{
    private final Transaction transaction;

    public DefaultTransactionPrinter(Transaction transaction)
    {
        this.transaction = transaction;
    }

    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        Style style = new Style();
        style.setFontSize(Style.FontSize._2, Style.FontSize._2)
                .setJustification(EscPosConst.Justification.Center);

        escpos.writeLF(style, "Transaction #" + transaction.getId());

        escpos.feed(1);

        new LineFiller("-").print(escpos);

        escpos.writeLF(Styles.CENTERED,
                LangFileLoader.getTranslation("word.date") + " " + transaction.getDate().toLocalDate().toString()
        );

        escpos.writeLF(Styles.CENTERED,
                LangFileLoader.getTranslation("word.time") + " "
                        + transaction.getDate().toLocalTime().truncatedTo(ChronoUnit.SECONDS).toString()
        );

        escpos.feed(1);

        escpos.writeLF(transaction.getDescription());

        escpos.feed(1);

        new InLinePrinter(Style.FontSize._2)
                .concatLeft("TOTAL:")
                .concatRight(transaction.getAmount().toString() + "EUR")
                .print(escpos);

        new OpenCashDrawer().print(escpos);

        return escpos;
    }
}
