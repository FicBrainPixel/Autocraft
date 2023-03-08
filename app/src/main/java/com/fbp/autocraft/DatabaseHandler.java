package com.fbp.autocraft;

import static com.fbp.autocraft.MainPage.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Autocraft Robolabs";
    private static final String TABLE_NAME = "device";
    public static final String ID = "id";
    private static final String MAC_ADDRESS = "mac_address";
    private static final String DEVICE_NAME = "device_name";
    private static final String CUSTOM_NAME = "custom_name";
    private static final String ENABLED = "enabled";
    private static final String FAN_ONE = "fan_one";
    private static final String FAN_TWO = "fan_two";
    private static final String SOCKET_ONE = "socket_one";
    private static final String SOCKET_TWO = "socket_two";
    private static final String LIGHT_ONE = "light_one";
    private static final String LIGHT_TWO = "light_two";
    private static final String LIGHT_THREE = "light_three";
    private static final String LIGHT_FOUR = "light_four";

    String create, select;
    Cursor cursor;
    ContentValues values;
    SQLiteDatabase sqLiteDatabase;
    List<DeviceModelData> deviceModelDataList;

    public DatabaseHandler(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        create = "CREATE TABLE " + TABLE_NAME
                    + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MAC_ADDRESS + " TEXT, "
                    + DEVICE_NAME + " TEXT, "
                    + CUSTOM_NAME + " TEXT, "
                    + ENABLED + " BOOLEAN, "
                    + FAN_ONE + " TEXT, "
                    + FAN_TWO + " TEXT, "
                    + SOCKET_ONE + " TEXT, "
                    + SOCKET_TWO + " TEXT, "
                    + LIGHT_ONE + " TEXT, "
                    + LIGHT_TWO + " TEXT, "
                    + LIGHT_THREE + " TEXT, "
                    + LIGHT_FOUR + " TEXT"
                    + ")";
        Log.d(TAG, "Query being run is : " + create);
        db.execSQL(create);
    }

    public void addDevice(DeviceModelData deviceModelData)
    {
        sqLiteDatabase = getWritableDatabase();

        values = new ContentValues();
        values.put(MAC_ADDRESS, deviceModelData.getMacAddress());
        values.put(DEVICE_NAME, deviceModelData.getDeviceName());
        values.put(CUSTOM_NAME, deviceModelData.getCustomName());
        values.put(ENABLED, deviceModelData.getEnabled());
        values.put(FAN_ONE, deviceModelData.getFanOne());
        values.put(FAN_TWO, deviceModelData.getFanTwo());
        values.put(SOCKET_ONE, deviceModelData.getSocketOne());
        values.put(SOCKET_TWO, deviceModelData.getSocketTwo());
        values.put(LIGHT_ONE, deviceModelData.getLightOne());
        values.put(LIGHT_TWO, deviceModelData.getLightTwo());
        values.put(LIGHT_THREE, deviceModelData.getLightThree());
        values.put(LIGHT_FOUR, deviceModelData.getLightFour());

        sqLiteDatabase.insert(TABLE_NAME, null, values);
        Log.d(TAG, "Successfully inserted");
        sqLiteDatabase.close();
    }

    public List<DeviceModelData> getAllDevices()
    {
        deviceModelDataList = new ArrayList<>();
        sqLiteDatabase = getReadableDatabase();

        select = "SELECT * FROM " + TABLE_NAME;
        cursor = sqLiteDatabase.rawQuery(select, null);

        if(cursor.moveToFirst())
        {
            do
            {
                DeviceModelData deviceModelData = new DeviceModelData();
                deviceModelData.setMacAddress(cursor.getString(1));
                deviceModelData.setDeviceName(cursor.getString(2));
                deviceModelData.setCustomName(cursor.getString(3));
                deviceModelData.setEnabled(cursor.getInt(4));
                deviceModelData.setFanOne(cursor.getString(5));
                deviceModelData.setFanTwo(cursor.getString(6));
                deviceModelData.setSocketOne(cursor.getString(7));
                deviceModelData.setSocketTwo(cursor.getString(8));
                deviceModelData.setLightOne(cursor.getString(9));
                deviceModelData.setLightTwo(cursor.getString(10));
                deviceModelData.setLightThree(cursor.getString(11));
                deviceModelData.setLightFour(cursor.getString(12));
                deviceModelDataList.add(deviceModelData);
            } while(cursor.moveToNext());
        }

        cursor.close();

        return deviceModelDataList;
    }

    public int updateDevice(DeviceModelData deviceModelData)
    {
        sqLiteDatabase = this.getWritableDatabase();

        values = new ContentValues();
        values.put(MAC_ADDRESS, deviceModelData.getMacAddress());
        values.put(DEVICE_NAME, deviceModelData.getDeviceName());
        values.put(CUSTOM_NAME, deviceModelData.getCustomName());
        values.put(ENABLED, deviceModelData.getEnabled());
        values.put(FAN_ONE, deviceModelData.getFanOne());
        values.put(FAN_TWO, deviceModelData.getFanTwo());
        values.put(SOCKET_ONE, deviceModelData.getSocketOne());
        values.put(SOCKET_TWO, deviceModelData.getSocketTwo());
        values.put(LIGHT_ONE, deviceModelData.getLightOne());
        values.put(LIGHT_TWO, deviceModelData.getLightTwo());
        values.put(LIGHT_THREE, deviceModelData.getLightThree());
        values.put(LIGHT_FOUR, deviceModelData.getLightFour());

        return sqLiteDatabase.update(TABLE_NAME, values, MAC_ADDRESS + "=?", new String[]{deviceModelData.getMacAddress()});
    }

    public int updateDeviceVisibility(String macAddress, int enabled)
    {
        sqLiteDatabase = getWritableDatabase();

        values = new ContentValues();
        values.put(MAC_ADDRESS, macAddress);
        values.put(ENABLED, enabled);
        Log.d(TAG, values + " " + macAddress);

        return sqLiteDatabase.update(TABLE_NAME, values, MAC_ADDRESS + "=?", new String[]{macAddress});
    }

    public void deleteDevice(int id)
    {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABLE_NAME, ID + "=?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();

        Log.d(TAG, "Data deleted");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}