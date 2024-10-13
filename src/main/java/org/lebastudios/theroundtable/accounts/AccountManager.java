package org.lebastudios.theroundtable.accounts;

import lombok.Getter;
import lombok.Setter;
import org.lebastudios.theroundtable.database.entities.Account;

@Setter
@Getter
public class AccountManager
{
    private static AccountManager instance;
    private Account currentLogged;

    private AccountManager() {}

    public static AccountManager getInstance()
    {
        if (instance == null) instance = new AccountManager();

        return instance;
    }

    public boolean isAccountValidToSignRecips()
    {
        if (currentLogged == null) return false;

        return currentLogged.getType() != Account.AccountType.ROOT
                && currentLogged.getType() != Account.AccountType.ACCOUNTANT;
    }
    
    public boolean isAccountAdmin()
    {
        if (currentLogged == null) return false;

        return currentLogged.getType() == Account.AccountType.ROOT 
                || currentLogged.getType() == Account.AccountType.ADMIN;
    }
    
    public String getCurrentLoggedAccountName()
    {
        return currentLogged == null ? "default" : currentLogged.getName();
    }

}
