package com.fbp.autocraft;

import static com.fbp.autocraft.MainPage.databaseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class DeviceFragment extends Fragment
{
    View view;

    static ImageView noDevice;
    static GridView gridView;
    static DeviceDetails deviceDetails;
    SearchView searchView;
    static ArrayList<CustomDeviceData> customDeviceData;
    static CustomDeviceAdapter customDeviceAdapter;
    static List<DeviceModelData> deviceModelData;
    public DeviceFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_device, container, false);

        searchView = view.findViewById(R.id.search_view);
        gridView = view.findViewById(R.id.grid_list);
        noDevice = view.findViewById(R.id.no_device);

        deviceDetails = new DeviceDetails();
        customDeviceData = new ArrayList<>();

        getData(getContext());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText)
            {
                customDeviceAdapter.getFilter().filter(searchText);
                return false;
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

        return view;
    }

    static public void getData(Context context)
    {
        customDeviceData = new ArrayList<>();
        deviceModelData = databaseHandler.getAllDevices();

        for(DeviceModelData deviceModelDataCopy : deviceModelData)
        {
            //noinspection ConstantConditions
            customDeviceData.add(new CustomDeviceData(deviceModelDataCopy.macAddress, deviceModelDataCopy.customName, deviceDetails.deviceImageId.get(deviceModelDataCopy.deviceName)));
        }

        customDeviceAdapter = new CustomDeviceAdapter(context, customDeviceData);
        gridView.setAdapter(customDeviceAdapter);

        noDevice.setVisibility(customDeviceData.size() == 0 ? View.VISIBLE : View.GONE);
    }
}