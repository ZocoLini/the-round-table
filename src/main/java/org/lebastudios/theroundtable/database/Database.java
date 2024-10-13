package org.lebastudios.theroundtable.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.lebastudios.theroundtable.config.data.DatabaseConfigData;
import org.lebastudios.theroundtable.config.data.JSONFile;
import org.lebastudios.theroundtable.database.entities.*;
import org.lebastudios.theroundtable.events.AppLifeCicleEvents;

import java.util.function.Consumer;

public class Database
{
    private static Database instance;
    private final SessionFactory sessionFactory = buildSessionFactory();

    private Database()
    {
        AppLifeCicleEvents.OnAppCloseRequest.addListener(sessionFactory::close);
    }

    public static Database getInstance()
    {
        if (instance == null) instance = new Database();

        return instance;
    }

    private SessionFactory buildSessionFactory()
    {
        try
        {
            // Create the SessionFactory from hibernate.cfg.xml
            var config = new Configuration().configure();

            DatabaseConfigData databaseConfigData = new JSONFile<>(DatabaseConfigData.class).get();
            
            config.getProperties().put(
                    Environment.JAKARTA_JDBC_URL, "jdbc:h2:"
                            + databaseConfigData.databaseFolder
                            + "/" + databaseConfigData.establishmentDatabaseName
            );

            config.addAnnotatedClass(Category.class)
                    .addAnnotatedClass(Product.class)
                    .addAnnotatedClass(SubCategory.class)
                    .addAnnotatedClass(Receipt.class)
                    .addAnnotatedClass(Client.class)
                    .addAnnotatedClass(Product_Receipt.class)
                    .addAnnotatedClass(Transaction.class)
                    .addAnnotatedClass(Account.class);

            return config.buildSessionFactory();
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
