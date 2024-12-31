package org.lebastudios.theroundtable.logs;

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
}
