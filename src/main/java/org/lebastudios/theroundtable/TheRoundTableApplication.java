package org.lebastudios.theroundtable;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.accounts.AccountStageController;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.apparience.ThemeLoader;
import org.lebastudios.theroundtable.config.data.CashRegisterStateData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.PreferencesConfigData;
import org.lebastudios.theroundtable.dialogs.ConfirmationTextDialogController;
import org.lebastudios.theroundtable.dialogs.InformationTextDialogController;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.language.LangFileLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;
import org.lebastudios.theroundtable.setup.SetupStageController;
import org.lebastudios.theroundtable.ui.TaskManager;
import org.lebastudios.theroundtable.updates.UpdateAppJar;

import java.io.File;
import java.util.function.Consumer;

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
                && enviroment.equals("dev")) userDirectory = System.getProperty("user.home") + File.separator + ".round-table-dev";
        else userDirectory = System.getProperty("user.home") + File.separator + ".round-table";
        
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
        // TODO: loadPlugins() shold be able to load the translations.
        PluginLoader.loadPlugins();
        LangFileLoader.loadLang(
                new JSONFile<>(PreferencesConfigData.class).get().langauge,
                System.getProperty("user.country")
        );
        
        if (SetupStageController.checkIfStart())
        {
            showAndWaitInStage(SetupStageController.getParentNode(), "Setup", true, s ->
            {
                s.setOnCloseRequest(e ->
                {
                    ConfirmationTextDialogController.loadAttachedNode(
                            LangFileLoader.getTranslation("textblock.closingsetup"),
                            response ->
                            {
                                if (response) System.exit(0);
                            }
                    );
                    e.consume();
                });
                s.getIcons().add(ImageLoader.getIcon("the-round-table-logo.png"));
            });
        }
        
        showAndWaitInStage(AccountStageController.getParentNode(), "Login", true, s ->
        {
            s.setOnCloseRequest(e -> System.exit(0));
            s.getIcons().add(ImageLoader.getIcon("the-round-table-logo.png"));
        });

        Scene mainScene = createScene(MainStageController.getParentNode());
        stage.setTitle("The Round Table");
        stage.getIcons().add(ImageLoader.getIcon("the-round-table-logo.png"));
        stage.setMinWidth(720);
        stage.setMinHeight(480);
        stage.setScene(mainScene);
        stage.show();
        
        TaskManager.getInstance().startNewTaskWithProgressBar(createCheckingForUpdateTask());
        
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (windowEvent ->
        {
            if (windowEvent.isConsumed()) return; 
            
            checkIfTheCashReghisterIsClosed(windowEvent);
        }));
    }

    private AppTask createCheckingForUpdateTask()
    {
        return new AppTask() {
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
            InformationTextDialogController.loadAttachedNode(
                    LangFileLoader.getTranslation("textblock.needtoclosethecashregister")
            );
            event.consume();
        }
        else
        {
            AppLifeCicleEvents.OnAppCloseRequest.invoke();
        }
    }
    
    // TODO: Refactor and made different clases using the builder pattern
    //  Scene Builder
    //  Stage Builder
    
    public static Scene createScene(Parent root)
    {
        return ThemeLoader.addActualTheme(new Scene(root));
    }
    
    public static Stage showAndWaitInStage(Parent root, String title, boolean resizeable, Consumer<Stage> stageConsumer)
    {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(resizeable);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(ImageLoader.getIcon("the-round-table-logo.png"));
        stage.setScene(createScene(root));
        
        stageConsumer.accept(stage);
        
        stage.showAndWait();

        return stage;
    }
    
    public static Stage showAndWaitInStage(Parent root, String title, boolean resizeable)
    {
        return showAndWaitInStage(root, title, resizeable, stage -> {});
    }

    public static Stage showAndWaitInStage(Parent root, String title)
    {
        return showAndWaitInStage(root, title, false);
    }
}