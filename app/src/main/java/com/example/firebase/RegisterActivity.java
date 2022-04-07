package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    TextView name,password,number,email,verifactionntext;
    EditText nameedit,passwordedit,numberedit,emailedit,    numberoptedit,verificationedits;
    Button submit,verifaction,numberverificationbtn;
    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String phonenumber;
    String verification_code;
    CountryCodePicker countryCodePicker;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        connectxml();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();
        //countryCodePicker.registerCarrierNumberEditText(numberoptedit);




        numberverificationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String numbers = numberoptedit.getText().toString().trim();
                    phonenumber = "+91"+numbers;


                PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phonenumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(RegisterActivity.this)
                        .setCallbacks(mCallbacks)
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }
        });

        verifaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.GONE);
                String otptext = verificationedits.getText().toString().trim();
                verficode(otptext);
                Intent intent = new Intent(RegisterActivity.this,HomeActivity.class);

                startActivity(intent);
                finish();

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emaitxt = emailedit.getText().toString().trim();
                String numbertxt = numberedit.getText().toString().trim();
                String passwordtxt = passwordedit.getText().toString().trim();
                String nametxt = nameedit.getText().toString().trim();


                if(TextUtils.isEmpty(emaitxt)){
                    emailedit.setError("Empty Email");
                }else if (TextUtils.isEmpty(passwordtxt)){
                    passwordedit.setError("Empty Password");
                }
                else if (TextUtils.isEmpty(numbertxt)){
                    numberedit.setError("Empty Number");
                }
                else if (TextUtils.isEmpty(nametxt)){
                    nameedit.setError("Empty Name");
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                   createuser(emaitxt,passwordtxt,numbertxt,nametxt);
                }
            }
        });
    }

    private void createuser(String emaitxt, String passwordtxt, String numbertxt, String nametxt) {

        firebaseAuth.createUserWithEmailAndPassword(emaitxt,passwordtxt).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {


                if (task.isSuccessful()){
                    String userid = firebaseAuth.getCurrentUser().getUid();
                    HashMap<String,String> usermap  = new HashMap<>();
                    usermap.put("emailtext",emaitxt);
                    usermap.put("passwordtext",passwordtxt);
                    usermap.put("numbertext",numbertxt);
                    usermap.put("nametext",nametxt);
                    usermap.put("userid",userid);

                    firebaseFirestore.collection("TenAmBatchData").document(userid).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Register Susccesfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                                finish();

                            }else{
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "error"+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "error"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            progressBar.setVisibility(View.GONE);

            String code = phoneAuthCredential.getSmsCode();
            verficode(code);
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);

            startActivity(intent);
            finish();


        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(), "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verification_code =s;
        }
    };

    private void verficode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code,code);
        signwithcredential(credential);
    }
//    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
//        firebaseAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//
//                            FirebaseUser user = task.getResult().getUser();
//                            // Update UI
//                        } else {
//                            // Sign in failed, display a message and update the UI
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
//                                // The verification code entered was invalid
//                            }
//                        }
//                    }
//                });
//    }

    private void signwithcredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);

                    startActivity(intent);
                    finish();


                    Toast.makeText(getApplicationContext(), " Phone number verified", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "error" + task.getException(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
        private void connectxml() {
        name = findViewById(R.id.emailtext);
        password =findViewById(R.id.passwordtext);
        email = findViewById(R.id.emailtext);
        number = findViewById(R.id.numbertext);
        submit = findViewById(R.id.submitbtn);

        nameedit = findViewById(R.id.nameedit);
        passwordedit = findViewById(R.id.passwordedit);
        emailedit = findViewById(R.id.emailedit);
        verifaction = findViewById(R.id.verificationbtn);
        verifactionntext = findViewById(R.id.verificationtext);
        verificationedits = findViewById(R.id.verificationedit);
        number = findViewById(R.id.numberotp);
        numberoptedit = findViewById(R.id.numbereditotp);
        numberverificationbtn = findViewById(R.id.numberverificationbtn);

        progressBar = findViewById(R.id.progressbar);

    }
}










