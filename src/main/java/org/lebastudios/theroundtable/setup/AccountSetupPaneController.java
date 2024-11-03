package org.lebastudios.theroundtable.setup;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.lebastudios.theroundtable.accounts.LocalPasswordValidator;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.locale.LangFileLoader;

import java.net.URL;

public class AccountSetupPaneController extends SetupPaneController
{
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;
    @FXML private TextField passwordField;
    @FXML private TextField confirmPasswordField;

    public AccountSetupPaneController(Node titleNode)
    {
        super(titleNode);
    }

    @FXML @Override protected void initialize()
    {
        ((BorderPane) getRoot()).setTop(titleNode);
        errorLabel.setText("");
    }

    @Override
    public URL getFXML()
    {
        return AccountSetupPaneController.class.getResource("accountSetupPane.fxml");
    }

    @Override
    public void apply()
    {
        Account account = new Account(usernameField.getText(), 
                LocalPasswordValidator.hashPassword(passwordField.getText()), 
                Account.AccountType.ROOT);

        Database.init();
        Database.getInstance().connectTransaction(session -> session.persist(account));
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
