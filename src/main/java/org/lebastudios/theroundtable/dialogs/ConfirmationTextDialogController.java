package org.lebastudios.theroundtable.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.language.LangBundleLoader;
import org.lebastudios.theroundtable.language.LangFileLoader;

import java.util.function.Consumer;

public class ConfirmationTextDialogController
{
    private final String informationText;
    private final Consumer<Boolean> action;
    @FXML private Label textLabel;

    public ConfirmationTextDialogController(String informationText, Consumer<Boolean> action)
    {
        this.informationText = informationText;
        this.action = action;
    }

    @SneakyThrows
    public static void loadAttachedNode(String informationText, Consumer<Boolean> action)
    {
        var loader = new FXMLLoader(ConfirmationTextDialogController.class.getResource("confirmationTextDialog.fxml"));

        loader.setController(new ConfirmationTextDialogController(informationText, action));
        LangBundleLoader.addLangBundle(loader, Launcher.class);

        TheRoundTableApplication.showAndWaitInStage(loader.load(),
                LangFileLoader.getTranslation("title.confirmdialog"));
    }

    public void initialize()
    {
        textLabel.setText(informationText);
    }

    @FXML
    private void accept(ActionEvent actionEvent)
    {
        action.accept(true);
        Stage stage = (Stage) textLabel.getScene().getWindow();
        stage.close();
    }

    public void cancel(ActionEvent actionEvent)
    {
        action.accept(false);
        Stage stage = (Stage) textLabel.getScene().getWindow();
        stage.close();
    }
}
