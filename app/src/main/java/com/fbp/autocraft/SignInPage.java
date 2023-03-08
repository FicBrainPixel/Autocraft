package com.fbp.autocraft;

import static com.fbp.autocraft.MainPage.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignInPage extends AppCompatActivity
{
    boolean userExists;
    SignInButton signIn;
    static FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ConnectivityManager connectivityManager;
    @SuppressLint("StaticFieldLeak")
    static GoogleSignInClient googleSignInClient;
    Map<String, Object> user;
    ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_page);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        signIn = findViewById(R.id.signIn);

        user = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        googleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build());

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        signIn.setOnClickListener(v ->
        {
            startActivity(new Intent(this, MainPage.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            /*
            try
            {
                if(connectivityManager.getActiveNetworkInfo() != null && Runtime.getRuntime().exec("ping -c 1 google.com").waitFor() == 0)
                {
                    activityResultLauncher.launch(googleSignInClient.getSignInIntent());
                }
                else
                {
                    Snackbar.make(findViewById(android.R.id.content), "Please turn on your internet connection", Snackbar.LENGTH_LONG).show();
                }
            } catch (Exception ignored) {}
             */
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
        {
            if(result.getResultCode() == RESULT_OK)
            {
                try
                {
                     firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(GoogleSignIn.getSignedInAccountFromIntent(result.getData()).
                         getResult(ApiException.class).getIdToken(),null))
                            .addOnCompleteListener(this, task ->
                    {
                        if(task.isSuccessful())
                        {
                            if(firebaseAuth.getCurrentUser() != null)
                            {
                                firebaseFirestore.collection("Users").get().addOnSuccessListener(task1 ->
                                {
                                    for(DocumentSnapshot documentSnapshot : task1.getDocuments())
                                    {
                                        if(Objects.requireNonNull(documentSnapshot.getString("Email")).equals(firebaseAuth.getCurrentUser().getEmail()))
                                        {
                                            userExists = true;
                                            break;
                                        }
                                    }

                                    if(userExists)
                                    {
                                        userExists = false;
                                        startActivity(new Intent(this, MainPage.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }
                                    else
                                    {
                                        addUser();
                                    }
                                }).addOnFailureListener(e -> Log.d(TAG, e.getMessage()));
                            }
                        }
                        else
                        {
                            Snackbar.make(findViewById(android.R.id.content), "Authentication Failed", Snackbar.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception ignored) {}
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        if(firebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(this,MainPage.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void addUser()
    {
        if(firebaseAuth.getCurrentUser() != null)
        {
            user.put("Name", firebaseAuth.getCurrentUser().getDisplayName());
            user.put("Email", firebaseAuth.getCurrentUser().getEmail());

            firebaseFirestore.collection("Users").add(user).
                addOnSuccessListener(documentReference ->
                {
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainPage.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }).
                addOnFailureListener(e ->
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show());
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}