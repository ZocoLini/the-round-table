package org.lebastudios.theroundtable.communications;

import org.lebastudios.theroundtable.plugins.IPlugin;

import java.io.IOException;
import java.net.ServerSocket;

public class PortDistributor
{
    private static PortDistributor instance;
    private static int actualPort = 60000;

    public static PortDistributor getInstance()
    {
        if (instance == null) instance = new PortDistributor();

        return instance;
    }

    private PortDistributor() {}

    public synchronized int requestAvailablePort(IPlugin _plugin)
    {
        do
        {
            actualPort--;
        } while (!isPortAvailable(actualPort));

        return actualPort;
    }

    private boolean isPortAvailable(int port)
    {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
