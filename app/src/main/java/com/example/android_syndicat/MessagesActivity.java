package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {
    private List<Item> itemList;
    private ItemAdapter itemAdapter;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annonces);

        db = FirebaseDatabase.getInstance();

        displayButtons();

        DatabaseReference root = db.getReference().child("messages");

        SearchView searchView = findViewById(R.id.searchView);
        searchView.clearFocus();//optional for devices older

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        itemAdapter = new ItemAdapter(itemList);

        recyclerView.setAdapter(itemAdapter);

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Item item = dataSnapshot.getValue(Item.class);
                    itemList.add(item);
                }
                itemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void filterList(String text){
        List<Item> filteredList = new ArrayList<>();
        for(Item item : itemList){
            if(item.getItemName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        if(filteredList.isEmpty()){
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            return;
        }
        itemAdapter.setFilteredList(filteredList);
    }
    public void goToPreviousActivity(View view){
        startActivity(new Intent(MessagesActivity.this, MainActivity.class));
        finish();
    }

    private void displayButtons() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        DatabaseReference userRef = db.getReference().child("users").child(currentUser.getUid());
        Button btn = findViewById(R.id.addAnnonce);

        userRef.child("role").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(MessagesActivity.this, "Error retrieving last position", Toast.LENGTH_SHORT).show();
                return;
            }
            String userRole = task.getResult().getValue(String.class);
            Log.d("myTag",userRole);
            if (userRole.equals("admin")) {
                btn.setVisibility(View.VISIBLE);
            }
        });
    }
    public void onClickChangeToAdd(View view){
        startActivity(new Intent(MessagesActivity.this, AnnonceAddActivity.class));
        finish();
    }

}
