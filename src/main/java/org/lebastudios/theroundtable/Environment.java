package org.lebastudios.theroundtable;

import java.io.File;
import java.io.IOException;

@Deprecated
public class Environment
{
    private static Boolean isDev = null;
    private static Boolean isTest = null;
    
    public enum EnvironmentType
    {
        DEV,
        TEST,
        PROD
    }
    
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
        return !isDev() && !isTest();
    }
    
    public static boolean isTest()
    {
        if (isTest != null) return isTest;

        String enviroment = System.getenv("TRT_ENV");
        isTest = enviroment != null && enviroment.equals("test");
        return isTest;
    }
    
    public static EnvironmentType getEnvironmentType()
    {
        if (isDev()) return EnvironmentType.DEV;
        if (isTest()) return EnvironmentType.TEST;
        return EnvironmentType.PROD;
    }
}
