package org.lebastudios.theroundtable.dialogs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.controllers.StageController;
import org.lebastudios.theroundtable.locale.LangBundleLoader;
import org.lebastudios.theroundtable.ui.StageBuilder;

import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;

public class RequestTextDialogController extends StageController<RequestTextDialogController>
{
    private final Consumer<String> action;
    private final Runnable onCancel;
    private final Function<String, Boolean> validator;
    private final String inputTip;
    private final String info;
    private final String validationError;
    private final String title;
    @FXML private TextField textInputField;
    @FXML private Label infoLabel;
    @FXML private Label errorLabel;

    public RequestTextDialogController(Consumer<String> action, String inputTip, String title,
            Function<String, Boolean> validator, String info, String validationError, Runnable onCancel)
    {
        this.inputTip = inputTip;

        this.action = action;
        this.validator = validator;
        this.title = title;
        this.info = info;
        this.validationError = validationError;
        this.onCancel = onCancel;
    }

    public RequestTextDialogController(Consumer<String> action, String title, String inputTip,
            Function<String, Boolean> validator)
    {
        this(action, title, inputTip, validator, "", "", () -> {});
    }
    
    @FXML @Override protected void initialize()
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

        close();
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
        onCancel.run();
        close();
    }

    @Override
    protected void customizeStageBuilder(StageBuilder stageBuilder)
    {
        stageBuilder.setModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public URL getFXML()
    {
        return RequestTextDialogController.class.getResource("requestTextDialog.fxml");
    }

    @Override
    public String getTitle()
    {
        return this.title;
    }
}
