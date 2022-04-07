package com.example.firebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    TextInputEditText email,password;
    Button button;
    FirebaseAuth firebaseAuth;
    TextView  textView ,forgottext;


    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conntection();



        forgottext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText restmail = new EditText(view.getContext());
                final AlertDialog.Builder passwordRestDialog = new AlertDialog.Builder(view.getContext());
                passwordRestDialog.setTitle("Reset password?");
                passwordRestDialog.setMessage("Enter Your Email to Received");
                passwordRestDialog.setView(restmail);
                passwordRestDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = restmail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Toast.makeText(getApplicationContext(), "Reset Link to your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Error ! Reset link  is Not sent"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });
                passwordRestDialog.setNegativeButton("Not", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                passwordRestDialog.create().show();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailtext = email.getText().toString().trim();
                String passwordtext = password.getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword(emailtext,passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Your Successfilly Register", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);

                          startActivity(intent);
                          finish();


                        }
                        else {
                            Toast.makeText(getApplicationContext(), "error"+task.getException(), Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });




    }




    @Override
    protected void onStart() {
        super.onStart();

        if (firebaseAuth.getCurrentUser() !=null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);

            startActivity(intent);
            finish();


        }
    }

    private void conntection() {
        email = findViewById(R.id.emailedit);
        password = findViewById(R.id.passwordedit);
        button = findViewById(R.id.loginbtn);
        firebaseAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.registerss);
        forgottext = findViewById(R.id.forgotpassword);
        progressBar = findViewById(R.id.progressbarmain);


    }

}