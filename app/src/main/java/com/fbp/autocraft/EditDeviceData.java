package com.fbp.autocraft;

import static com.fbp.autocraft.MainPage.TAG;
import static com.fbp.autocraft.MainPage.context;
import static com.fbp.autocraft.MainPage.databaseHandler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;

public class EditDeviceData extends AppCompatActivity
{
    ConstraintLayout editDevice;
    SwitchCompat deviceStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_device_data);

        editDevice = findViewById(R.id.editDevice);
        deviceStatus = findViewById(R.id.status);

        editDevice.setOnClickListener(v -> startActivity(new Intent(this, DeviceSetup.class)));

        deviceStatus.setChecked(databaseHandler.getAllDevices().get(CustomDeviceAdapter.index).enabled == 1);

        deviceStatus.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            if(databaseHandler.updateDeviceVisibility(CustomDeviceAdapter.macAddress, isChecked ? 1 : 0) < 1)
            {
                Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        DeviceFragment.getData(context);
        finish();
    }
}