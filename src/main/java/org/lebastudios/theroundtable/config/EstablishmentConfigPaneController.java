package org.lebastudios.theroundtable.config;

import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.config.data.EstablishmentConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.language.LangFileLoader;

import java.io.File;
import java.io.FileInputStream;

public class EstablishmentConfigPaneController extends SettingsPaneController
{
    public TextField establishmentName;
    public TextField establishmentId;
    public TextField establishmentAddress;
    public TextField establishmentPhone;
    public TextField establishmentZipCode;
    public TextField establishmentCity;
    public ImageView establishmentLogo;

    private File imageFile;

    @Override
    public void apply()
    {
        var establishmentData = new JSONFile<>(EstablishmentConfigData.class);

        establishmentData.get().name = establishmentName.getText();
        establishmentData.get().id = establishmentId.getText();

        establishmentData.get().phone = establishmentPhone.getText();
        establishmentData.get().address = establishmentAddress.getText();

        establishmentData.get().city = establishmentCity.getText();
        establishmentData.get().zipCode = establishmentZipCode.getText();

        if (imageFile != null)
        {
            imageFile = ImageLoader.saveImageInSpecialFolder(imageFile);
            establishmentData.get().logoImgPath = imageFile.getAbsolutePath();
        }

        establishmentData.save();
    }

    @Override
    public void initialize()
    {
        var establishmentData = new JSONFile<>(EstablishmentConfigData.class);

        establishmentName.setText(establishmentData.get().name);
        establishmentId.setText(establishmentData.get().id);

        establishmentPhone.setText(establishmentData.get().phone);

        establishmentAddress.setText(establishmentData.get().address);
        establishmentCity.setText(establishmentData.get().city);
        establishmentZipCode.setText(establishmentData.get().zipCode);

        var imageFile = new File(establishmentData.get().logoImgPath);
        establishmentLogo.setOnMouseClicked(_ -> selectImage());
        establishmentLogo.setOnTouchPressed(_ -> selectImage());
        if (!imageFile.exists() || !imageFile.isFile())
        {
            establishmentLogo.setImage(ImageLoader.getIcon("no-product-image.png"));
        }
        else
        {
            establishmentLogo.setImage(ImageLoader.getSavedImage(establishmentData.get().logoImgPath));
        }
    }

    @SneakyThrows
    private void selectImage()
    {
        var fileChooser = new FileChooser();

        fileChooser.setTitle(LangFileLoader.getTranslation("title.imagechooser"));
        fileChooser.setInitialDirectory(new File(ImageLoader.SavedImagesDirectory()));
        fileChooser.setSelectedExtensionFilter(
                new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg")
        );

        var imageFile = fileChooser.showOpenDialog(null);

        if (imageFile == null) return;

        var image = new Image(new FileInputStream(imageFile));

        if (image.isError()) return;

        establishmentLogo.setImage(image);
        this.imageFile = imageFile;
    }
}
