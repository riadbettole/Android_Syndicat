package com.example.android_syndicat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DeleteAccountActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteaccount);
    }

    public void deleteMyAccount(View view) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        final EditText password = findViewById(R.id.password);

        AuthCredential credential = EmailAuthProvider
                .getCredential(currentUser.getEmail(), password.getText().toString());

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(task -> currentUser.delete()
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.d("myTag", "User account deleted.");
                            }
                        })
                );
    }
}
