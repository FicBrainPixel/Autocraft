package com.fbp.autocraft;

import static com.fbp.autocraft.DataExchange.pendingMessage;
import static com.fbp.autocraft.DataExchange.send;
import static com.fbp.autocraft.DeviceModelData.MODEL_FIVE;
import static com.fbp.autocraft.DeviceModelData.MODEL_FOUR;
import static com.fbp.autocraft.DeviceModelData.MODEL_ONE;
import static com.fbp.autocraft.DeviceModelData.MODEL_SIX;
import static com.fbp.autocraft.DeviceModelData.MODEL_THREE;
import static com.fbp.autocraft.DeviceModelData.MODEL_TWO;
import static com.fbp.autocraft.MainPage.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class DeviceModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable
{
    Context context;
    @SuppressLint("StaticFieldLeak")
    static DataExchange dataExchange;
    ArrayList<DeviceModelData> deviceModelData, duplicateDeviceModelData;
    public DeviceModelAdapter(Context context, ArrayList<DeviceModelData> deviceModelData)
    {
        this.context = context;
        this.deviceModelData = deviceModelData;

        dataExchange = new DataExchange(context);
    }

    @Override
    public int getItemViewType(int position)
    {
        switch (deviceModelData.get(position).getDeviceName())
        {
            case MODEL_ONE:
                return 0;
            case MODEL_TWO:
                return 1;
            case MODEL_THREE:
                return 2;
            case MODEL_FOUR:
                return 3;
            case MODEL_FIVE:
                return 4;
            case MODEL_SIX:
                return 5;
            default:
                return -1;
        }
    }

    public static class ModelOneViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView name, lightOne, lightTwo, lightThree, lightFour;

        public ModelOneViewHolder(@NonNull View itemView)
        {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            lightOne = itemView.findViewById(R.id.lightOne);
            lightTwo = itemView.findViewById(R.id.lightTwo);
            lightThree = itemView.findViewById(R.id.lightThree);
            lightFour = itemView.findViewById(R.id.lightFour);
        }

        private void setViews(String nameText, String lightOneText, String lightTwoText, String lightThreeText,
                              String lightFourText)
        {
            name.setText(nameText);
            name.setBackground(null);
            name.setInputType(InputType.TYPE_NULL);
            lightOne.setText(lightOneText);
            lightTwo.setText(lightTwoText);
            lightThree.setText(lightThreeText);
            lightFour.setText(lightFourText);
        }
    }

    public static class ModelTwoViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView customName;

        public ModelTwoViewHolder(@NonNull View itemView)
        {
            super(itemView);

            customName = itemView.findViewById(R.id.name);
        }

        private void setViews(String text) {
            customName.setText(text);
        }
    }

    public static class ModelThreeViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView customName;

        public ModelThreeViewHolder(@NonNull View itemView)
        {
            super(itemView);

            customName = itemView.findViewById(R.id.name);
        }

        private void setViews(String text) {
            customName.setText(text);
        }
    }

    public static class ModelFourViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView customName;

        public ModelFourViewHolder(@NonNull View itemView)
        {
            super(itemView);

            customName = itemView.findViewById(R.id.name);
        }

        private void setViews(String text) {
            customName.setText(text);
        }
    }

    public static class ModelFiveViewHolder extends RecyclerView.ViewHolder
    {
        private final TextView customName;

        public ModelFiveViewHolder(@NonNull View itemView)
        {
            super(itemView);

            customName = itemView.findViewById(R.id.name);
        }

        private void setViews(String text) {
            customName.setText(text);
        }
    }

    public static class ModelSixViewHolder extends RecyclerView.ViewHolder
    {
        private final ImageView fanOneSpeedUp, fanOneSpeedDown, fanTwoSpeedUp,
                fanTwoSpeedDown;
        private final TextView name, fanOne, fanTwo, socketOne, socketTwo, lightOne, lightTwo,
        lightThree, lightFour;

        public ModelSixViewHolder(@NonNull View itemView)
        {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            fanOne = itemView.findViewById(R.id.fanOne);
            fanOneSpeedUp = itemView.findViewById(R.id.fanOneSpeedUp);
            fanOneSpeedDown = itemView.findViewById(R.id.fanOneSpeedDown);
            fanTwo = itemView.findViewById(R.id.fanTwo);
            fanTwoSpeedUp = itemView.findViewById(R.id.fanTwoSpeedUp);
            fanTwoSpeedDown = itemView.findViewById(R.id.fanTwoSpeedDown);
            socketOne = itemView.findViewById(R.id.socketOne);
            socketTwo = itemView.findViewById(R.id.socketTwo);
            lightOne = itemView.findViewById(R.id.lightOne);
            lightTwo = itemView.findViewById(R.id.lightTwo);
            lightThree = itemView.findViewById(R.id.lightThree);
            lightFour = itemView.findViewById(R.id.lightFour);
        }

        private void setViews(String nameText, String fanOneText, String fanTwoText, String socketOneText,
            String socketTwoText, String lightOneText, String lightTwoText, String lightThreeText,
                String lightFourText)
        {
            name.setText(nameText);
            name.setBackground(null);
            name.setInputType(InputType.TYPE_NULL);
            fanOne.setText(fanOneText);
            fanTwo.setText(fanTwoText);
            socketOne.setText(socketOneText);
            socketTwo.setText(socketTwoText);
            lightOne.setText(lightOneText);
            lightTwo.setText(lightTwoText);
            lightThree.setText(lightThreeText);
            lightFour.setText(lightFourText);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case 0:
                View layoutOne = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.device_model_1, parent, false);
                return new ModelOneViewHolder(layoutOne);

            case 1:
                View layoutTwo = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.device_model_2, parent, false);
                return new ModelTwoViewHolder(layoutTwo);

            case 2:
                View layoutThree = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.device_model_3, parent, false);
                return new ModelThreeViewHolder(layoutThree);

            case 3:
                View layoutFour = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.device_model_4, parent, false);
                return new ModelFourViewHolder(layoutFour);

            case 4:
                View layoutFive = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.device_model_5, parent, false);
                return new ModelFiveViewHolder(layoutFive);

            case 5:
                View layoutSix = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.device_model_6, parent, false);
                return new ModelSixViewHolder(layoutSix);

            default:
                View layoutZero = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.no_device_model, parent, false);
                return new ModelSixViewHolder(layoutZero);
        }
    }

    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        switch (deviceModelData.get(position).getDeviceName())
        {
            case MODEL_ONE:
                ((ModelOneViewHolder) holder).setViews(
                    deviceModelData.get(position).getCustomName(),
                    deviceModelData.get(position).getLightOne(),
                    deviceModelData.get(position).getLightTwo(),
                    deviceModelData.get(position).getLightThree(),
                    deviceModelData.get(position).getLightFour());

                ((ModelOneViewHolder) holder).lightOne.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightOne;
                    sendOrConnect(position);
                });
                ((ModelOneViewHolder) holder).lightTwo.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightTwo;
                    sendOrConnect(position);
                });
                ((ModelOneViewHolder) holder).lightThree.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightThree;
                    sendOrConnect(position);
                });
                ((ModelOneViewHolder) holder).lightFour.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightFour;
                    sendOrConnect(position);
                });
                break;

            case MODEL_TWO:
                ((ModelTwoViewHolder) holder).setViews(deviceModelData.get(position).getCustomName());
                break;

            case MODEL_THREE:
                ((ModelThreeViewHolder) holder).setViews(deviceModelData.get(position).getCustomName());
                break;

            case MODEL_FOUR:
                ((ModelFourViewHolder) holder).setViews(deviceModelData.get(position).getCustomName());
                break;

            case MODEL_FIVE:
                ((ModelFiveViewHolder) holder).setViews(deviceModelData.get(position).getCustomName());
                break;

            case MODEL_SIX:
                ((ModelSixViewHolder) holder).setViews(
                    deviceModelData.get(position).getCustomName(),
                    deviceModelData.get(position).getFanOne(),
                    deviceModelData.get(position).getFanTwo(),
                    deviceModelData.get(position).getSocketOne(),
                    deviceModelData.get(position).getSocketTwo(),
                    deviceModelData.get(position).getLightOne(),
                    deviceModelData.get(position).getLightTwo(),
                    deviceModelData.get(position).getLightThree(),
                    deviceModelData.get(position).getLightFour());

                ((ModelSixViewHolder) holder).fanOne.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).fanOne;
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).fanOneSpeedUp.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).fanOne + " UP";
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).fanOneSpeedDown.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).fanOne + " DOWN";
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).fanTwo.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).fanTwo;
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).fanTwoSpeedUp.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).fanTwo + " UP";
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).fanTwoSpeedDown.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).fanTwo + " DOWN";
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).socketOne.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).socketOne;
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).socketTwo.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).socketTwo;
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).lightOne.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightOne;
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).lightTwo.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightTwo;
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).lightThree.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightThree;
                    sendOrConnect(position);
                });
                ((ModelSixViewHolder) holder).lightFour.setOnClickListener(v ->
                {
                    pendingMessage = deviceModelData.get(position).lightFour;
                    sendOrConnect(position);
                });
                break;
            default:
                Log.d(TAG, "HI");
        }
    }

    @Override
    public int getItemCount()
    {
        return deviceModelData.size();
    }

    public static void sendData(Context context, String message)
    {
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

    public void connectToDevice(String deviceAddress)
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress);
        dataExchange.connect(bluetoothDevice);
    }

    public void sendOrConnect(int position)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 2);
        }
        else
        {
            send = 1;

            if (dataExchange.getState() == DataExchange.STATE_CONNECTED)
            {
                sendData(context, pendingMessage);
            }
            else
            {
                connectToDevice(deviceModelData.get(position).getMacAddress());
            }
        }
    }

    @Override
    public Filter getFilter()
    {
        return Searched_Filter;
    }

    private final Filter Searched_Filter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            ArrayList<DeviceModelData> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(duplicateDeviceModelData);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DeviceModelData item : duplicateDeviceModelData)
                {
                    if (filterPattern.equals("all"))
                    {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        @SuppressLint("NotifyDataSetChanged")
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            deviceModelData.clear();
            deviceModelData.addAll((Collection<? extends DeviceModelData>) results.values);
            notifyDataSetChanged();
        }
    };
}