package org.lebastudios.theroundtable.accounts;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LocalPasswordValidator
{
    public static boolean isValidFormat(String password)
    {
        return password.length() >= 8;
    }
    
    public static String hashPassword(String password)
    {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
    
    public static boolean validatePassword(String password, String hash)
    {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }
}
