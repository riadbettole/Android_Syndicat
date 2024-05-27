package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        setContentView(R.layout.activity_login);
    }

    public void onClickConnect(View view){
        final EditText email = findViewById(R.id.email_login);
        final EditText password = findViewById(R.id.password_login);

        final String emailTxt = email.getText().toString();
        final String passwordTxt = password.getText().toString();

        if(emailTxt.isEmpty() || passwordTxt.isEmpty()){
            Toast.makeText(LoginActivity.this,"Email/Password Empty",Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
            .addOnCompleteListener(LoginActivity.this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
        });

    }

    public void onClickGoToRegisterActivity(View view){
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
