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

public class InformationTextDialogController
{
    private final String informationText;
    @FXML private Label textLabel;


    public InformationTextDialogController(String informationText)
    {
        this.informationText = informationText;
    }

    @SneakyThrows
    public static void loadAttachedNode(String informationText)
    {
        var loader = new FXMLLoader(InformationTextDialogController.class.getResource("informationTextDialog.fxml"));

        loader.setController(new InformationTextDialogController(informationText));
        LangBundleLoader.addLangBundle(loader, Launcher.class);

        TheRoundTableApplication.showAndWaitInStage(loader.load(), LangFileLoader.getTranslation("title.infodialog"));
    }

    public void initialize()
    {
        textLabel.setText(informationText);
    }

    @FXML
    private void accept(ActionEvent actionEvent)
    {
        Stage stage = (Stage) textLabel.getScene().getWindow();
        stage.close();
    }
}
