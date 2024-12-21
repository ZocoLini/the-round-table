package org.lebastudios.theroundtable.database;

import java.sql.Connection;
import java.util.Arrays;

public interface IDatabaseUpdater
{
    default int getDatabaseVersion() { return 1; }
    String getDatabaseIdentifier();
    default void updateDatabase(Connection conn, int oldVersion, int newVersion) throws Exception 
    {
        for (int i = oldVersion + 1; i <= newVersion; i++)
        {
            var methodName = "version" + i;
            var updateMethod = Arrays.stream(this.getClass().getMethods())
                    .filter(method -> method.getName().equals(methodName))
                    .findFirst();

            if (updateMethod.isEmpty()) continue;

            updateMethod.get().invoke(this, conn);
        }
    }
}
