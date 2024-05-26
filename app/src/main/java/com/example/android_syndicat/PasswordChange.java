package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordChange extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_changepass);
    }

    public void onClickChangePassowrd(View view){
        FirebaseUser user = mAuth.getCurrentUser();

        final EditText oldPass = findViewById(R.id.oldPassword);
        final EditText newPass = findViewById(R.id.newPassword);
        final EditText newPassRepeat = findViewById(R.id.newPasswordConf);


        final String oldPassTxt = oldPass.getText().toString();
        final String newPassTxt = newPass.getText().toString();
        final String newPassRepeatTxt = newPassRepeat.getText().toString();

        if(!newPassTxt.equals(newPassRepeatTxt)){
            Toast.makeText(this,"Passwords arent matching" , Toast.LENGTH_SHORT).show();
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassTxt);
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassTxt).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Log.d("myTag", "Password updated");
                                    startActivity(new Intent(PasswordChange.this,MainActivity.class));
                                    finish();
                                } else {
                                    Log.d("myTag", "Error password not updated");
                                }
                            });
                        } else {
                            Log.d("myTag", "Error auth failed");
                        }
                    }
                });

    }
}
