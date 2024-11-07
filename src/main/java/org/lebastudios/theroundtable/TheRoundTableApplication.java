package org.lebastudios.theroundtable;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.accounts.AccountStageController;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.config.data.CashRegisterStateData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.dialogs.InformationTextDialogController;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.locale.AppLocale;
import org.lebastudios.theroundtable.locale.LangFileLoader;
import org.lebastudios.theroundtable.locale.LangLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;
import org.lebastudios.theroundtable.setup.SetupStageController;
import org.lebastudios.theroundtable.ui.SceneBuilder;
import org.lebastudios.theroundtable.ui.TaskManager;
import org.lebastudios.theroundtable.updates.UpdateAppJar;

import java.io.File;
import java.util.Properties;

public class TheRoundTableApplication extends Application
{
    public static String getAppVersion()
    {
        try (final var pomResource = Launcher.class.getResourceAsStream(
                Environment.isDev()
                        ? "/../../../pom.xml"
                        : "/META-INF/maven/org.lebastudios.theroundtable/desktop-app/pom.properties")
        )
        {
            var properties = new Properties();

            properties.load(pomResource);

            return properties.getProperty("version");
        }
        catch (Exception e)
        {
            System.err.println("Version couldn't be laded: " + e.getMessage());
            return "2.0.2";
        }
    }

    public static String getUserDirectory()
    {
        return System.getProperty("user.home") + File.separator + (Environment.isDev()
                ? ".round-table-dev"
                : ".round-table");
    }

    public static String getAppDirectory()
    {
        return new File(Launcher.class.getProtectionDomain().getCodeSource()
                .getLocation().getFile()).getParentFile().getParent();
    }

    @SneakyThrows
    @Override
    public void start(Stage stage)
    {
        LangLoader.loadLang(Launcher.class, AppLocale.getActualLocale());

        if (SetupStageController.checkIfStart()) new SetupStageController().instantiate(true);

        new AccountStageController().instantiate(true);

        PluginLoader.loadPlugins();

        stage.setTitle("The Round Table");
        stage.getIcons().add(ImageLoader.getIcon("the-round-table-logo.png"));

        Scene mainScene = new SceneBuilder(new MainStageController().getParent()).build();
        stage.setScene(mainScene);
        stage.show();

        if (AccountManager.getInstance().isAccountAdmin())
        {
            TaskManager.getInstance().startNewTaskWithProgressBar(createCheckingForUpdateTask());
        }

        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, e ->
        {
            AppLifeCicleEvents.OnAppCloseRequest.invoke(e);
            
            if (!e.isConsumed()) 
            {
                AppLifeCicleEvents.OnAppClose.invoke(e);
                Platform.exit();
            }
        });
        
        AppLifeCicleEvents.OnAppCloseRequest.addListener(windowEvent ->
        {
            if (windowEvent.isConsumed()) return;

            checkIfTheCashReghisterIsClosed(windowEvent);
        });
    }

    private AppTask createCheckingForUpdateTask()
    {
        return new AppTask()
        {
            @Override
            protected Void call()
            {
                updateMessage("Checking for updates...");

                new UpdateAppJar().update();

                return null;
            }
        };
    }

    // TODO: Should be in the cash register plugin (Use AppLifeEvents)
    private void checkIfTheCashReghisterIsClosed(WindowEvent event)
    {
        var cashRegisterState = new JSONFile<>(CashRegisterStateData.class).get();
        if (cashRegisterState.open)
        {
            event.consume();
            new InformationTextDialogController(
                    LangFileLoader.getTranslation("textblock.needtoclosethecashregister")
            ).instantiate();
        }
    }
}