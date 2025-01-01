package org.lebastudios.theroundtable.logs;

import java.time.format.DateTimeFormatter;

public class Logs
{
    private static Logs instance;
    public enum LogType { INFO, WARNING, ERROR }
    
    public static Logs getInstance()
    {
        if (instance == null) instance = new Logs();
        
        return instance;
    }
    
    private Logs() {}
    
    public void log(LogType type, String message)
    {
        String date = java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("O dd/MM/yyyy HH:mm:ss"));
        
        switch (type)
        {
            case INFO -> System.out.println(date + " [INFO] " + message);
            case WARNING -> System.out.println(date + " [WARNING] " + message);
            case ERROR -> System.err.println(date + " [ERROR] " + message);
        }
    }
}
