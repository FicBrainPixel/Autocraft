package com.fbp.autocraft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fbp.autocraft.databinding.MainPageBinding;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainPage extends AppCompatActivity
{
    @SuppressLint("StaticFieldLeak")
    static Context context;
    static String TAG = "ACRL";
    static DatabaseHandler databaseHandler;
    MainPageBinding mainPageBinding;
    FirebaseAnalytics firebaseAnalytics;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mainPageBinding = MainPageBinding.inflate(getLayoutInflater());
        setContentView(mainPageBinding.getRoot());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        context = getApplicationContext();

        replaceFragment(new HomeFragment());

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        databaseHandler = new DatabaseHandler(this);

        mainPageBinding.bottomNavigationBar.setOnItemSelectedListener(item ->
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d(TAG, String.valueOf(DeviceModelAdapter.dataExchange.getState()));
            }

            switch (item.getItemId())
            {
                case R.id.home:
                {
                    replaceFragment(new HomeFragment());
                    break;
                }

                case R.id.store:
                {
                    replaceFragment(new StoreFragment());
                    break;
                }

                case R.id.device:
                {
                    DataExchange.send = 1;

                    DeviceModelAdapter.dataExchange.stop();
                    replaceFragment(new DeviceFragment());
                    break;
                }

                case R.id.setting:
                {
                    replaceFragment(new SettingFragment());
                    break;
                }
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}