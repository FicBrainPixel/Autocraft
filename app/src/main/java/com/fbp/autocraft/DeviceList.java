package com.fbp.autocraft;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class DeviceList extends AppCompatActivity
{
    int i, j;
    static int modelNumber = 5, modelType = 0;
    static String modelName = "A6";
    static HashMap<String, String[]> deviceConfig;
    int[] deviceId = {R.id.deviceOne,R.id.deviceTwo};
    ImageView[] deviceList;
    ImageView back;
    String[] deviceName = {"Navneet's M21","LAPTOP-75E5MLTC"};
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        deviceList = new ImageView[deviceId.length];

        for(i=0;i<deviceId.length;i++)
        {
            deviceList[i] = findViewById(deviceId[i]);
        }

        back = findViewById(R.id.back);

        deviceConfig = new HashMap<>();

        deviceConfig.put("A1", new String[]{"L1","L2","L3","L4"});
        deviceConfig.put("A2", new String[]{"F1","F2","S1","S2","L1","L2","L3","L4"});
        deviceConfig.put("A3", new String[]{"F1","F2","S1","S2","L1","L2","L3","L4"});
        deviceConfig.put("A4", new String[]{"F1","F2","S1","S2","L1","L2","L3","L4"});
        deviceConfig.put("A5", new String[]{"F1","F2","S1","S2","L1","L2","L3","L4"});
        deviceConfig.put("A6", new String[]{"F1","F2","S1","S2","L1","L2","L3","L4"});

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if (result.getResultCode() == RESULT_OK)
            {
                setResult(RESULT_OK);
                finish();
            }
        });

        for(i=0;i<deviceId.length;i++)
        {
            deviceList[i].setOnClickListener(v ->
            {
                for(j=0;j<deviceId.length;j++)
                {
                    if(deviceList[j].getId() == v.getId())
                    {
                        activityResultLauncher.launch(new Intent(this, ScanBluetooth.class).putExtra("Device Name", deviceName[j]));
                    }
                }
            });
        }

        back.setOnClickListener(v -> finish());
    }
}