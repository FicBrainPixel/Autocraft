package com.fbp.autocraft;

public class CustomDeviceData
{
    int image;
    String name, macAddress;

    public CustomDeviceData(String macAddress, String name, int image)
    {
        this.macAddress = macAddress;
        this.name = name;
        this.image = image;
    }

    public String getMacAddress()
    {
        return macAddress;
    }

    public String getName()
    {
        return name;
    }

    public int getImage()
    {
        return image;
    }
}