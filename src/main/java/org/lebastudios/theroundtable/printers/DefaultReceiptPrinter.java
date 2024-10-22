package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Order;
import org.lebastudios.theroundtable.database.entities.Receipt;
import org.lebastudios.theroundtable.language.LangFileLoader;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

public class DefaultReceiptPrinter implements IReceiptPrinter
{
    @Override
    public EscPos print(EscPos escpos, Receipt receipt, Order order) throws IOException
    {
        var printerConfig = new JSONFile<>(PrintersConfigData.class).get();
        PrinterManager.getInstance().getHeaderPrinter().print(escpos);

        escpos.feed(1);

        if (!printerConfig.hideReceiptData)
        {
            escpos.writeLF(Styles.CENTERED,
                    LangFileLoader.getTranslation("word.date") + ": "
                            + receipt.getTransaction().getDate().toLocalDate().toString() + "  " +
                            LangFileLoader.getTranslation("word.time") + ": " +
                            receipt.getTransaction().getDate().toLocalTime().truncatedTo(ChronoUnit.SECONDS)
                                    .toString());

            escpos.feed(1);

            escpos.writeLF(LangFileLoader.getTranslation("phrase.tablename")
                    + ": " + order.getOrderName());
            escpos.writeLF(LangFileLoader.getTranslation("phrase.simplifiedreceipt") +
                    ": #" + receipt.getId());

            Database.getInstance().connectQuery(session ->
            {
                try
                {
                    if (receipt.getClientIdentifier() == null)
                    {
                        escpos.writeLF(LangFileLoader.getTranslation("phrase.generalpublicclient"));
                    }
                    else
                    {
                        escpos.writeLF(LangFileLoader.getTranslation("word.client")
                                + ": " + receipt.getClientName() + " - " + receipt.getClientIdentifier());
                    }

                    if (receipt.getAttendantName() != null)
                    {
                        escpos.writeLF(LangFileLoader.getTranslation("phrase.attendedby")
                                + receipt.getAttendantName());
                    }
                }
                catch (Exception _) {}
            });
        }

        escpos.feed(1);

        PrinterManager.getInstance().getOrderPrinter().print(escpos, order);

        escpos.feed(1);

        // Taxes

        if (!printerConfig.hideTaxesIncludedMsg)
        {
            escpos.writeLF(LangFileLoader.getTranslation("phrase.taxesincluded"));

            escpos.feed(1);
        }

        if (!printerConfig.hideTaxesDesglose)
        {
            new InLinePrinter().concatRight("BASE", 20, EscPosConst.Justification.Left_Default)
                    .concatRight(LangFileLoader.getTranslation("word.iva"), 8, EscPosConst.Justification.Left_Default)
                    .concatRight(LangFileLoader.getTranslation("word.quote"), 15,
                            EscPosConst.Justification.Left_Default)
                    .print(escpos);

            var baseImponible = order.getTotal().divide(new BigDecimal("1.10"), 2, RoundingMode.FLOOR);
            var taxes = order.getTotal().subtract(baseImponible).setScale(2, RoundingMode.FLOOR);

            new InLinePrinter().concatRight(baseImponible.toString(), 20, EscPosConst.Justification.Left_Default)
                    .concatRight("10%", 8, EscPosConst.Justification.Left_Default)
                    .concatRight(taxes.toString(), 15, EscPosConst.Justification.Left_Default).print(escpos);

            escpos.feed(1);
        }

        // Payment Info
        if (!printerConfig.hidePaymentInfo)
        {
            escpos.writeLF(Styles.CENTERED,
                    LangFileLoader.getTranslation("phrase.paymentmethod")
                            + receipt.getPaymentMethod());

            new InLinePrinter().concatLeft("", 6)
                    .concatLeft(LangFileLoader.getTranslation("phrase.paymentamount"), 18)
                    .concatRight(LangFileLoader.getTranslation("word.change"), 18)
                    .concatRight("", 6).print(escpos);

            new InLinePrinter().concatLeft("", 6)
                    .concatLeft(receipt.getPaymentAmount().toString(), 18)
                    .concatRight(
                            order.getTotal().subtract(receipt.getPaymentAmount()).abs().setScale(2, RoundingMode.FLOOR)
                                    .toString(),
                            18).concatRight("", 6)
                    .print(escpos);

            escpos.feed(1);
        }

        PrinterManager.getInstance().getFooterPrinter().print(escpos);

        new OpenCashDrawer().print(escpos);

        return escpos;
    }
}
