package com.yash.firebasedemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_email_reg,et_pass_reg;
    private Button btn_reg;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_email_reg=findViewById(R.id.et_email_reg);
        et_pass_reg=findViewById(R.id.et_pass_reg);
        btn_reg=findViewById(R.id.btn_reg);

        progressBar=findViewById(R.id.progressBar_reg);

        btn_reg.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_reg:
                registerUser();
                break;
        }
    }

    private void registerUser() {
        String userName=et_email_reg.getText().toString();
        String userPassword=et_pass_reg.getText().toString();
        Log.e("name",userName+userPassword);
        if(userName.isEmpty()){
            et_email_reg.setError("email is required");
            et_email_reg.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches())
        {
            et_email_reg.setError("please enter a valid email");
            et_email_reg.requestFocus();
            return;
        }
        if(userPassword.isEmpty())
        {
            et_pass_reg.setError("Password is required");
            et_pass_reg.requestFocus();
            return;
        }
        if (userPassword.length()<5){
            et_pass_reg.setError("password should be greater than 5");
            et_pass_reg.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(userName , userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){

                            Log.e("issuccedd","abc");
                            Toast.makeText(getApplicationContext(), "User registered successfully", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent i=new Intent(RegisterActivity.this,SignInActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                        else
                        {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException){

                                Toast.makeText(getApplicationContext(), "User already registered", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
