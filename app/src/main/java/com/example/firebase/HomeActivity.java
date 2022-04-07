package com.example.firebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView name ,number;
    ImageView imageViewsss;
    RecyclerView recyclerView;
    ArrayList<ModelClass> userlist;
    ArrayList<ImageModelClass> imagelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        userlist =new ArrayList<>();
        imagelist = new ArrayList<>();
        name = findViewById( R.id.textview);
        number = findViewById(R.id.textviewemail);
        imageViewsss = findViewById(R.id.imagessviewsss);
        recyclerView = findViewById(R.id.recyclerview);
        firebaseAuth =FirebaseAuth.getInstance();
        fatchData();
        fatchinlist();

        fatchImage();


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),1,RecyclerView.VERTICAL,false);

        recyclerView.setLayoutManager(gridLayoutManager);

    }

    private void fatchImage() {
        String userid = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("userImageData").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
           DocumentSnapshot document = task.getResult();
           if(document.exists()){
               String imageuritext = document.get("imageUrl").toString();

               Glide.with(getApplicationContext()).load(imageuritext).into(imageViewsss);
           }
           else {
               Toast.makeText(getApplicationContext(), "error"+task.getException(), Toast.LENGTH_SHORT).show();
           }
            }
        });


    }

    private void fatchinlist() {
        FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection("TenAmBatchData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        ModelClass modelClass = document.toObject(ModelClass.class);
                        userlist.add(modelClass);

                    }

                    firebaseFirestore.collection("userImageData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    ImageModelClass imageModelClass = document.toObject(ImageModelClass.class);
                                    imagelist.add(imageModelClass);


                                }

                            }


                            RecyleAdapter customRecyclerAdapter = new RecyleAdapter(imagelist, userlist, getApplicationContext());
                            recyclerView.setAdapter(customRecyclerAdapter);


                        }
                    });

                }
            }
        });

    }

    private void fatchData() {
        String userid = firebaseAuth.getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection("TenAmBatchData").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()){

                    String  nametext = document.get("nametext").toString();
                    String numbertext = document.get("numbertext").toString();
                    name.setText(nametext);
                    number.setText(numbertext);

                }else {
                    Toast.makeText(getApplicationContext(), "Error"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id  = item.getItemId();
        switch (id){
            case R.id.logoutmenu:
                firebaseAuth.signOut();
                startActivity(new Intent(HomeActivity.this ,MainActivity.class));
                finish();
                break;

            case R.id.addpicture:
                startActivity(new Intent( HomeActivity.this,ImageUpdateActivity.class));
                finish();
                break;

        }

        return true;
    }
}

