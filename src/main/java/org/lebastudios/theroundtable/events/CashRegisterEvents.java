package org.lebastudios.theroundtable.events;

import org.lebastudios.theroundtable.database.entities.Order;

public final class CashRegisterEvents
{
    public static Event1<Order> showOrder = new Event1<>();
}
