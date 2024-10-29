package org.lebastudios.theroundtable.setup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.accounts.LocalPasswordValidator;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.locale.LangBundleLoader;
import org.lebastudios.theroundtable.locale.LangFileLoader;

public class AccountSetupPaneController extends SetupPaneController
{
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;
    @FXML private TextField passwordField;
    @FXML private TextField confirmPasswordField;

    @SneakyThrows
    public static SetupPaneController loadAttachedNode()
    {
        FXMLLoader loader = new FXMLLoader(AccountSetupPaneController.class.getResource("accountSetupPane.fxml"));
        LangBundleLoader.loadLang(loader, Launcher.class);
        Node node = loader.load();

        SetupPaneController controller = loader.getController();
        controller.root = node;

        return controller;
    }

    public void initialize()
    {
        errorLabel.setText("");
    }

    @Override
    public void apply()
    {
        Account account = new Account(usernameField.getText(), 
                LocalPasswordValidator.hashPassword(passwordField.getText()), 
                Account.AccountType.ROOT);

        Database.getInstance().connectTransaction(session ->
        {
            session.persist(account);
        });
    }

    @Override
    public boolean validateData()
    {
        if (usernameField.getText().isBlank() || usernameField.getText().length() < 3)
        {
            errorLabel.setText(LangFileLoader.getTranslation("setup.error.invalidname"));
            UIEffects.shakeNode(usernameField);
            return false;
        }

        if (!LocalPasswordValidator.isValidFormat(passwordField.getText()))
        {
            errorLabel.setText(LangFileLoader.getTranslation("setup.error.invalidpassword"));
            UIEffects.shakeNode(passwordField);
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText()))
        {
            errorLabel.setText(LangFileLoader.getTranslation("setup.error.passwordmatch"));
            UIEffects.shakeNode(confirmPasswordField);
            return false;
        }

        errorLabel.setText("");
        return true;
    }
}
