package com.example.emailandphoneauth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneSignActivity extends AppCompatActivity {

    EditText phoneNumber, sentCode;
    Button sendSMS, phoneSignIn;
    String sentCodeByAuthenticator;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_sign);

        phoneNumber = findViewById(R.id.editTextPhoneNumber);
        sentCode = findViewById(R.id.editTextCode);
        sendSMS = findViewById(R.id.buttonSendSMS);
        phoneSignIn = findViewById(R.id.buttonPhoneSignIn);

        sendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userPhoneNumber = phoneNumber.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(userPhoneNumber, 60, TimeUnit.SECONDS,
                        PhoneSignActivity.this, mCallback);

            }
        });

        phoneSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithPhoneCode();

            }
        });
    }

    public void signInWithPhoneCode(){
        String userEnteredCode = sentCode.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(sentCodeByAuthenticator, userEnteredCode);
        signInWithAuthenticationCredential(credential);
    }

    public void signInWithAuthenticationCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(PhoneSignActivity.this, MainMenu.class);
                            startActivity(intent);
                        }
                    }
                });

    }



    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(PhoneSignActivity.this, "Code is sent successfully", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            Toast.makeText(PhoneSignActivity.this, "Code couldn't be sent", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            sentCodeByAuthenticator = s;
        }
    };
}