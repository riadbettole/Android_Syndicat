package com.example.android_syndicat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class ChangePhotoActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 1;
    ActivityResultLauncher<Uri> takePictureLauncher;
    Uri imageUri;
    ActivityResultLauncher<Intent> resultLauncher;
    ImageView imageView;
    private StorageReference storageRef;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseStorage storage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changeimage);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        imageUri = createUri();
        imageView = findViewById(R.id.photo);

        setImageOfUser();

        registerPictureLauncher();
        registerResult();
    }

    private Uri createUri(){
        File imageFile = new File(getApplicationContext().getFilesDir(), "camera_photo.jpg");
        return FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.camerapermission.fileprovider",
                imageFile
        );
    }

    public void pickImage(View view){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }
    private void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try{
                        Uri imageUriX = result.getData().getData();
                        imageView.setImageURI(imageUriX);
                        saveImage(imageUriX);
                    }catch (Exception e){
                        Log.e("myTag","No Image Selected");
                    }
                }
        );
    }
    private void registerPictureLauncher(){
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                result -> {
                    try{
                        if(result){
                            imageView.setImageURI(imageUri);
                            saveImage(imageUri);
                        }
                    }catch (Exception e){
                        Log.e("myTag","wtf");
                    }
                }
        );
    }
    public void checkCameraPermissionAndOpenCamera(View view){
        if(ActivityCompat.checkSelfPermission(ChangePhotoActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ChangePhotoActivity.this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }else {
            takePictureLauncher.launch(imageUri);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePictureLauncher.launch(imageUri);
            } else {
                Log.d("myTag","im done camera denied");
            }
        }
    }

    private void saveImage(Uri imageUriX){

        DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());

        StorageReference imageRef = storageRef.child("images/" + currentUser.getUid() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUriX);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            userRef.child("image").setValue("images/" + currentUser.getUid() + ".jpg");
            startActivity(new Intent(ChangePhotoActivity.this, MainActivity.class));
            finish();
            Log.d("myTag", "Image uploaded successfully");
        }).addOnFailureListener(e -> {
            Log.e("myTag", "Error uploading image: " + e.getMessage());
        });
    }

    public void onClickGetBack(View view){
        startActivity(new Intent(ChangePhotoActivity.this, MainActivity.class));
        finish();
    }

    private void setImageOfUser() {
        StorageReference storageRef = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mDatabase.child("users").child(currentUser.getUid());

        userRef.child("image").get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Toast.makeText(ChangePhotoActivity.this, "Error retrieving last position", Toast.LENGTH_SHORT).show();
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
}
