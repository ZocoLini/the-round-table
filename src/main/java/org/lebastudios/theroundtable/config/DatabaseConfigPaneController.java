package org.lebastudios.theroundtable.config;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.lebastudios.theroundtable.Launcher;
import org.lebastudios.theroundtable.TheRoundTableApplication;
import org.lebastudios.theroundtable.apparience.UIEffects;
import org.lebastudios.theroundtable.config.data.DatabaseConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.database.BackupDB;
import org.lebastudios.theroundtable.database.Database;
import org.lebastudios.theroundtable.dialogs.InformationTextDialogController;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class DatabaseConfigPaneController extends SettingsPaneController
{
    @FXML private TextField numMaxBackups;
    @FXML private VBox backupSection;
    @FXML private Label databasesDirectory;
    @FXML private CheckBox enableBackups;
    @FXML private Label databasesBackupDirectory;

    @Override
    @FXML protected void initialize()
    {
        var data = new JSONFile<>(DatabaseConfigData.class).get();

        databasesDirectory.setText(data.databaseFolder);
        enableBackups.setSelected(data.enableBackups);
        databasesBackupDirectory.setText(data.backupFolder);
        
        numMaxBackups.setText(new JSONFile<>(DatabaseConfigData.class).get().numMaxBackups + "");
        
        backupSection.disableProperty().bind(enableBackups.selectedProperty().not());
    }

    @Override
    public void apply()
    {
        if (!validateValues()) return;
        
        var data = new JSONFile<>(DatabaseConfigData.class);

        // When database directory changes
        try
        {
            updateDatabaseDirectory(data.get());
            updateBackupDirectory(data.get());
        }
        catch (Exception e)
        {
            new InformationTextDialogController("ERROR: " + e.getMessage()).instantiate();
            return;
        }

        data.get().enableBackups = enableBackups.isSelected();

        data.get().numMaxBackups = Integer.parseInt(numMaxBackups.getText());
        
        data.save();

        if (data.get().enableBackups) {BackupDB.getInstance().initialize();}
        else {BackupDB.getInstance().stop();}
    }

    private boolean validateValues()
    {
        try
        {
            int numMaxBackupsValue = Integer.parseInt(numMaxBackups.getText());
            
            if (numMaxBackupsValue < 1) throw new IllegalStateException();
            
        }
        catch (Exception exception)
        {
            UIEffects.shakeNode(numMaxBackups);
            return false;
        }
        
        return true;
    }
    
    private void updateDatabaseDirectory(DatabaseConfigData data)
    {
        if (!databasesDirectory.getText().equals(data.databaseFolder))
        {
            File oldDirectory = new File(data.databaseFolder);
            File newDirectory = new File(databasesDirectory.getText());

            if (!newDirectory.exists() && !newDirectory.mkdirs())
            {
                throw new RuntimeException("Failed to create new database directory.");
            }

            File databaseFile = Database.getDatabaseFile();
            
            if (!databaseFile.exists()) return;
            
            databaseFile.renameTo(new File(newDirectory.getAbsolutePath(), databaseFile.getName()));

            data.databaseFolder = databasesDirectory.getText();
            
            try
            {
                if (oldDirectory.list().length == 0) oldDirectory.delete();
            }
            catch (Exception ignore) {}
            
            Database.getInstance().reloadDatabase();
        }
    }

    private void updateBackupDirectory(DatabaseConfigData data)
    {
        if (!databasesBackupDirectory.getText().equals(data.backupFolder))
        {
            File oldDirectory = new File(data.backupFolder);
            File newDirectory = new File(databasesBackupDirectory.getText());

            if (!newDirectory.exists() && !newDirectory.mkdirs())
            {
                throw new RuntimeException("Failed to create new backup directory.");
            }

            if (!oldDirectory.exists()) return;
            
            Arrays.stream(oldDirectory.listFiles())
                    .filter(file -> file.isFile() && file.getName().endsWith(".zip"))
                    .forEach(file -> file.renameTo(new File(newDirectory.getAbsolutePath(), file.getName())));
            
            data.backupFolder = databasesBackupDirectory.getText();

            try
            {
                if (oldDirectory.list().length == 0) oldDirectory.delete();
            }
            catch (Exception exception) {}
        }
    }

    @FXML
    private void selectDatabasesBackupDirectory(ActionEvent actionEvent)
    {
        File path = getDirectoryChooser("Select Backup Directory").showDialog(getStage());
        if (path == null) return;

        databasesBackupDirectory.setText(path.getAbsolutePath());
    }

    @FXML
    private void selectDatabasesDirectory(ActionEvent actionEvent)
    {
        File path = getDirectoryChooser("Select Databases Directory").showDialog(getStage());
        if (path == null) return;

        databasesDirectory.setText(path.getAbsolutePath());
    }

    private DirectoryChooser getDirectoryChooser(String title)
    {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(TheRoundTableApplication.getUserDirectory()));
        directoryChooser.setTitle(title);
        return directoryChooser;
    }

    @Override
    public Class<?> getBundleClass()
    {
        return Launcher.class;
    }

    @Override
    public boolean hasFXMLControllerDefined()
    {
        return true;
    }

    @Override
    public URL getFXML()
    {
        return AboutConfigPaneController.class.getResource("databaseConfigPane.fxml");
    }
}
