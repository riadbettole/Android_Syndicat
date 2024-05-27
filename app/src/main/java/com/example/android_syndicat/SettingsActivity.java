package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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
