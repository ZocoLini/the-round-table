package org.lebastudios.theroundtable.logs;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

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
        final var date = getDateString();

        switch (type)
        {
            case INFO -> System.out.println(date + " [INFO] " + message);
            case WARNING -> System.out.println(date + " [WARNING] " + message);
            case ERROR -> System.err.println(date + " [ERROR] " + message);
        }
    }
    
    public void log(String message, Exception e)
    {
        final var date = getDateString();

        System.err.println(date + " [EXCEPTION] " + message + " (" + e.getMessage() + ")");
    }

    private static String getDateString()
    {
        return java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }


}
