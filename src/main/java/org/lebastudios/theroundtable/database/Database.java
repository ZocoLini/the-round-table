package org.lebastudios.theroundtable.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.lebastudios.theroundtable.config.data.DatabaseConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.database.entities.*;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;
import org.lebastudios.theroundtable.plugins.PluginLoader;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Database
{
    private static Database instance;
    private SessionFactory sessionFactory;

    private Database() {}

    public static void init()
    {
        if (getInstance().sessionFactory != null) return;

        getInstance().sessionFactory = getInstance().buildSessionFactory();
        AppLifeCicleEvents.OnAppClose.addListener((windowEvent) ->
        {
            if (windowEvent.isConsumed()) return;

            getInstance().sessionFactory.close();
        });
    }

    public static Database getInstance()
    {
        if (instance == null) instance = new Database();

        return instance;
    }

    public static File getDatabaseFile()
    {
        DatabaseConfigData databaseConfigData = new JSONFile<>(DatabaseConfigData.class).get();

        return new File(
                databaseConfigData.databaseFolder,
                databaseConfigData.establishmentDatabaseName + ".sqlite"
        );
    }

    public void reloadDatabase()
    {
        sessionFactory.close();
        sessionFactory = buildSessionFactory();
    }

    private SessionFactory buildSessionFactory()
    {
        try
        {
            var config = new Configuration().configure();

            File databaseFile = getDatabaseFile();

            databaseFile.getParentFile().mkdirs();

            config.setProperty(
                    "hibernate.connection.url", "jdbc:sqlite:" + databaseFile.getAbsolutePath()
            );

            config.addAnnotatedClass(Category.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(SubCategory.class)
                    .addAnnotatedClass(Receipt.class)
                    .addAnnotatedClass(Product_Receipt.class)
                    .addAnnotatedClass(Transaction.class)
                    .addAnnotatedClass(Account.class);

            // Loading all the plugin entities to the Hibernate configuration from the Plugins
            PluginLoader.getPluginEntities().forEach(config::addAnnotatedClass);

            // Adding the plugin ClassLoader to the Hibernate configuration
            StandardServiceRegistry serviceRegistry =
                    new StandardServiceRegistryBuilder(
                            new BootstrapServiceRegistryBuilder().applyClassLoader(PluginLoader.getPluginsClassLoader())
                                    .build())
                            .applySettings(config.getProperties())
                            .build();

            return config.buildSessionFactory(serviceRegistry);
        }
        catch (Throwable ex)
        {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void connectTransaction(Consumer<Session> action)
    {
        if (sessionFactory == null) throw new IllegalStateException("Database not initialized");

        // Ejemplo de uso de la sesión para interactuar con la base de datos
        try (Session session = sessionFactory.openSession())
        {
            session.getTransaction().begin();

            action.accept(session);

            session.flush();
            session.getTransaction().commit();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void connectQuery(Consumer<Session> action)
    {
        if (sessionFactory == null) throw new IllegalStateException("Database not initialized");

        // Ejemplo de uso de la sesión para interactuar con la base de datos
        try (Session session = sessionFactory.openSession())
        {
            action.accept(session);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
