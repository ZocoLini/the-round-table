package org.lebastudios.theroundtable.accounts;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.lebastudios.theroundtable.database.entities.Account;
import org.lebastudios.theroundtable.ui.IconView;

import java.util.function.BiConsumer;

public class AccountBoxController
{
    @Getter private final Account account;
    
    @FXML private VBox root;
    @FXML private IconView accountIcon;
    @FXML private Label accountName;
    
    public AccountBoxController(Account account)
    {
        this.account = account;
    }
    
    public void initialize()
    {
        accountName.setText(account.getName());
        accountIcon.setIconName("account.png");
    }
    
    public void setOnAction(BiConsumer<AccountBoxController, Node> action)
    {
        root.setOnMouseClicked(_ -> action.accept(this, root));
    }
}
