package org.lebastudios.theroundtable.config;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.lebastudios.theroundtable.accounts.AccountCreatorController;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.dialogs.InformationTextDialogController;
import org.lebastudios.theroundtable.ui.IconView;

public class UsersConfigPaneController extends SettingsPaneController
{
    public VBox usersContainer;
    
    @Override
    public void apply()
    {
        
    }

    @Override
    public void initialize()
    {
        usersContainer.getChildren().clear();
        
        Database.getInstance().connectQuery(session ->
        {
            for (Account account : session.createQuery("from Account", Account.class).list())
            {
                usersContainer.getChildren().add(createUserNode(account));
            }
        });
    }

    public void addUser(ActionEvent actionEvent) 
    {
        Account account = AccountCreatorController.createAcount();
        
        if (account != null)
        {
            usersContainer.getChildren().add(createUserNode(account));
        }
    }

    public void removeUser(Account selectedAccount) 
    {
        if (selectedAccount.getType() == Account.AccountType.ROOT)
        {
            InformationTextDialogController.loadAttachedNode("The root account cannot be deleted.");
            return;
        }
        
        if (selectedAccount.getType() == Account.AccountType.ADMIN 
                && AccountManager.getInstance().getCurrentLogged().getType() != Account.AccountType.ROOT) 
        {
            InformationTextDialogController.loadAttachedNode("Only the root account can delete an admin account.");
            return;
        }
        
        Database.getInstance().connectTransaction(session ->
        {
            Account account = session.get(Account.class, selectedAccount.getId());
            
            if (account.getReceipts().isEmpty()) 
            {
                session.remove(account);
            }
            else
            {
                account.setDeleted(true);
                session.merge(account);
            }
        });
        
        initialize();
    }
    
    private Node createUserNode(Account account)
    {
        HBox root = new HBox();
        root.setPrefWidth(250);
        root.getStyleClass().add("button");
        root.setSpacing(10);

        ContextMenu contextMenu = new ContextMenu();
        final var menuItem = new MenuItem("Delete");
        menuItem.setOnAction(event -> removeUser(account));
        contextMenu.getItems().add(menuItem);
        root.setOnContextMenuRequested(event ->
        {
            contextMenu.show(root, event.getScreenX(), event.getScreenY());

        });
        
        IconView icon = new IconView(account.getIconName());
        root.getChildren().add(icon);
        
        VBox info = new VBox();
        root.getChildren().add(info);
        info.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(info, Priority.ALWAYS);
        info.setSpacing(5);
        
        info.getChildren().add(new Label(account.getName()));
        info.getChildren().add(new Label(account.getTypeString()));
        
        return root;
    }
}
