package com.fbp.autocraft;

import static com.fbp.autocraft.DeviceList.modelType;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.google.android.material.snackbar.Snackbar;

public class ScanBluetooth extends AppCompatActivity
{
    boolean deviceFound, swapStatus;
    ImageView messageBox, status, back;
    Handler handler;
    String deviceName;
    static String macAddress;
    BluetoothDevice bluetoothDevice;
    BluetoothAdapter bluetoothAdapter;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_bluetooth);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        back = findViewById(R.id.back);
        status = findViewById(R.id.status);
        messageBox = findViewById(R.id.message_box);

        deviceName = getIntent().getStringExtra("Device Name");

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        handler = new Handler();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
            }
        }
        else if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }

        status.setOnClickListener(v ->
        {
            if(swapStatus)
            {
                if(modelType == 0)
                {
                    activityResultLauncher.launch(new Intent(this, DeviceSetup.class));
                }
                else
                {
                    activityResultLauncher.launch(new Intent(this, ScanWifi.class));
                }
            }
            else
            {
                if(messageBox.getVisibility() == View.VISIBLE)
                {
                    messageBox.setVisibility(View.INVISIBLE);
                }

                if(bluetoothAdapter == null)
                {
                    Toast.makeText(this, "This device does not support bluetooth.", Toast.LENGTH_SHORT).show();
                }
                else if(!isLocationEnabled())
                {
                    messageBox.setImageResource(R.drawable.location_message_box);
                    messageBox.setVisibility(View.VISIBLE);
                }
                else if(bluetoothAdapter.isEnabled())
                {
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    }
                    else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 2);
                    }
                    else if(!bluetoothAdapter.isDiscovering())
                    {
                        try {
                            status.setEnabled(false);
                            status.setAlpha(0.5f);
                            bluetoothAdapter.startDiscovery();

                            if(messageBox.getVisibility() == View.VISIBLE)
                            {
                                messageBox.setVisibility(View.INVISIBLE);
                            }

                            handler.postDelayed(() ->
                            {
                                if(deviceFound)
                                {
                                    Toast.makeText(this, "Found", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    if(bluetoothAdapter.isDiscovering())
                                    {
                                        bluetoothAdapter.cancelDiscovery();
                                    }
                                    status.setEnabled(true);
                                    status.setAlpha(1.0f);
                                    messageBox.setVisibility(View.VISIBLE);
                                    messageBox.setImageResource(R.drawable.not_found_message);
                                }
                            }, 15000);
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "Soory 1", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    messageBox.setImageResource(R.drawable.bluetooth_message_box);
                    messageBox.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Soory 2", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if(result.getResultCode() == RESULT_OK)
            {
                setResult(RESULT_OK);
                finish();
            }
        });

        back.setOnClickListener(v -> finish());

        registerReceiver(ActionFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(ActionFoundReceiver);
    }

    private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction()))
            {
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if(bluetoothDevice.getName() != null && bluetoothDevice.getName().equals(deviceName))
                {
                    try
                    {
                        deviceFound = true;
                        macAddress = bluetoothDevice.getAddress();
                        status.setAlpha(1.0f);
                        status.setEnabled(true);
                        status.setImageResource(R.drawable.done);
                        swapStatus = true;
                        bluetoothAdapter.cancelDiscovery();
                        bluetoothDevice.createBond();
                    }
                    catch (Exception e)
                    {
                        Snackbar.make(findViewById(android.R.id.content), "Cannot pair the device. Please try again", Snackbar.LENGTH_LONG).show();
                    }
                }
                Toast.makeText(context, bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context, "Soory 3", Toast.LENGTH_SHORT).show();
            }
        }
    };
}