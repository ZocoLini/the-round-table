package org.lebastudios.theroundtable.database;

import lombok.SneakyThrows;
import org.lebastudios.theroundtable.AppTask;
import org.lebastudios.theroundtable.Zip;
import org.lebastudios.theroundtable.config.data.DatabaseConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.ui.TaskManager;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackupDB
{
    private static BackupDB instance;
    
    public static BackupDB getInstance()
    {
        if (instance == null) instance = new BackupDB();
        return instance;
    }
    
    ScheduledExecutorService executor;
    private boolean running = false;
            
    private BackupDB() {}

    @SneakyThrows
    public void initialize()
    {
        if (running || !new JSONFile<>(DatabaseConfigData.class).get().enableBackups) return;
        
        running = true;
        
        AppLifeCicleEvents.OnAppCloseRequest.addListener(() -> {
            stop();
            realizeBackup();
        });

        try 
        {
            executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(this::realizeBackup, 1, 1, TimeUnit.HOURS);
        }
        catch (Exception exception) {}
    }

    public void stop()
    {
        if (!running) return;
        
        running = false;
        
        executor.shutdown();
        executor.close();
    }
    
    public void realizeBackup()
    {
        TaskManager.getInstance().startNewTaskWithProgressBar(creatingBackup());
    }
    
    private AppTask creatingBackup()
    {
        return new AppTask() {
            @Override
            protected Void call()
            {
                updateMessage("Creating backup...");
                updateProgress(0, 100);
                
                var data = new JSONFile<>(DatabaseConfigData.class).get();

                File backupFolder = new File(data.backupFolder);
                File databaseFolder = new File(data.databaseFolder);
                
                if (!backupFolder.exists() && !backupFolder.mkdirs())
                {
                    updateMessage("Failed to create backup directory.");
                    updateProgress(100, 100);
                    return null;
                }
                
                if (!databaseFolder.exists() && !databaseFolder.mkdirs())
                {
                    updateMessage("Failed to create database directory.");
                    updateProgress(100, 100);
                    return null;
                }
                
                File backupFile = new File(backupFolder.getAbsolutePath() + "/" + LocalDateTime.now() + ".zip");
                
                try
                {
                    Zip.createZip(databaseFolder, backupFile);
                }
                catch (Exception e)
                {
                    updateMessage("Failed to create backup.");
                }

                updateProgress(100, 100);
                
                return null;
            }
        };
    }
}
