package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;
import org.lebastudios.theroundtable.printers.PrinterManager;

public class PrintersConfigPaneController extends SettingsPaneController
{
    @FXML private CheckBox hideEstablishmentLogo;
    @FXML private CheckBox hideReceiptData;
    @FXML private CheckBox hideTaxesIncludedMsg;
    @FXML private CheckBox hideTaxesDesglose;
    @FXML private CheckBox hidePaymentInfo;
    @FXML private ChoiceBox<String> defaultPrinter;

    @Override
    public void apply()
    {
        var printerData = new JSONFile<>(PrintersConfigData.class);

        printerData.get().defaultPrinter = defaultPrinter.getValue();

        printerData.get().hideTaxesDesglose = hideTaxesDesglose.isSelected();
        printerData.get().hidePaymentInfo = hidePaymentInfo.isSelected();
        printerData.get().hideReceiptData = hideReceiptData.isSelected();
        printerData.get().hideTaxesIncludedMsg = hideTaxesIncludedMsg.isSelected();
        printerData.get().hideEstablishmentLogo = hideEstablishmentLogo.isSelected();

        printerData.save();
    }

    @Override
    public void initialize()
    {
        var printerData = new JSONFile<>(PrintersConfigData.class).get();

        defaultPrinter.getItems().clear();

        defaultPrinter.getItems().addAll(
                PrinterManager.getInstance().getAvailablePrinters()
        );

        if (!printerData.defaultPrinter.isEmpty())
        {
            defaultPrinter.setValue(printerData.defaultPrinter);
        }
        else
        {
            defaultPrinter.setValue("");
        }

        hideReceiptData.setSelected(printerData.hideReceiptData);
        hideTaxesIncludedMsg.setSelected(printerData.hideTaxesIncludedMsg);
        hideTaxesDesglose.setSelected(printerData.hideTaxesDesglose);
        hidePaymentInfo.setSelected(printerData.hidePaymentInfo);
        hideEstablishmentLogo.setSelected(printerData.hideEstablishmentLogo);
    }
}
