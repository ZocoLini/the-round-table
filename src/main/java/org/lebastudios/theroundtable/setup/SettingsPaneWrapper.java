package org.lebastudios.theroundtable.setup;

import javafx.fxml.FXMLLoader;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.config.SettingsPaneController;
import org.lebastudios.theroundtable.locale.LangBundleLoader;

public class SettingsPaneWrapper extends SetupPaneController
{
    private final SettingsPaneController controller;

    @SneakyThrows
    public SettingsPaneWrapper(String paneName)
    {
        FXMLLoader loader = new FXMLLoader(SettingsPaneController.class.getResource(paneName));
        LangBundleLoader.loadLang(loader, Launcher.class);
        loader.load();
        this.controller = loader.getController();
        this.root = controller.getRoot();
    }

    @Override
    public void apply()
    {
        controller.apply();
    }

    @Override
    public boolean validateData()
    {
        return true;
    }
}
