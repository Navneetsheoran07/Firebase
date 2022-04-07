package com.example.firebase;

import static android.content.ContentValues.TAG;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {
    ProgressBar progressBar;
    EditText entername,enternumber;
    TextView nametext ,numbertext;
    Button savebtn;
    ImageView imageView;
    Uri imageuri;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    RelativeLayout imagebackground;
    String[] permissons = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.INTERNET};

    int permissoncode = 2132;
     String useridvalue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        connection();



        if (getIntent() !=null) {
            useridvalue = getIntent().getStringExtra("userid");

//           for (ModelClass modelClass:userlist) {
//               Log.e(TAG, "userid: "+modelClass.getUserid() );
//            }

        }

        fatchData();

        fatchImage();


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String nametext = entername.getText().toString().trim();
                //String numbertxt = enternumber.getText().toString().trim();

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


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(),imageView);
                popupMenu.inflate(R.menu.popupmenu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        switch (id )
                        {
                            case R.id.cameramenu:
                                Intent intent = new Intent();
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

    private void fatchImage() {
       // String userid = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("userImageData").document(useridvalue).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    String imageuritext = document.get("imageUrl").toString();

                    Glide.with(getApplicationContext()).load(imageuritext).into(imageView);
                   // Glide.with(getApplicationContext()).load(imageuritext).

                }
                else {
                    Toast.makeText(getApplicationContext(), "error"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    private void fatchData() {
       // String userid = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection("TenAmBatchData").document(useridvalue).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()){

                    String  nametext = document.get("nametext").toString();
                    String numbertext = document.get("numbertext").toString();
                    entername.setText(nametext);
                    enternumber.setText(numbertext);



                }else {
                    Toast.makeText(getApplicationContext(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                }
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


    ActivityResultLauncher<Intent> CameraResultLauncher =registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()== Activity.RESULT_OK){
                        Bitmap bitmap= (Bitmap) result.getData().getExtras().get("data");
                        imageView.setImageBitmap(bitmap);
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
                        imageView.setImageURI(imageuri);




                    }
                }
            }
    );


    private void connection() {
        progressBar = findViewById(R.id.progressbar);
        entername = findViewById(R.id.nameedittextvies);
        enternumber = findViewById(R.id.numberedittextvies);
        savebtn = findViewById(R.id.savebtn);
        imageView = findViewById(R.id.imagescontact);
        nametext = findViewById(R.id.nametextviewss);
        numbertext = findViewById(R.id.numbertextviewss);
        imagebackground = findViewById(R.id.relativeLayoutdetails);
    }
}