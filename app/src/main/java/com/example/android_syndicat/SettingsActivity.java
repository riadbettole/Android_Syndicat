package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
    }

    public void onClickChangeToCamera(View view){
        startActivity(new Intent(this, ChangePhotoActivity.class));
        finish();
    }
    public void onClickChangeToPassword(View view){
        startActivity(new Intent(this, PasswordChangeActivity.class));
        finish();
    }

    public void onClickChangeToMap(View view){
        startActivity(new Intent(this, MapActivity.class));
        finish();
    }

    public void onClickReturnBack(View view){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}
