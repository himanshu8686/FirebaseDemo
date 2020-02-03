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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_link;
    private Button btn_signin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar_signin;
    private EditText et_signin;
    private EditText et_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        tv_link=findViewById(R.id.tv_link);
        tv_link.setOnClickListener(this);
        btn_signin=findViewById(R.id.btn_signin);
        btn_signin.setOnClickListener(this);
        progressBar_signin=findViewById(R.id.progressBar_signin);

        et_signin=findViewById(R.id.et_signin);
        et_pass=findViewById(R.id.et_pass);
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null){
            finish();

            Intent i=new Intent(SignInActivity.this,ProfileActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    @Override
    public void onClick(View v)
    {
     if (v==tv_link)
     {
         Intent intent=new Intent(SignInActivity.this,RegisterActivity.class);
         startActivity(intent);
     }
     else if (v==btn_signin)
     {
         userLogin();
     }
    }

    private void userLogin()
    {
        String userName=et_signin.getText().toString();
        String userPassword=et_pass.getText().toString();
        Log.e("name",userName+userPassword);
        if(userName.isEmpty()){
            et_signin.setError("email is required");
            et_signin.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches())
        {
            et_signin.setError("please enter a valid email");
            et_signin.requestFocus();
            return;
        }
        if(userPassword.isEmpty())
        {
            et_pass.setError("Password is required");
            et_pass.requestFocus();
            return;
        }
        if (userPassword.length()<5){
            et_pass.setError("password should be greater than 5");
            et_pass.requestFocus();
            return;
        }

        progressBar_signin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar_signin.setVisibility(View.GONE);
                if (task.isSuccessful())
                {
                    finish();
                    Intent intent=new Intent(SignInActivity.this,ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
