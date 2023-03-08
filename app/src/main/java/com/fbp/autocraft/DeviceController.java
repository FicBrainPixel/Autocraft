package com.fbp.autocraft;

import static com.fbp.autocraft.DataExchange.pendingMessage;
import static com.fbp.autocraft.DataExchange.send;
import static com.fbp.autocraft.MainPage.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class DeviceController extends AppCompatActivity
{
    int i, j;
    String macAddress;
    ImageView back, addShortcut, settings;
    TextView[] button;
    int[] cardId = {R.id.fanOneCard,R.id.fanTwoCard,R.id.socketOneCard,R.id.socketTwoCard,R.id.lightOneCard,R.id.lightTwoCard,R.id.lightThreeCard,R.id.lightFourCard},
          textId = {R.id.fanOne,R.id.fanTwo,R.id.socketOne,R.id.socketTwo,R.id.lightOne,R.id.lightTwo,R.id.lightThree,R.id.lightFour};
    ArrayList<String> resId;
    ViewFlipper selectModel;
    DeviceDetails deviceDetails;
    static DeviceModelData deviceModelData;
    DatabaseHandler databaseHandler;
    @SuppressLint("StaticFieldLeak")
    static DataExchange dataExchange;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.device_controller);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

            back = findViewById(R.id.back);
            settings = findViewById(R.id.settings);
            selectModel = findViewById(R.id.selectModel);
            addShortcut = findViewById(R.id.add_shortcut);

            resId = new ArrayList<>();
            button = new TextView[textId.length];

            deviceDetails = new DeviceDetails();

            dataExchange = new DataExchange(this);
            databaseHandler = new DatabaseHandler(this);

            if(getIntent().getStringExtra("MAC") != null)
            {
                back.setVisibility(View.INVISIBLE);
                settings.setVisibility(View.INVISIBLE);
                addShortcut.setVisibility(View.INVISIBLE);
            }

            macAddress = CustomDeviceAdapter.macAddress != null ? CustomDeviceAdapter.macAddress : getIntent().getStringExtra("MAC");

            if(macAddress == null)
            {
                deviceModelData = databaseHandler.getAllDevices().get(CustomDeviceAdapter.index);
            }
            else
            {
                for(DeviceModelData copyDeviceModelData : databaseHandler.getAllDevices())
                {
                    if(macAddress.equals(copyDeviceModelData.macAddress))
                    {
                        deviceModelData = copyDeviceModelData;
                    }
                }
            }

            Log.d(TAG,deviceModelData.macAddress + " " + deviceModelData.deviceName);

            for(i=0;i<textId.length;i++)
            {
                //noinspection ConstantConditions
                button[i] = (TextView) ((CardView) ((ConstraintLayout) ((ConstraintLayout) selectModel.getChildAt(new DeviceDetails().devicePosition.get(deviceModelData.deviceName))).getViewById(R.id.button_list)).getViewById(cardId[i])).getChildAt(1);
            }

            //noinspection ConstantConditions
            selectModel.setDisplayedChild(deviceDetails.devicePosition.get(deviceModelData.deviceName));
            //noinspection ConstantConditions
            ((EditText) ((ConstraintLayout) selectModel.getChildAt(deviceDetails.devicePosition.get(deviceModelData.deviceName))).getViewById(R.id.name)).setText(deviceModelData.customName);

            resId.add(deviceModelData.fanOne);
            resId.add(deviceModelData.fanTwo);
            resId.add(deviceModelData.socketOne);
            resId.add(deviceModelData.socketTwo);
            resId.add(deviceModelData.lightOne);
            resId.add(deviceModelData.lightTwo);
            resId.add(deviceModelData.lightThree);
            resId.add(deviceModelData.lightFour);

            for(i=0;i<textId.length;i++)
            {
                if(button[i] != null)
                {
                    button[i].setText(resId.get(i));
                    button[i].setOnClickListener(v ->
                    {
                        for(j=0;j<textId.length;j++)
                        {
                            if(v.getId() == button[j].getId())
                            {
                                pendingMessage = button[j].getText().toString();
                                sendOrConnect();
                            }
                        }
                    });
                }
            }

            addShortcut.setOnClickListener(v ->
            {
                if (ShortcutManagerCompat.isRequestPinShortcutSupported(getApplicationContext()))
                {
                    ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(getApplicationContext(), deviceModelData.macAddress)
                        .setIntent(new Intent(getApplicationContext(), DeviceController.class).putExtra("MAC", deviceModelData.macAddress).putExtra("Device Name", deviceModelData.deviceName).setAction(Intent.ACTION_MAIN))
                        .setShortLabel(deviceModelData.customName)
                        .setIcon(IconCompat.createWithResource(getApplicationContext(), R.mipmap.logo))
                        .build();
                    ShortcutManagerCompat.requestPinShortcut(getApplicationContext(), shortcutInfo, null);
                }
                else
                {
                    Toast.makeText(this,"Launcher does not support short cut icon",Toast.LENGTH_LONG).show();
                }
            });

            settings.setOnClickListener(v -> startActivity(new Intent(this, DeviceSetup.class)));

            back.setOnClickListener(v -> finish());
        }
        catch (Exception e)
        {
            Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
        }
    }
    public static void sendData(Context context, String message)
    {
        try
        {
            Log.d(TAG, String.valueOf(dataExchange.getState()));
            if (dataExchange.getState() == DataExchange.STATE_CONNECTED)
            {
                if (message.length() > 0)
                {
                    byte[] send = message.getBytes();
                    dataExchange.write(send);
                    Log.d(TAG, Arrays.toString(send));
                }
            }
            else
            {
                Toast.makeText(context, "Connection was lost!", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Toast.makeText(context, String.valueOf(e), Toast.LENGTH_SHORT).show();
        }
    }

    public void connectToDevice(String deviceAddress)
    {
        try
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
            dataExchange.connect(bluetoothDevice);
        }
        catch (Exception e)
        {
            Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
        }
    }

    public void sendOrConnect()
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
            else
            {
                send = 2;

                if (dataExchange.getState() == DataExchange.STATE_CONNECTED)
                {
                    sendData(this, pendingMessage);
                }
                else
                {
                    connectToDevice(deviceModelData.macAddress);
                }
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
        }
    }
}