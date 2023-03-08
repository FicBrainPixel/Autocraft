package com.fbp.autocraft;

import static com.fbp.autocraft.DeviceList.modelNumber;
import static com.fbp.autocraft.MainPage.TAG;
import static com.fbp.autocraft.MainPage.databaseHandler;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class DeviceSetup extends AppCompatActivity
{
    int i, j;
    boolean visibilityStatus;
    String tempText;
    static boolean isNameChanged;
    boolean isEditingMode;
    ImageView status, back, visibility;
    EditText customName;
    int[] cardId = {R.id.fanOneCard,R.id.fanTwoCard,R.id.socketOneCard,R.id.socketTwoCard,R.id.lightOneCard,R.id.lightTwoCard,R.id.lightThreeCard,R.id.lightFourCard},
          textId = {R.id.fanOne,R.id.fanTwo,R.id.socketOne,R.id.socketTwo,R.id.lightOne,R.id.lightTwo,R.id.lightThree,R.id.lightFour};
    ArrayList<String> resId;
    TextView type, tempTextview;
    TextView[] button;
    ViewFlipper selectModel;
    DeviceModelData deviceModelData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_setup);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        selectModel = findViewById(R.id.selectModel);
        type = findViewById(R.id.type);
        back = findViewById(R.id.back);
        status = findViewById(R.id.status);
        visibility = findViewById(R.id.visibility);

        selectModel.setDisplayedChild(modelNumber);

        customName = (EditText) ((ConstraintLayout) selectModel.getChildAt(modelNumber)).getViewById(R.id.name);

        button = new TextView[textId.length];
        resId = new ArrayList<>();

        for(i=0;i<textId.length;i++)
        {
            button[i] = (TextView) ((CardView) ((ConstraintLayout) ((ConstraintLayout) selectModel.getChildAt(modelNumber)).getViewById(R.id.button_list)).getViewById(cardId[i])).getChildAt(1);
        }

        isEditingMode = DeviceController.deviceModelData != null;

        deviceModelData = isEditingMode ? DeviceController.deviceModelData : new DeviceModelData();

        resId.add(deviceModelData.fanOne);
        resId.add(deviceModelData.fanTwo);
        resId.add(deviceModelData.socketOne);
        resId.add(deviceModelData.socketTwo);
        resId.add(deviceModelData.lightOne);
        resId.add(deviceModelData.lightTwo);
        resId.add(deviceModelData.lightThree);
        resId.add(deviceModelData.lightFour);

        if(isEditingMode)
        {
            status.setImageResource(R.drawable.save);
            customName.setText(deviceModelData.customName);
            visibilityStatus = deviceModelData.enabled == 1;
            visibility.setImageResource(visibilityStatus ? R.drawable.visible : R.drawable.invisible);
        }

        for(i=0;i<textId.length;i++)
        {
            if(button[i] != null)
            {
                button[i].setOnClickListener(v ->
                {
                    for(j=0;j<textId.length;j++)
                    {
                        if(v.getId() == button[j].getId())
                        {
                            swap(button[j]);
                        }
                    }
                });

                if(isEditingMode)
                {
                    button[i].setText(resId.get(i));
                }
            }
        }

        status.setOnClickListener(v ->
        {
            if(customName.getText().toString().isEmpty())
            {
                Snackbar.make(findViewById(android.R.id.content), "Name cannot be empty", Snackbar.LENGTH_LONG).show();
                return;
            }
            else
            {
                deviceModelData.setCustomName(customName.getText().toString());
            }

            if(isEditingMode)
            {
                isNameChanged = true;
                deviceModelData.setMacAddress(CustomDeviceAdapter.macAddress);
            }
            else
            {
                deviceModelData.setMacAddress(ScanBluetooth.macAddress);
                deviceModelData.setDeviceName(DeviceList.modelName);
            }

            if(button[0] != null)
            {
                deviceModelData.setFanOne(button[0].getText().toString());
            }
            if(button[1] != null)
            {
                deviceModelData.setFanTwo(button[1].getText().toString());
            }
            if(button[2] != null)
            {
                deviceModelData.setSocketOne(button[2].getText().toString());
            }
            if(button[3] != null)
            {
                deviceModelData.setSocketTwo(button[3].getText().toString());
            }
            if(button[4] != null)
            {
                deviceModelData.setLightOne(button[4].getText().toString());
            }
            if(button[5] != null)
            {
                deviceModelData.setLightTwo(button[5].getText().toString());
            }
            if(button[6] != null)
            {
                deviceModelData.setLightThree(button[6].getText().toString());
            }
            if(button[7] != null)
            {
                deviceModelData.setLightFour(button[7].getText().toString());
            }

            if(CustomDeviceAdapter.macAddress != null && databaseHandler.updateDevice(deviceModelData) > 0)
            {
                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            }
            else
            {
                databaseHandler.addDevice(deviceModelData);
                setResult(RESULT_OK);
            }

            finish();
        });

        visibility.setOnClickListener(v ->
        {
            visibilityStatus = !visibilityStatus;
            deviceModelData.setEnabled(visibilityStatus ? 1 : 0);
            visibility.setImageResource(visibilityStatus ? R.drawable.visible : R.drawable.invisible);
        });

        back.setOnClickListener(v -> finish());
    }

    private void swap(TextView swapText)
    {
        if(tempTextview == null || !(tempTextview.getText().toString().charAt(0) == swapText.getText().toString().charAt(0)))
        {
            tempTextview = swapText;
            type.setText(tempTextview.getText().toString());
        }
        else if(tempTextview.getText().toString().equals(swapText.getText().toString()))
        {
            tempTextview = null;
            type.setText("--");
        }
        else
        {
            if(tempTextview.getText().toString().charAt(0) == swapText.getText().toString().charAt(0))
            {
                tempText = swapText.getText().toString();
                swapText.setText(tempTextview.getText().toString());
                tempTextview.setText(tempText);

                type.setText("--");
                tempTextview = null;
            }
            else
            {
                Log.d(TAG, "Theek kardo");
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}