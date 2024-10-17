package org.lebastudios.theroundtable.accounts;

import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.events.UserEvents;
import org.lebastudios.theroundtable.language.LangBundleLoader;

import java.util.List;

public class AccountStageController
{
    @FXML private Label passwordError;
    @FXML private PasswordField passwordField;
    @FXML private VBox passwordBox;
    @FXML private BorderPane root;
    @FXML private FlowPane accountsBox;

    private Account accountSelected;
    
    @SneakyThrows
    public static Parent getParentNode()
    {
        FXMLLoader loader = new FXMLLoader(AccountStageController.class.getResource("accountStage.fxml"));
        LangBundleLoader.addLangBundle(loader, Launcher.class);
        
        return loader.load();
    }
    
    public void initialize()
    {
        root.setCenter(accountsBox);
        
        Database.getInstance().connectQuery(session ->
        {
            List<Account> accounts = session.createQuery("from Account", Account.class).list();
            
            accounts.forEach(account -> accountsBox.getChildren().add(generateAccountBox(account)));
        });
    }
    
    @SneakyThrows
    private Node generateAccountBox(Account account)
    {
        FXMLLoader loader = new FXMLLoader(AccountStageController.class.getResource("accountBox.fxml"));
        LangBundleLoader.addLangBundle(loader, Launcher.class);

        var controller = new AccountBoxController(account);
        loader.setController(controller);
        
        Node root = loader.load();
        
        controller.setOnAction(this::onAccountSelected);
        
        return root;
    }
    
    private void onAccountSelected(AccountBoxController controller, Node node)
    {
        accountSelected = controller.getAccount();
        
        root.setCenter(passwordBox);
    }

    public void submitPassword(ActionEvent actionEvent) 
    {
        if (!BCrypt.verifyer().verify(passwordField.getText().toCharArray(), accountSelected.getPassword()).verified)
        {
            passwordField.clear();
            passwordError.setText("Invalid password");
            UIEffects.shakeNode(passwordField);
            return;
        }

        AccountManager.getInstance().setCurrentLogged(accountSelected);
        ((Stage) root.getScene().getWindow()).close();
    }

    public void cancelPassword(ActionEvent actionEvent) 
    {
        accountSelected = null;
        passwordField.clear();
        passwordError.setText("");
        root.setCenter(accountsBox);
    }
}
