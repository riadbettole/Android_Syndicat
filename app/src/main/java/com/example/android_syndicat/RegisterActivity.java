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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void onClickRegister(View view){
        final EditText email = findViewById(R.id.email_login);
        final EditText password = findViewById(R.id.password_login);
        final EditText passwordRepeat = findViewById(R.id.repeat_password);
        final EditText fullName = findViewById(R.id.fullname);
        final EditText phone = findViewById(R.id.phone_number);

        final String emailTxt = email.getText().toString();
        final String passwordTxt = password.getText().toString();
        final String passwordRepeatTxt = passwordRepeat.getText().toString();
        final String fullNameTxt = fullName.getText().toString();
        final String phoneTxt = phone.getText().toString();

        if(emailTxt.isEmpty()||phoneTxt.isEmpty()||fullNameTxt.isEmpty()||passwordTxt.isEmpty()||passwordRepeatTxt.isEmpty()){
            Toast.makeText(RegisterActivity.this,"Please fill all the fields" , Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordTxt.equals(passwordRepeatTxt)){
            Toast.makeText(RegisterActivity.this,"Passwords arent matching" , Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(RegisterActivity.this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();

                    mAuth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                            .addOnCompleteListener(RegisterActivity.this, authTask -> {
                                if (authTask.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        updateUserDataAfterRegister(user, fullNameTxt, phoneTxt);
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to get current user", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                });
    }
    private void updateUserDataAfterRegister(FirebaseUser user, String fullName, String phoneNumber){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Failed to update display name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(RegisterActivity.this, "User registered with display name: " + fullName, Toast.LENGTH_SHORT).show();
                });

        String userId = user.getUid();
        Log.d("myTag",userId);
        Log.d("myTag","Im here");
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        userRef.child("phoneNumber").setValue(phoneNumber);
        userRef.child("location").setValue("Casablanca");
        userRef.child("localisation").setValue("(33.59152650505565, -7.604822520772551)");
        userRef.child("image").setValue("images/test.jpg");
        userRef.child("role").setValue("user");

        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish();
    }
    public void goToPreviousActivity(View view){
        finish();
    }
}