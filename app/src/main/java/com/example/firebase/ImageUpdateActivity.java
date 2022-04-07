package com.example.firebase;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ImageUpdateActivity extends AppCompatActivity {
    ImageView imageViews;
    ProgressBar progressBar;
    Button uploadimagebtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String[] permissons = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET};

    int permissoncode = 2132;
    Uri imageuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_update);
        imageViews = findViewById(R.id.imageviews);
        progressBar = findViewById(R.id.progressbar);
        uploadimagebtn = findViewById(R.id.uploadiamge);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        uploadimagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageuri != null){
                    progressBar.setVisibility(View.VISIBLE);
                    uploadImage(imageuri);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(permissons, permissoncode);
            }


        }
        imageViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),imageViews);
                popupMenu.inflate(R.menu.popupmenu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id )
                        {
                            case R.id.cameramenu:
                                Intent  intent = new Intent();
                                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                                CameraResultLauncher.launch(intent);
                                break;

                            case R.id.gallerymenu:
                                Intent  intents = new Intent();
                                intents.setAction(Intent.ACTION_GET_CONTENT);
                                intents.setType("image/*");
                                GalleryResultLauncher.launch(intents);
                                break;
                        }


                        return true;
                    }
                });
                popupMenu.show();




            }
        });

    }

    private void uploadImage(Uri imageuri) {
        String userid = firebaseAuth.getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("usreimage").child(userid);
        storageReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imageurl = uri.toString();

                        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                        HashMap<String,String> usermapimage = new HashMap<>();
                        usermapimage.put("imageUrl",imageurl);
                        usermapimage.put("userid",userid);

                        firebaseFirestore.collection("userImageData").document(userid).set(usermapimage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Image Upload Successfully", Toast.LENGTH_SHORT).show();
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Image Upload fail"+task.getException(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                    }
                });
            }
        });
    }


    ActivityResultLauncher<Intent>  CameraResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        Bitmap  bitmap= (Bitmap) result.getData().getExtras().get("data");
                        imageViews.setImageBitmap(bitmap);
                        imageuri = getImageuribitmap(getApplicationContext(),bitmap);



                    }
                }
            }
    );

    private Uri getImageuribitmap(Context applicationContext, Bitmap bitmap) {
        ByteArrayOutputStream byobj= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byobj);
        String path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(),bitmap,"antthing","this is somethin else");
        return Uri.parse(path);
    }


    ActivityResultLauncher<Intent> GalleryResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        imageuri = result.getData().getData();
                        imageViews.setImageURI(imageuri);




                    }
                }
            }
    );
}