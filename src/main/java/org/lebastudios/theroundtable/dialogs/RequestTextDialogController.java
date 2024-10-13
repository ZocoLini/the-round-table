package org.lebastudios.theroundtable.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.language.LangBundleLoader;

import java.util.function.Consumer;
import java.util.function.Function;

public class RequestTextDialogController
{
    private final Consumer<String> action;
    private final Runnable onCancel;
    private final Function<String, Boolean> validator;
    private final String inputTip;
    private final String info;
    private final String validationError;
    @FXML private TextField textInputField;
    @FXML private Label infoLabel;
    @FXML private Label errorLabel;

    public RequestTextDialogController(Consumer<String> action, String inputTip,
            Function<String, Boolean> validator, String info, String validationError, Runnable onCancel)
    {
        this.inputTip = inputTip;

        this.action = action;
        this.validator = validator;
        this.info = info;
        this.validationError = validationError;
        this.onCancel = onCancel;
    }

    @SneakyThrows
    public static void loadAttachedNode(Consumer<String> action, String title, String inputTip,
            Function<String, Boolean> validator, String info, String validationError, Runnable onCancel)
    {
        var loader = new FXMLLoader(RequestTextDialogController.class.getResource("requestTextDialog.fxml"));

        loader.setController(new RequestTextDialogController(action, inputTip, validator, info, validationError,
                onCancel));
        LangBundleLoader.addLangBundle(loader, Launcher.class);

        TheRoundTableApplication.showAndWaitInStage(loader.load(), title);
    }

    @SneakyThrows
    public static void loadAttachedNode(Consumer<String> action, String title, String inputTip,
            Function<String, Boolean> validator)
    {
        loadAttachedNode(action, title, inputTip, validator, "", "", () -> {});
    }

    public void initialize()
    {
        textInputField.setPromptText(inputTip);

        if (!info.isBlank())
        {
            infoLabel.setText(info);
            infoLabel.setVisible(true);
        }
    }

    @FXML
    private void accept(ActionEvent actionEvent)
    {
        if (!validateInput())
        {
            UIEffects.shakeNode(textInputField);
            errorLabel.setText(validationError);
            errorLabel.setVisible(true);
            return;
        }

        action.accept(textInputField.getText());

        Stage stage = (Stage) textInputField.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput()
    {
        if (validator != null) return validator.apply(textInputField.getText());

        textInputField.setText(textInputField.getText().trim());
        return !textInputField.getText().isBlank();
    }

    @FXML
    private void cancel(ActionEvent actionEvent)
    {
        Stage stage = (Stage) textInputField.getScene().getWindow();
        onCancel.run();
        stage.close();
    }
}
