package org.lebastudios.theroundtable.env;

public class Variables
{
    private static EnvironmentType envType = detectEnvironmentType();
    
    public enum EnvironmentType
    {
        DEV,
        TEST,
        PROD
    }
    
    public static boolean isDev()
    {
        return getEnvironmentType() == EnvironmentType.DEV;
    }
    
    public static boolean isProd()
    {
        return getEnvironmentType() == EnvironmentType.PROD;
    }
    
    public static boolean isTest()
    {
        return getEnvironmentType() == EnvironmentType.TEST;
    }
    
    public static EnvironmentType getEnvironmentType()
    {
        if (envType == null) 
        {
            envType = detectEnvironmentType();
        }
        
        return envType;
    }
    
    private static EnvironmentType detectEnvironmentType()
    {
        String enviroment = System.getenv("TRT_ENV");
        
        if (enviroment == null) return EnvironmentType.PROD;
        
        return switch (enviroment)
        {
            case "dev" -> EnvironmentType.DEV;
            case "test" -> EnvironmentType.TEST;
            default -> EnvironmentType.PROD;
        };
    }
}
