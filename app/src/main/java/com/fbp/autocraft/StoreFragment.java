package com.fbp.autocraft;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class StoreFragment extends Fragment
{
    View view;
    public StoreFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_store, container, false);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((v, keyCode, event) ->
        {
            if(keyCode == KeyEvent.KEYCODE_BACK)
            {
                ((Activity)view.getContext()).finishAffinity();
                return true;
            }
            return false;
        });

        return view;
    }
}