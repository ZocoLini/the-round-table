package org.lebastudios.theroundtable;

public class Environment
{
    private static Boolean isDev = null;
    
    public static boolean isDev()
    {
        if (isDev != null) return isDev;
        
        String enviroment = System.getenv("TRT_ENV");
        isDev = enviroment != null && enviroment.equals("dev");
        return isDev;
    }
    
    public static boolean isProd()
    {
        return !isDev();
    }
}
