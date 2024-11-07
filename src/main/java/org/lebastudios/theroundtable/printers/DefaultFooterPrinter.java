package org.lebastudios.theroundtable.printers;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import org.lebastudios.theroundtable.locale.LangFileLoader;

import java.io.IOException;

final class DefaultFooterPrinter implements IPrinter
{
    @Override
    public EscPos print(EscPos escpos) throws IOException
    {
        var centered = new Style().setBold(true).setJustification(EscPosConst.Justification.Center);
        escpos.writeLF(centered, LangFileLoader.getTranslation("ticket.footerbye"));
        return escpos;
    }
}
