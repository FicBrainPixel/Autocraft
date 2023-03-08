package com.fbp.autocraft;

import static android.app.Activity.RESULT_OK;
import static com.fbp.autocraft.MainPage.TAG;
import static com.fbp.autocraft.MainPage.databaseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment
{
    View view;
    ImageView addDevice, noDevice;
    RecyclerView recyclerView;
    DeviceModelData deviceModelData;
    DeviceModelAdapter deviceModelAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ArrayList<DeviceModelData> list;
    List<DeviceModelData> deviceModelDataList;
    HomeFragment() {}

    
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        addDevice = view.findViewById(R.id.add_device);
        noDevice = view.findViewById(R.id.no_device);
        recyclerView = view.findViewById(R.id.recycle_view);

        getData();

        deviceModelAdapter = new DeviceModelAdapter(view.getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(deviceModelAdapter);

        addDevice.setOnClickListener(v -> activityResultLauncher.launch(new Intent(getContext(), DeviceList.class)));

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() == RESULT_OK)
            {
                deviceModelData = databaseHandler.getAllDevices().get(list.size());

                if(deviceModelData.enabled == 1)
                {
                    list.add(new DeviceModelData(deviceModelData.macAddress, deviceModelData.deviceName,
                            deviceModelData.customName, deviceModelData.enabled, deviceModelData.fanOne,
                            deviceModelData.fanTwo, deviceModelData.socketOne, deviceModelData.socketTwo,
                            deviceModelData.lightOne, deviceModelData.lightTwo, deviceModelData.lightThree,
                            deviceModelData.lightFour));

                    noDevice.setVisibility(View.GONE);

                    deviceModelAdapter.notifyDataSetChanged();
                }
            }
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) ->
        {
            if(keyCode == KeyEvent.KEYCODE_BACK)
            {
                ((Activity)view.getContext()).finishAffinity();
                return true;
            }
            return false;
        });

        //databaseHandler.deleteDevice(1);

        return view;
    }

    private void getData()
    {
        list = new ArrayList<>();
        deviceModelDataList = databaseHandler.getAllDevices();

        for(DeviceModelData deviceModelData : deviceModelDataList)
        {
            if(deviceModelData.enabled == 1)
            {
                list.add(new DeviceModelData(deviceModelData.macAddress, deviceModelData.deviceName,
                        deviceModelData.customName, deviceModelData.enabled, deviceModelData.fanOne,
                        deviceModelData.fanTwo, deviceModelData.socketOne, deviceModelData.socketTwo,
                        deviceModelData.lightOne, deviceModelData.lightTwo, deviceModelData.lightThree,
                        deviceModelData.lightFour));
            }
        }

        noDevice.setVisibility(list.size() == 0 ? View.VISIBLE : View.INVISIBLE);
    }
}