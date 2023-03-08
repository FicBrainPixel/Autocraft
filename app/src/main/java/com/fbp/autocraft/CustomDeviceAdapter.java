package com.fbp.autocraft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class CustomDeviceAdapter extends ArrayAdapter<CustomDeviceData> implements Filterable
{
    static int index = -1;
    static String macAddress;
    Context context;
    TextView name;
    ImageView image;

    ArrayList<CustomDeviceData> customDeviceData, duplicateCustomDeviceData;

    public CustomDeviceAdapter(Context context, ArrayList<CustomDeviceData> customDeviceData)
    {
        super(context, 0, customDeviceData);

        this.context = context;
        this.customDeviceData = customDeviceData;
        duplicateCustomDeviceData = new ArrayList<>(customDeviceData);
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent)
    {
        View view = convertView;

        if (view == null)
        {
            view = LayoutInflater.from(getContext()).inflate(R.layout.grid_device_card, parent, false);
        }

        name = view.findViewById(R.id.name);
        image = view.findViewById(R.id.image);

        name.setText(getItem(position).getName());
        //image.setImageResource(getItem(position).getImage());

        image.setOnClickListener(v ->
        {
            index = position;
            macAddress = getItem(position).getMacAddress();

            context.startActivity(new Intent(context, DeviceController.class));
        });

        return view;
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
            ArrayList<CustomDeviceData> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(duplicateCustomDeviceData);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CustomDeviceData item : duplicateCustomDeviceData)
                {
                    if (item.name.toLowerCase().toLowerCase().contains(filterPattern.toLowerCase()))
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
            customDeviceData.clear();
            customDeviceData.addAll((Collection<? extends CustomDeviceData>) results.values);
            notifyDataSetChanged();
        }
    };
}