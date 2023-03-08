package com.fbp.autocraft;

import java.util.HashMap;

public class DeviceDetails
{
    HashMap<String, Integer> deviceImageId, devicePosition;

    public DeviceDetails()
    {
        deviceImageId = new HashMap<>();
        devicePosition = new HashMap<>();

        deviceImageId.put("A1", R.drawable.home);
        deviceImageId.put("A2", R.drawable.devices);
        deviceImageId.put("A3", R.drawable.store);
        deviceImageId.put("A4", R.drawable.settings);
        deviceImageId.put("A5", R.drawable.f_up);
        deviceImageId.put("A6", R.drawable.f_down);

        devicePosition.put("A1", 0);
        devicePosition.put("A2", 1);
        devicePosition.put("A3", 2);
        devicePosition.put("A4", 3);
        devicePosition.put("A5", 4);
        devicePosition.put("A6", 5);
    }
}
