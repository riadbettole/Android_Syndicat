package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MessageAddActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addannonce);
        mAuth = FirebaseAuth.getInstance();
    }
    public void addItem(View view){
        final EditText text = findViewById(R.id.password);
        final EditText image = findViewById(R.id.image_input);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference itemsRef = database.getReference("annonces");

        Item newItem = new Item(text.getText().toString(), image.getText().toString(), mAuth.getCurrentUser().getUid());

        itemsRef.push().setValue(newItem)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MessageAddActivity.this, "Failed to add item", Toast.LENGTH_SHORT).show());
    }
    public void goToPreviousActivity(View view){
        startActivity(new Intent(MessageAddActivity.this, AnnoncesActivity.class));
        finish();
    }
}
