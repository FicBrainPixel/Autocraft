package com.fbp.autocraft;

public class DeviceModelData
{
    public static final String MODEL_ONE = "A1", MODEL_TWO = "A2", MODEL_THREE = "A3", MODEL_FOUR = "A4",
        MODEL_FIVE = "A5", MODEL_SIX = "A6";
    int id, enabled;
    String macAddress, deviceName, customName, fanOne, fanTwo, socketOne, socketTwo, lightOne, lightTwo, lightThree, lightFour;
    public DeviceModelData() {}

    public DeviceModelData(String macAddress, String deviceName, String customName, int enabled, String fanOne, String fanTwo, String socketOne, String socketTwo, String lightOne, String lightTwo, String lightThree, String lightFour)
    {
        this.macAddress = macAddress;
        this.deviceName = deviceName;
        this.customName = customName;
        this.enabled = enabled;
        this.fanOne = fanOne;
        this.fanTwo = fanTwo;
        this.socketOne = socketOne;
        this.socketTwo = socketTwo;
        this.lightOne = lightOne;
        this.lightTwo = lightTwo;
        this.lightThree = lightThree;
        this.lightFour = lightFour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMacAddress()
    {
        return macAddress;
    }
    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }
    public String getDeviceName()
    {
        return deviceName;
    }
    public void setDeviceName(String deviceName)
    {
        this.deviceName = deviceName;
    }
    public String getCustomName()
    {
        return customName;
    }
    public void setCustomName(String customName)
    {
        this.customName = customName;
    }
    public int getEnabled()
    {
        return enabled;
    }
    public void setEnabled(int enabled)
    {
        this.enabled = enabled;
    }
    public String getFanOne()
    {
        return fanOne;
    }
    public void setFanOne(String fanOne)
    {
        this.fanOne = fanOne;
    }
    public String getFanTwo()
    {
        return fanTwo;
    }
    public void setFanTwo(String fanTwo)
    {
        this.fanTwo = fanTwo;
    }
    public String getSocketOne()
    {
        return socketOne;
    }
    public void setSocketOne(String socketOne)
    {
        this.socketOne = socketOne;
    }
    public String getSocketTwo()
    {
        return socketTwo;
    }
    public void setSocketTwo(String socketTwo)
    {
        this.socketTwo = socketTwo;
    }
    public String getLightOne()
    {
        return lightOne;
    }
    public void setLightOne(String lightOne)
    {
        this.lightOne = lightOne;
    }
    public String getLightTwo()
    {
        return lightTwo;
    }
    public void setLightTwo(String lightTwo)
    {
        this.lightTwo = lightTwo;
    }
    public String getLightThree()
    {
        return lightThree;
    }
    public void setLightThree(String lightThree)
    {
        this.lightThree = lightThree;
    }
    public String getLightFour()
    {
        return lightFour;
    }
    public void setLightFour(String lightFour)
    {
        this.lightFour = lightFour;
    }
}