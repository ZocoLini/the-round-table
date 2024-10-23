package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Order;
import org.lebastudios.theroundtable.database.entities.Receipt;
import org.lebastudios.theroundtable.language.LangFileLoader;
import org.lebastudios.theroundtable.maths.BigDecimalOperations;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;

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
                    escpos.writeLF(LangFileLoader.getTranslation("word.client")
                            + ": " + receipt.getClientString());
                    
                    escpos.writeLF(LangFileLoader.getTranslation("phrase.attendedby")
                            + receipt.getAttendantName());
                }
                catch (Exception _) {}
            });
        }

        escpos.feed(1);

        PrinterManager.getInstance().getOrderPrinter().print(escpos, order);

        escpos.feed(1);

        // Taxes

        if (!printerConfig.hideTaxesDesglose)
        {
            TreeMap<BigDecimal, BigDecimal> taxes = new TreeMap<>();

            order.getProducts().forEach((product, qty) ->
            {
                taxes.put(product.getTaxes(),
                        taxes.getOrDefault(product.getTaxes(), BigDecimal.ZERO).add(qty.multiply(product.getPrice())));
            });

            taxes.forEach((percen, total) ->
            {
                try
                {
                    printTaxesGroup(escpos, percen, total);
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });

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
                    .concatLeft(BigDecimalOperations.toString(receipt.getPaymentAmount()), 18)
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

    /**
     * 
     * @param escpos
     * @param percentage beetween 0 and 1
     * @param total with taxes
     */
    private void printTaxesGroup(EscPos escpos, BigDecimal percentage, BigDecimal total) throws IOException
    {
        var percentageOver100 = percentage.multiply(new BigDecimal(100));
        var base = BigDecimalOperations.divide(total, percentage.add(BigDecimal.ONE));
        var taxes = total.subtract(base);
        
        new InLinePrinter().concatLeft(BigDecimalOperations.toString(percentageOver100), 6)
                .concatLeft(" % " + LangFileLoader.getTranslation("word.iva") + " ")
                .concatLeft(LangFileLoader.getTranslation("word.over"))
                .concatRight(taxes.toString(), 8, EscPosConst.Justification.Right)
                .concatRight(base.toString(), 8, EscPosConst.Justification.Right).print(escpos);
    }
}
