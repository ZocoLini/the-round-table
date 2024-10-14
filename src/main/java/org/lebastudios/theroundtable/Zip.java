package org.lebastudios.theroundtable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip
{
    public static void createZip(File source, File destination)
    {
        try (var fos = new FileOutputStream(destination);
             var zos = new ZipOutputStream(fos))
        {
            var buffer = new byte[1024];
            var files = source.listFiles();
            for (var file : files)
            {
                var fis = new FileInputStream(file);
                zos.putNextEntry(new ZipEntry(file.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0)
                {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
                fis.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
