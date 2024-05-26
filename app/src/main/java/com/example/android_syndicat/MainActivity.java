package com.example.android_syndicat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Parameter;

public class MainActivity extends AppCompatActivity {
//    UserSingleton userSingleton;
    FirebaseUser currentUser;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        setImageOfUser();

        Log.d("myTag","the user is " + currentUser.getEmail());

        TextView greeting = findViewById(R.id.greeting);
        String greetingMessage = "Hello, "+currentUser.getDisplayName().split(" ")[0]+"!";
        greeting.setText(greetingMessage);

        TextView meteo = findViewById(R.id.meteo);
        getTemperatureOfCity(new TemperatureCallback() {
            @Override
            public void onTemperatureReceived(String temperature) {
                String meteoMessage = temperature;
                Log.d("myTag", "im here");
                Log.d("myTag", meteoMessage);
                meteo.setText(meteoMessage);
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("myTag", errorMessage);
                meteo.setText("Error fetching temperature");
            }
        });


    }

    private void setImageOfUser() {
        StorageReference storageRef = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());

        userRef.child("image").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Error retrieving last position", Toast.LENGTH_SHORT).show();
                return;
            }
            String imagePath = task.getResult().getValue(String.class);

            ImageView imageView = findViewById(R.id.photo);

            StorageReference islandRef = storageRef.child(imagePath);

            final long ONE_MEGABYTE = 1024 * 1024* 5;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }).addOnFailureListener(exception -> {
                Log.e("myTag",exception.toString());
            });
        });
    }

    public void onClickSwitchToAnnonces(View view){
            startActivity(new Intent(this, Annonces.class));
    }
    public void onClickSwitchToFactures(View view){}
    public void onClickSwitchToMessages(View view){}
    public void onClickSwitchToParams(View view){
        startActivity(new Intent(this, Settings.class));
        finish();
    }
    public void onClickLogOut(View view){
        auth.signOut();
        startActivity(new Intent(this, Login.class));
        finish();
        Toast.makeText(this, "Logged out Succesfully", Toast.LENGTH_SHORT).show();
    }

    public interface TemperatureCallback {
        void onTemperatureReceived(String temperature);
        void onError(String errorMessage);
    }
    private void getTemperatureOfCity(TemperatureCallback callback) {
        final String key = "c2584f5de0714cd9816143218242605";

        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());

        userRef.child("location").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Error retrieving last position", Toast.LENGTH_SHORT).show();
                return;
            }
            String city = task.getResult().getValue(String.class);
            String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + city;

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                    response -> {
                        try {
                            JSONObject locationObj = response.getJSONObject("current");
                            String tempC = locationObj.getString("temp_c");
                            callback.onTemperatureReceived("☁️ "+tempC + "°C " + city);
                        } catch (JSONException e) {
                            Log.e("myTag", e.toString());
                            e.printStackTrace();
                            callback.onError("Error parsing JSON response");
                        }
                    },
                    error -> {
                        Log.e("myTag", error.toString());
                        callback.onError("Error fetching temperature data");
                    });

            queue.add(jsonObjectRequest);
        });

    }

}