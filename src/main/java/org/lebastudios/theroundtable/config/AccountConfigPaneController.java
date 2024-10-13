package org.lebastudios.theroundtable.config;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.lebastudios.theroundtable.communications.LicenseValidator;
import org.lebastudios.theroundtable.config.data.AccountConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.dialogs.InformationTextDialogController;
import org.lebastudios.theroundtable.ui.TaskManager;

import java.util.function.Consumer;

public class AccountConfigPaneController extends SettingsPaneController
{
    @FXML private TextField licenseId;
    private String lastLicense = "";

    @Override
    public void initialize()
    {
        var accountData = new JSONFile<>(AccountConfigData.class).get();

        licenseId.setText(accountData.license);
        lastLicense = accountData.license;
    }

    @Override
    public void apply()
    {
        var accountData = new JSONFile<>(AccountConfigData.class);
        accountData.get().license = licenseId.getText();
        accountData.save();
        
        TaskManager.getInstance().startNewTaskWithProgressBar(
                new LicenseValidator(onLicenseValidated)
                        .createValidatingLicenseTask()
        );
    }

    private final Consumer<Boolean> onLicenseValidated = validation ->
    {
        if (!validation)
        {
            Platform.runLater(() ->
            {
                var accountData = new JSONFile<>(AccountConfigData.class);
                accountData.get().license = lastLicense;
                accountData.save();
                
                InformationTextDialogController.loadAttachedNode(
                        "Your license could not be validated. Check your internet connection and try again."
                );
            });
        }
    };
}
