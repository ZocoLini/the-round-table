package org.lebastudios.theroundtable.communications;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Net
{
    public static boolean isConnectionAvailable()
    {
        try
        {
            InetAddress address = InetAddress.getByName("lebastudios.org");
            return address != null && !address.toString().isEmpty();
        }
        catch (UnknownHostException e)
        {
            // Manejar excepciones de resolución de nombre
            e.printStackTrace();
            return false;
        }
    }
}
