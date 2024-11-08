package org.lebastudios.theroundtable.accounts;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import lombok.Getter;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.controllers.PaneController;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.ui.IconView;

import java.net.URL;
import java.util.function.BiConsumer;

public class AccountBoxController extends PaneController<AccountBoxController>
{
    @Getter private final Account account;

    @FXML private IconView accountIcon;
    @FXML private Label accountName;

    public AccountBoxController(Account account)
    {
        this.account = account;
    }

    @FXML @Override protected void initialize()
    {
        accountName.setText(account.getName());
        accountIcon.setIconName(account.getIconName());
    }

    public void setOnAction(BiConsumer<AccountBoxController, Node> action)
    {
        getRoot().setOnMouseClicked(_ -> action.accept(this, getRoot()));
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public URL getFXML()
    {
        return AccountBoxController.class.getResource("accountBox.fxml");
    }
}
