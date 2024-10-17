package org.lebastudios.theroundtable.events;

import org.lebastudios.theroundtable.database.entities.Account;

public final class UserEvents
{
    public static final Event1<Account> OnAccountLogIn = new Event1<>();
    public static final Event1<Account> OnAccountLogOut = new Event1<>();
}
