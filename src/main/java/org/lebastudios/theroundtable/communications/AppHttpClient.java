package org.lebastudios.theroundtable.communications;

import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.config.data.SettingsData;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.util.List;

public class AppHttpClient
{
    private static AppHttpClient instance;

    public static AppHttpClient getInstance()
    {
        if (instance == null) instance = new AppHttpClient();

        return instance;
    }

    private HttpClient client;

    private AppHttpClient() {}

    public HttpClient getClient()
    {
        if (client == null) client = newClient();

        return client;
    }

    public HttpClient newClient()
    {
        HttpClient.Builder client = HttpClient.newBuilder();

        // Cargar la configuración del proxy desde el archivo JSON
        var proxy = new JSONFile<>(SettingsData.class).get().proxyData;

        // Si no se debe usar el proxy, retornar el cliente sin configuración de proxy
        if (proxy == null || !proxy.usingProxy) {
            return client.build();
        }

        // Configurar el ProxySelector
        client.proxy(new ProxySelector() {
            @Override
            public List<Proxy> select(URI uri) {
                // Construir la dirección del proxy
                String proxyAddress = proxy.proxyAddress;
                int proxyPort = proxy.proxyPort;

                // Crear un SocketAddress usando InetSocketAddress
                SocketAddress socketAddress = new InetSocketAddress(proxyAddress, proxyPort);

                // Retornar un único Proxy para "http" y "https"
                return List.of(new Proxy(Proxy.Type.HTTP, socketAddress));
            }

            @Override
            public void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
                // Manejar la conexión fallida si es necesario
            }
        });

        // Devolver el cliente configurado
        return client.build();
    }
}
