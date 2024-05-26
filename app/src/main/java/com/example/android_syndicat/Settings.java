package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Settings  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
    }

    public void onClickChangeToCamera(View view){
        startActivity(new Intent(this, ChangePhoto.class));
        finish();
    }
    public void onClickChangeToPassword(View view){
        startActivity(new Intent(this, PasswordChange.class));
        finish();
    }

    public void onClickChangeToMap(View view){
        startActivity(new Intent(this, Map.class));
        finish();
    }

    public void onClickReturnBack(View view){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
