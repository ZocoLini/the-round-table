package org.lebastudios.theroundtable.config.data;

import org.lebastudios.theroundtable.config.Settings;

import java.io.File;

public class EstablishmentConfigData implements FileRepresentator
{
    public String name = "Establishment Name";
    public String id = "Something";
    public String logoImgPath = "";
    public String address = "Your Direcction";
    public String city = "City";
    public String zipCode = "12345";
    public String phone = "+1 (234) 345 1235";

    @Override
    public File getFile()
    {
        return new File(Settings.getGlobalDir() + "/establishment.json");
    }
}
