package com.example.android_syndicat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapView mapview;
    GoogleMap gMap;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String latLngStr;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mapview = findViewById(R.id.mapView);
        mapview.onCreate(savedInstanceState);
        mapview.getMapAsync(this);

        user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId);

        userRef.child("localisation").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(MapActivity.this, "Error retrieving last position", Toast.LENGTH_SHORT).show();
                return;
            }
            String lastSavedPosition = task.getResult().getValue(String.class);
            if (lastSavedPosition == null) {
                LatLng defaultLocation = new LatLng(33.59152650505565, -7.604822520772551);
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
                return;
            }

            String[] parts = lastSavedPosition.split(",");

            Pair<Double, Double> p = getLatAndLng(parts);

            LatLng lastLatLng = new LatLng(p.first, p.second);

            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(lastLatLng).title("Selected Location"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        LatLng coordinate = new LatLng(33.59152650505565, -7.604822520772551);
        gMap.addMarker(new MarkerOptions().position(coordinate).title("emsi centre"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 25));


        gMap.setOnMapClickListener(latLng -> {
            gMap.clear();
            gMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            Log.d("myTag", latLng.toString());
            latLngStr = latLng.toString();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapview.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapview.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapview.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapview.onSaveInstanceState(outState);
    }

    public void goToPreviousActivity(View view) {
        finish();
    }

    public void savePosition(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userRef = mDatabase.child("users").child(userId).child("localisation");
        userRef.child("localisation").setValue(latLngStr);
        Pair<Double, Double> p = getLatAndLng(latLngStr.split(","));
        getCityNameFromApi(p.first, p.second);
        startActivity(new Intent(this, SettingsActivity.class));
        finish();
    }

    private void getCityNameFromApi(double latitude, double longitude) {
        final String key = "c2584f5de0714cd9816143218242605";
        String apiUrl = "https://api.weatherapi.com/v1/current.json?key=" + key + "&q=" + latitude + "," + longitude;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    try {
                        JSONObject locationObj = response.getJSONObject("location");
                        String cityName = locationObj.getString("name");
                        String userId = user.getUid();
                        DatabaseReference userRef = mDatabase.child("users").child(userId);
                        userRef.child("location").setValue(cityName);
                        Log.d("myTag", "YOU DESERVE TO REST");
                        // Use the city name as needed
                    } catch (JSONException e) {
                        Log.e("myTag", e.toString());
                        e.printStackTrace();
                    }
                },
                error -> Log.e("myTag", error.toString()));

        queue.add(jsonObjectRequest);
    }

    private Pair<Double, Double> getLatAndLng(String[] parts) {
        double lat = 0, lng = 0;
        try {
            lat = Double.parseDouble(parts[0].substring(10));
            lng = Double.parseDouble(parts[1].replace(")", ""));
        } catch (NumberFormatException e) {
            // Handle the case where the string cannot be parsed as a number
            Toast.makeText(MapActivity.this, "Error parsing last position", Toast.LENGTH_SHORT).show();
        }
        return new Pair(lat, lng);
    }

    public class Pair<U, V> {
        public U first;
        public V second;
        public Pair(U first, V second) {
            this.first = first;
            this.second = second;
        }
    }
}
