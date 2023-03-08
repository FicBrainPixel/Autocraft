package com.fbp.autocraft;

import static com.fbp.autocraft.SignInPage.firebaseAuth;
import static com.fbp.autocraft.SignInPage.googleSignInClient;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SettingFragment extends Fragment
{
    View view;
    Button logout;
    static ArrayList<String> uidList;
    SettingFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_setting, container, false);

        uidList = new ArrayList<>();

        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(v ->
            googleSignInClient.signOut().addOnCompleteListener(task ->
            {
                if(task.isSuccessful())
                {
                    firebaseAuth.signOut();
                    ((Activity)view.getContext()).finish();
                }
            }));

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