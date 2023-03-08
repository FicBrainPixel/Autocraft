package com.fbp.autocraft;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class ScanWifi extends AppCompatActivity
{
    static String wifiSSID, wifiPassword;
    Button scan, done;
    private ListView wifiList;
    private WifiManager wifiManager;
    WifiReceiver receiverWifi;
    TextView ssid;
    EditText password;
    NetworkInfo networkInfo;
    ConstraintLayout wifiScan, wifiSetup;
    ConnectivityManager connectivityManager;
    LocationManager lm;
    ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_wifi);

        wifiList = findViewById(R.id.wifiList);
        scan = findViewById(R.id.scan);
        ssid = findViewById(R.id.ssid);
        password = findViewById(R.id.password);
        done = findViewById(R.id.done);
        wifiScan = findViewById(R.id.wifiScan);
        wifiSetup = findViewById(R.id.wifiSetup);

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        final NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            connectivityManager = getSystemService(ConnectivityManager.class);
        else
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback()
        {
            @Override
            public void onAvailable(Network network)
            {
                networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                if (networkInfo != null && networkInfo.isConnected())
                {
                    check();
                }
            }

            @Override
            public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {}
        };

        connectivityManager.requestNetwork(request, networkCallback);
        connectivityManager.registerNetworkCallback(request, networkCallback);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        scan.setOnClickListener(v -> check());

        wifiList.setOnItemClickListener((parent, view, position, id) ->
        {
            wifiSSID = String.valueOf(parent.getItemAtPosition(position));
            ssid.setText(wifiSSID);
            wifiScan.setVisibility(View.INVISIBLE);
            wifiSetup.setVisibility(View.VISIBLE);
        });

        ssid.setOnClickListener(v ->
        {
            wifiScan.setVisibility(View.VISIBLE);
            wifiSetup.setVisibility(View.INVISIBLE);
        });

        done.setOnClickListener(v ->
        {
            if (password.getText().toString().isEmpty())
            {
                password.setError("Password cannot be empty!");
            }
            else
            {
                wifiPassword = password.getText().toString();
                setResult(RESULT_OK);
                finish();
            }
        });

        check();
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
    {
        if (result.getResultCode() == RESULT_OK)
        {
            finish();
        }
    });

    public void check()
    {
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            Snackbar.make(findViewById(android.R.id.content), "Turn on your location", Snackbar.LENGTH_LONG).show();
        }
        else
        {
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo != null)
            {
                if(networkInfo.isConnected())
                {
                    Snackbar.make(findViewById(android.R.id.content), "Searching...", Snackbar.LENGTH_LONG).show();
                    receiverWifi = new WifiReceiver(wifiManager, wifiList);
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                    registerReceiver(receiverWifi, intentFilter);
                    wifiManager.startScan();
                    scan.setEnabled(false);
                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content), "Turn on your wifi", Snackbar.LENGTH_LONG).show();
                }
            }
            else
            {
                Snackbar.make(findViewById(android.R.id.content), "Wifi not available", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(receiverWifi);
    }

    class WifiReceiver extends BroadcastReceiver
    {
        WifiManager wifiManager;
        ListView wifiDeviceList;
        ArrayList<String> deviceList;

        public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList)
        {
            this.wifiManager = wifiManager;
            this.wifiDeviceList = wifiDeviceList;
        }

        public void onReceive(Context context, Intent intent)
        {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction()))
            {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);
                    return;
                }

                deviceList = new ArrayList<>();

                for (ScanResult scanResult : wifiManager.getScanResults())
                {
                    if(!scanResult.SSID.isEmpty())
                    {
                        deviceList.add(scanResult.SSID);
                    }
                }

                scan.setEnabled(true);
                wifiDeviceList.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, deviceList.toArray()));
            }
        }
    }
}