package org.lebastudios.theroundtable;

import javafx.application.Application;
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

public class TheRoundTableApplication extends Application
{
    private static String APP_VERSION;

    public static String getAppVersion()
    {
        if (APP_VERSION != null) return APP_VERSION;

        try
        {
            var properties = new java.util.Properties();
            properties.load(TheRoundTableApplication.class.getResourceAsStream(
                    "/META-INF/maven/org.lebastudios/the-round-table/pom.properties"));
            APP_VERSION = properties.getProperty("version");
        }
        catch (Exception e)
        {
            APP_VERSION = "?";
        }

        return APP_VERSION;
    }

    private static String userDirectory;

    public static String getUserDirectory()
    {
        if (userDirectory != null) return userDirectory;

        String enviroment = System.getenv("ENVIRONMENT");
        if (enviroment != null
                && enviroment.equals("dev"))
        {userDirectory = System.getProperty("user.home") + File.separator + ".round-table-dev";}
        else {userDirectory = System.getProperty("user.home") + File.separator + ".round-table";}

        return userDirectory;
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

        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (windowEvent ->
        {
            if (windowEvent.isConsumed()) return;

            checkIfTheCashReghisterIsClosed(windowEvent);
        }));
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

    private void checkIfTheCashReghisterIsClosed(WindowEvent event)
    {
        var cashRegisterState = new JSONFile<>(CashRegisterStateData.class).get();
        if (cashRegisterState.open)
        {
            new InformationTextDialogController(
                    LangFileLoader.getTranslation("textblock.needtoclosethecashregister")
            ).instantiate();
            event.consume();
        }
        else
        {
            AppLifeCicleEvents.OnAppCloseRequest.invoke();
        }
    }
}