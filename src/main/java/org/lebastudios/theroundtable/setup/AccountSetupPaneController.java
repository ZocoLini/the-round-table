package org.lebastudios.theroundtable.setup;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;

public class AccountSetupPaneController extends SetupPaneController
{
    @FXML private Label errorLabel;
    @FXML private TextField passwordField;
    @FXML private TextField confirmPasswordField;

    @SneakyThrows
    public static SetupPaneController loadAttachedNode()
    {
        FXMLLoader loader = new FXMLLoader(AccountSetupPaneController.class.getResource("accountSetupPane.fxml"));
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
        Account account = new Account("Administrator", passwordField.getText(), Account.AccountType.ROOT);

        Database.getInstance().connectTransaction(session ->
        {
            session.persist(account);
        });
    }

    @Override
    public boolean validateData()
    {
        if (passwordField.getText().isBlank() || passwordField.getText().length() < 8) 
        {
            errorLabel.setText("Password must be at least 8 characters long.");
            UIEffects.shakeNode(passwordField);
            return false;
        }
        
        if (!passwordField.getText().equals(confirmPasswordField.getText()))
        {
            errorLabel.setText("Passwords do not match.");
            UIEffects.shakeNode(confirmPasswordField);
            return false;
        }
        
        return true;
    }
}
