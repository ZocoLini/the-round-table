package org.lebastudios.theroundtable.accounts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import lombok.Setter;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.net.URL;
import java.util.function.Consumer;

public class AccountCreatorController extends StageController<AccountCreatorController>
{
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ChoiceBox<String> accountTypeChoiceBox;

    @Setter private Consumer<Account> accountConsumer;

    @FXML @Override protected void initialize()
    {
        var accountTypes = Account.AccountType.values();
        for (int i = 1; i < accountTypes.length; i++)
        {
            accountTypeChoiceBox.getItems().add(Account.getTypeString(accountTypes[i]));
        }

        accountTypeChoiceBox.getSelectionModel().select(0);
    }

    @FXML
    private void createAccount(ActionEvent actionEvent)
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
                LocalPasswordValidator.hashPassword(passwordField.getText()),
                Account.AccountType.values()[accountTypeChoiceBox.getSelectionModel().getSelectedIndex() + 1]
        );

        Database.getInstance().connectTransaction(session -> session.persist(account));

        cancel(actionEvent);
        
        if (accountConsumer != null) accountConsumer.accept(account);
    }

    @FXML
    private void cancel(ActionEvent actionEvent)
    {
        close();
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public String getTitle()
    {
        return "Create Account";
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public URL getFXML()
    {
        return AccountCreatorController.class.getResource("accountCreator.fxml");
    }
}
