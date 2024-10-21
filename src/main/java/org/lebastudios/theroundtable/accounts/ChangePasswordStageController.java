package org.lebastudios.theroundtable.accounts;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.language.LangBundleLoader;
import org.lebastudios.theroundtable.ui.StageBuilder;

public class ChangePasswordStageController extends StageController
{
    @FXML private PasswordField password;
    @FXML private PasswordField confirmPassword;

    private final Account account;
    private Runnable onAccept;

    public ChangePasswordStageController(Account account)
    {
        super("changePasswordStage.fxml");

        this.account = account;
    }

    public ChangePasswordStageController setOnAccept(Runnable runnable)
    {
        this.onAccept = runnable;
        return this;
    }

    @SneakyThrows
    @Override
    public void instantiate()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getFxml()));
        LangBundleLoader.addLangBundle(loader, Launcher.class);
        loader.setController(this);
        loader.load();

        new StageBuilder(getParent())
                .setModality(Modality.APPLICATION_MODAL)
                .setTitle("Change password")
                .build()
                .showAndWait();
    }

    @FXML
    private void changePassword(ActionEvent actionEvent)
    {
        if (!LocalPasswordValidator.isValidFormat(password.getText()))
        {
            UIEffects.shakeNode(password);
            return;
        }

        if (!password.getText().equals(confirmPassword.getText()))
        {
            UIEffects.shakeNode(password);
            return;
        }

        Database.getInstance().connectTransaction(session ->
        {
            Account account = session.get(Account.class, this.account.getId());

            account.setPassword(LocalPasswordValidator.hashPassword(password.getText()));
            account.setChangePasswordOnNextLogin(false);

            session.merge(account);
        });

        if (onAccept != null) onAccept.run();
        close();
    }

    @FXML
    private void cancel(ActionEvent actionEvent)
    {
        close();
    }
}
