package org.lebastudios.theroundtable;

import java.io.File;
import java.io.IOException;

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
    
    public static String homeDir()
    {
        String environment = System.getenv("TRT_HOME");
        
        String homeDir = System.getProperty("user.home") + File.separator + ".round-table";
        
        if (environment != null) 
        {
            try
            {
                File provisionalHomeDir = new File(environment);
                provisionalHomeDir.createNewFile();
                if (!provisionalHomeDir.exists()) throw new RuntimeException("TRT_HOME file wasn't created as expected");
                
                homeDir = environment;

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return homeDir + (Environment.isDev() ? "-dev" : "");
    }
    
    public static boolean isProd()
    {
        return !isDev();
    }
}
