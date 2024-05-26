package com.example.android_syndicat;

// Inside an Activity or Fragment
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnnonceAdd extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference itemsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addannonce);
        mAuth = FirebaseAuth.getInstance();
    }
    public void addItem(View view){
        final EditText text = findViewById(R.id.text_input);
        final EditText image = findViewById(R.id.image_input);

        database = FirebaseDatabase.getInstance();
        itemsRef = database.getReference("annonces");

        Log.d("myTag","test");
        Log.d("myTag",mAuth.getCurrentUser().getUid());
        // Create a new item
        Item newItem = new Item(text.getText().toString(), image.getText().toString(), mAuth.getCurrentUser().getUid());

        // Add the item to the database
        itemsRef.push().setValue(newItem)
                .addOnSuccessListener(aVoid -> {
                    // Write was successful
                    Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Toast.makeText(AnnonceAdd.this, "Failed to add item", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
