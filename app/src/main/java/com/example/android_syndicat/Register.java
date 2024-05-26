package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static UserSingleton userSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
//        userSingleton = UserSingleton.getInstance();
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
            Toast.makeText(Register.this,"Please fill all the fields" , Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordTxt.equals(passwordRepeatTxt)){
            Toast.makeText(Register.this,"Passwords arent matching" , Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailTxt, passwordTxt)
                .addOnCompleteListener(Register.this, task -> {
                    if(!task.isSuccessful()) {
                        Toast.makeText(Register.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(Register.this,"User registred" , Toast.LENGTH_SHORT).show();
                });
        mAuth.signInWithEmailAndPassword(emailTxt,passwordTxt)
                .addOnCompleteListener(Register.this, task -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUserDataAfterRegister(user, fullNameTxt, phoneTxt);
                    userSingleton.setCurrentUser(user);
                    startActivity(new Intent(Register.this,MainActivity.class));
                    finish();
                });
    }
    private void updateUserDataAfterRegister(FirebaseUser user, String fullName, String phoneNumber){
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(fullName)
                .build();

        user.updateProfile(profileUpdates)
            .addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(Register.this, "Failed to update display name", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(Register.this, "User registered with display name: " + fullName, Toast.LENGTH_SHORT).show();
        });

        String userId = user.getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId);
        userRef.child("phoneNumber").setValue(phoneNumber);
        userRef.child("location").setValue("Casablanca");
        userRef.child("localisation").setValue("(33.59152650505565, -7.604822520772551)");
        userRef.child("image").setValue("images/test.jpg");
    }
    public void goToPreviousActivity(View view){
        finish();
    }
}
