package org.lebastudios.theroundtable.accounts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.language.LangBundleLoader;

public class AccountCreatorController
{
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ChoiceBox<String> accountTypeChoiceBox;

    private static Account account;
    
    @SneakyThrows
    public static Account createAcount()
    {
        FXMLLoader loader = new FXMLLoader(AccountCreatorController.class.getResource("accountCreator.fxml"));
        LangBundleLoader.addLangBundle(loader, Launcher.class);
        
        TheRoundTableApplication.showAndWaitInStage(loader.load(), "Create Account");
        
        Account acc = account;
        account = null;
        
        return acc;
    }

    public void initialize()
    {
        var accountTypes = Account.AccountType.values();
        for (int i = 1; i < accountTypes.length; i++)
        {
            accountTypeChoiceBox.getItems().add(Account.getTypeString(accountTypes[i]));
        }
        
        accountTypeChoiceBox.getSelectionModel().select(0);
    }
    
    @FXML private void createAccount(ActionEvent actionEvent) 
    {
        if (passwordField.getText().isBlank() || passwordField.getText().length() < 8)
        {
            UIEffects.shakeNode(passwordField);
            return;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText()))
        {
            UIEffects.shakeNode(confirmPasswordField);
            return;
        }
        
        Account account = new Account(
                usernameField.getText(),
                passwordField.getText(),
                Account.AccountType.values()[accountTypeChoiceBox.getSelectionModel().getSelectedIndex() + 1]
        );
        
        Database.getInstance().connectTransaction(session ->
        {
            session.persist(account);
        });
        
        AccountCreatorController.account = account;

        cancel(actionEvent);
    }

    @FXML private void cancel(ActionEvent actionEvent) 
    {
        ((Stage) usernameField.getScene().getWindow()).close();
    }
}
