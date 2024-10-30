package org.lebastudios.theroundtable.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.lebastudios.theroundtable.config.data.DatabaseConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.database.entities.*;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;

import java.io.File;
import java.util.function.Consumer;

public class Database
{
    private static Database instance;
    private SessionFactory sessionFactory = buildSessionFactory();

    private Database()
    {
        AppLifeCicleEvents.OnAppCloseRequest.addListener(sessionFactory::close);
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
            var config = new Configuration();

            File databaseFile = getDatabaseFile();
            
            databaseFile.mkdirs();
            
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

            return config.configure().buildSessionFactory();
        }
        catch (Throwable ex)
        {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public void connectTransaction(Consumer<Session> action)
    {
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
