package org.lebastudios.theroundtable;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.SneakyThrows;
import org.lebastudios.theroundtable.accounts.AccountManager;
import org.lebastudios.theroundtable.accounts.AccountStageController;
import org.lebastudios.theroundtable.apparience.ImageLoader;
import org.lebastudios.theroundtable.env.Directories;
import org.lebastudios.theroundtable.env.Variables;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.locale.AppLocale;
import org.lebastudios.theroundtable.locale.LangLoader;
import org.lebastudios.theroundtable.plugins.PluginLoader;
import org.lebastudios.theroundtable.setup.SetupStageController;
import org.lebastudios.theroundtable.ui.SceneBuilder;
import org.lebastudios.theroundtable.ui.TaskManager;
import org.lebastudios.theroundtable.updates.UpdateAppJar;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class TheRoundTableApplication extends Application
{
    public static String getAppVersion()
    {
        if (Variables.isDev()) 
        {
            try
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                factory.setIgnoringElementContentWhitespace(true);
                Document document = factory.newDocumentBuilder().parse(new FileInputStream(new File(
                        new File(Launcher.class.getResource("/").getFile()).getParentFile().getParentFile(),
                        "pom.xml"
                )));


                for (int i = 0; i < document.getDocumentElement().getChildNodes().getLength(); i++)
                {
                    Node node = document.getDocumentElement().getChildNodes().item(i);

                    if (node.getNodeName().equals("version")) return node.getTextContent();
                }

                return "0";
            }
            catch (Exception _) {}
        }
        else
        {
            try (final var pomResource = Launcher.class.getResourceAsStream("/META-INF/maven/org.lebastudios.theroundtable/desktop-app/pom.properties"))
            {
                var properties = new Properties();

                properties.load(pomResource);

                return properties.getProperty("version");
            }
            catch (Exception _) {}
        }
        
        return "2.5.0";
    }

    public static String getUserDirectory()
    {
        return Directories.homeDir();
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

        stage.setTitle("The Round Table - " + AccountManager.getInstance().getCurrentLoggedAccountName());
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
                System.exit(0);
            }
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
}