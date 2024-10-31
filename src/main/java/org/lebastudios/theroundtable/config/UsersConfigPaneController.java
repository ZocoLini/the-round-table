package org.lebastudios.theroundtable.config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.accounts.AccountCreatorController;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.accounts.ChangePasswordStageController;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.dialogs.InformationTextDialogController;
import org.lebastudios.theroundtable.ui.IconButton;
import org.lebastudios.theroundtable.ui.IconView;

import java.net.URL;
import java.util.Objects;

public class UsersConfigPaneController extends SettingsPaneController
{
    @FXML private Label errorLabel;
    @FXML private IconButton deleteAccount;
    @FXML private ComboBox<String> accountType;
    @FXML private CheckBox changePasswordOnNextLogin;
    @FXML private PasswordField passwordField;
    @FXML private VBox usersContainer;
    @FXML private IconView userIcon;
    @FXML private Label userName;
    @FXML private StackPane userView;

    private Account selectedAccount;

    @Override
    public void apply()
    {
        if (selectedAccount == null) return;

        Account.AccountType type = Account.AccountType.values()[accountType.getSelectionModel().getSelectedIndex() + 1];
        if (type == null) return;

        Database.getInstance().connectTransaction(session ->
        {
            Account account = session.get(Account.class, selectedAccount.getId());

            account.setType(type);
            account.setChangePasswordOnNextLogin(changePasswordOnNextLogin.isSelected());

            session.merge(account);
        });

        reloadUsersContainer();
    }

    @Override
    @FXML protected void initialize()
    {
        userIcon.setIconSize(100);

        accountType.getItems().clear();
        var accountTypes = Account.AccountType.values();
        for (int i = 1; i < accountTypes.length; i++)
        {
            accountType.getItems().add(Account.getTypeString(accountTypes[i]));
        }

        reloadUsersContainer();
        
        passwordField.setEditable(false);
        passwordField.setOnMouseClicked(e ->
        {
            if (selectedAccount.getType() != Account.AccountType.ROOT)
                new ChangePasswordStageController(selectedAccount).instantiate();
        });
    }

    private void reloadUsersContainer()
    {
        usersContainer.getChildren().clear();

        Database.getInstance().connectQuery(session ->
        {
            for (Account account : session.createQuery("from Account", Account.class).list())
            {
                final var currentLogged = AccountManager.getInstance().getCurrentLogged();

                if (account.getType() != Account.AccountType.ROOT &&
                        Objects.equals(account.getId(), currentLogged.getId())) {continue;}
                if (!currentLogged.hasAuthorityOver(account)) continue;

                usersContainer.getChildren().add(createUserNode(account));
            }
        });
    }

    public void addUser(ActionEvent actionEvent)
    {
        new AccountCreatorController().instantiate(controller -> controller.setAccountConsumer(account ->
        {
            if (account != null)
            {
                usersContainer.getChildren().add(createUserNode(account));
            }
        }), false);
    }

    @FXML
    private void removeUser()
    {
        if (selectedAccount == null) return;

        if (selectedAccount.getType() == Account.AccountType.ROOT)
        {
            new InformationTextDialogController("The root account cannot be deleted.").instantiate();
            return;
        }

        if (selectedAccount.getType() == Account.AccountType.ADMIN
                && AccountManager.getInstance().getCurrentLogged().getType() != Account.AccountType.ROOT)
        {
            new InformationTextDialogController("Only the root account can delete an admin account.").instantiate();
            return;
        }

        Database.getInstance().connectTransaction(session ->
        {
            Account account = session.get(Account.class, selectedAccount.getId());

            session.remove(account);
        });

        userView.setVisible(false);
        reloadUsersContainer();
    }

    private Node createUserNode(Account account)
    {
        HBox root = new HBox();
        root.setPrefWidth(250);
        root.getStyleClass().add("button");
        root.setSpacing(10);

        root.setOnMouseClicked(e -> onUserClicked(account));

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

    private void onUserClicked(Account account)
    {
        this.selectedAccount = account;

        deleteAccount.setDisable(account.getType() == Account.AccountType.ROOT);

        userIcon.setIconName(account.getIconName());
        userName.setText(account.getName());

        accountType.setValue(account.getTypeString());
        accountType.setDisable(account.getType() == Account.AccountType.ROOT);

        passwordField.setText("abc123.");

        changePasswordOnNextLogin.setSelected(account.isChangePasswordOnNextLogin());

        userView.setVisible(true);
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
        return AboutConfigPaneController.class.getResource("usersConfigPane.fxml");
    }
}
