package org.lebastudios.theroundtable.config;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.output.PrinterOutputStream;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PrintersConfigData;
import org.lebastudios.theroundtable.printers.PrinterManager;

import java.net.URL;
import java.util.Arrays;

public class PrintersConfigPaneController extends SettingsPaneController
{
    @FXML private CheckBox useOpenCashDrawerDefaultCommand;
    @FXML private TextField openCashDrawerCommand;
    @FXML private ChoiceBox<String> defaultPrinter;

    @Override
    @FXML
    protected void initialize()
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

        openCashDrawerCommand.setText(parseCommand(printerData.getOpenCashDrawerCommand()));

        useOpenCashDrawerDefaultCommand.selectedProperty().addListener((_, _, newValue) ->
        {
            if (newValue == null) return;

            openCashDrawerCommand.setDisable(newValue);

            if (newValue)
            {
                openCashDrawerCommand.setText(parseCommand(PrintersConfigData.OPEN_CASH_DRAWER_DEFAULT_COMMAND));
            }
        });

        useOpenCashDrawerDefaultCommand.setSelected(printerData.isUseOpenCashDrawerDefaultCommand());
    }

    @Override
    public void apply()
    {
        var printerData = new JSONFile<>(PrintersConfigData.class);

        printerData.get().defaultPrinter = defaultPrinter.getValue();
        printerData.get().setUseOpenCashDrawerDefaultCommand(useOpenCashDrawerDefaultCommand.isSelected());
        printerData.get().setOpenCashDrawerCommand(parseCommand(openCashDrawerCommand.getText().trim()));

        printerData.save();
    }

    private byte[] parseCommand(String command)
    {
        try
        {
            var commandParsed = Arrays.stream(command.trim().split(" "))
                    .map(String::trim)
                    .map(Byte::parseByte)
                    .map(b -> (byte) b.intValue())
                    .toArray(Byte[]::new);

            byte[] commandBytes = new byte[commandParsed.length];
            for (int i = 0; i < commandBytes.length; i++)
            {
                commandBytes[i] = commandParsed[i];
            }

            return commandBytes;
        }
        catch (Exception exception)
        {
            UIEffects.shakeNode(openCashDrawerCommand);
            return null;
        }
    }

    private String parseCommand(byte[] command)
    {
        StringBuilder sb = new StringBuilder();
        for (var element : command)
        {
            sb.append(element).append(" ");
        }

        return sb.toString().trim();
    }

    @FXML
    private void testDefaultPrinter()
    {
        try (EscPos escPos = new EscPos(
                new PrinterOutputStream(PrinterOutputStream.getPrintServiceByName(defaultPrinter.getValue()))))
        {
            escPos.writeLF("Test")
                    .writeLF("Dolar: $100")
                    .writeLF("Euro: €100")
                    .writeLF("Special characters: áéñ#*=¿¡")
                    .feed(5)
                    .cut(EscPos.CutMode.PART);
        } catch (Exception _) { UIEffects.shakeNode(defaultPrinter); }
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
