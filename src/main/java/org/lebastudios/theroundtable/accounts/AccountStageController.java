package org.lebastudios.theroundtable.accounts;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.ui.LoadingPaneController;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.net.URL;
import java.util.List;

public class AccountStageController extends StageController<AccountStageController>
{
    @FXML private Label passwordError;
    @FXML private PasswordField passwordField;
    @FXML private VBox passwordBox;
    @FXML private BorderPane root;
    @FXML private FlowPane accountsBox;

    private Account accountSelected;

    @FXML @Override protected void initialize()
    {
        root.setCenter(new LoadingPaneController().getRoot());
        new Thread(() ->
        {
            Database.init();
            Database.getInstance().connectQuery(session ->
            {
                List<Account> accounts = session.createQuery("from Account", Account.class).list();

                accounts.forEach(account -> accountsBox.getChildren().add(generateAccountBox(account)));
            });

            Platform.runLater(() -> root.setCenter(accountsBox));
        }).start();
    }

    @SneakyThrows
    private Node generateAccountBox(Account account)
    {
        var controller = new AccountBoxController(account);

        controller.setOnAction(this::onAccountSelected);

        return controller.getRoot();
    }

    private void onAccountSelected(AccountBoxController controller, Node node)
    {
        accountSelected = controller.getAccount();

        root.setCenter(passwordBox);
    }

    public void submitPassword(ActionEvent actionEvent)
    {
        if (!LocalPasswordValidator.validatePassword(passwordField.getText(), accountSelected.getPassword()))
        {
            passwordField.clear();
            passwordError.setText("Invalid password");
            UIEffects.shakeNode(passwordField);
            return;
        }

        if (accountSelected.isChangePasswordOnNextLogin())
        {
            new ChangePasswordStageController(accountSelected).setOnAccept(() ->
            {
                AccountManager.getInstance().setCurrentLogged(accountSelected);
                ((Stage) root.getScene().getWindow()).close();
            }).instantiate();
        }
        else
        {
            AccountManager.getInstance().setCurrentLogged(accountSelected);
            ((Stage) root.getScene().getWindow()).close();
        }
    }

    public void cancelPassword(ActionEvent actionEvent)
    {
        accountSelected = null;
        passwordField.clear();
        passwordError.setText("");
        root.setCenter(accountsBox);
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder) 
    {
        stageBuilder.setStageConsumer(stage -> stage.setOnCloseRequest(e ->
        {
            AppLifeCicleEvents.OnAppCloseRequest.invoke(e);
            
            if (!e.isConsumed())
            {
                AppLifeCicleEvents.OnAppClose.invoke(e);
                System.exit(0);
            }
        }));
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public String getTitle()
    {
        return "Login";
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public URL getFXML()
    {
        return AccountStageController.class.getResource("accountStage.fxml");
    }
}
