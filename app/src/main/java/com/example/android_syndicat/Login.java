package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
//    private static UserSingleton userSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(Login.this, MainActivity.class));
        }
        setContentView(R.layout.activity_login);
    }

    public void onClickConnect(View view){
        final EditText email = findViewById(R.id.email_login);
        final EditText password = findViewById(R.id.password_login);

        final String emailTxt = email.getText().toString();
        final String passwordTxt = password.getText().toString();

        if(emailTxt.isEmpty() || passwordTxt.isEmpty()){
            Toast.makeText(Login.this,"Email/Password Empty",Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
            .addOnCompleteListener(Login.this, task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser user = mAuth.getCurrentUser();

                startActivity(new Intent(Login.this,MainActivity.class));
                finish();
        });

    }

    public void onClickGoToRegisterActivity(View view){
        startActivity(new Intent(Login.this, Register.class));
    }
}
