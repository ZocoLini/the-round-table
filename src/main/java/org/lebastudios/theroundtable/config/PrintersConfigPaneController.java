package org.lebastudios.theroundtable.config;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;
import org.lebastudios.theroundtable.printers.PrinterManager;

import java.net.URL;

public class PrintersConfigPaneController extends SettingsPaneController
{
    @FXML private ChoiceBox<String> defaultPrinter;

    @Override
    @FXML protected void initialize()
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
    }

    @Override
    public void apply()
    {
        var printerData = new JSONFile<>(PrintersConfigData.class);

        printerData.get().defaultPrinter = defaultPrinter.getValue();
        printerData.save();
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public URL getFXML()
    {
        return AboutConfigPaneController.class.getResource("printersConfigPane.fxml");
    }
}
