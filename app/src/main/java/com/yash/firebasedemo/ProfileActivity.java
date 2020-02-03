package com.yash.firebasedemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMG = 101;
    private Button btn_logout,btn_save;
    private FirebaseAuth mAuth;
    private ImageView img_view_profile;
    private EditText et_profile;
    Uri uriProfileImg;
    private ProgressBar progressBar;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btn_save=findViewById(R.id.btn_save);
        btn_save.setOnClickListener(this);
        btn_logout=findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(this);
        img_view_profile=findViewById(R.id.img_view_profile);
        img_view_profile.setOnClickListener(this);
        et_profile=findViewById(R.id.et_profile);
        progressBar=findViewById(R.id.progressBar);

        mAuth=FirebaseAuth.getInstance();
        loadUserInformation();

    }

    private void loadUserInformation()
    {
        FirebaseUser user=mAuth.getCurrentUser();
        if (user!=null)
        {
            if (user.getPhotoUrl() !=null)
            {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(img_view_profile);
            }
            if (user.getDisplayName()!=null)
            {
                et_profile.setText(user.getDisplayName());
            }
            if(user.getPhotoUrl()==null){
                Toast.makeText(this, "photo can't be loaded ", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * will pick images
     */
    private void showImageChooser()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"select profile image"),CHOOSE_IMG);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser==null){
            finish();

            Intent i=new Intent(this,SignInActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CHOOSE_IMG && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriProfileImg =data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImg);
                img_view_profile.setImageBitmap(bitmap);

                uploadImagToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImagToFirebase()
    {
        StorageReference profileImgRef= FirebaseStorage.getInstance().getReference("profilepics/"+System.currentTimeMillis()+".jpg");
        if (uriProfileImg !=null)
        {
            progressBar.setVisibility(View.VISIBLE);
            profileImgRef.putFile(uriProfileImg)
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                           profileImageUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                       }
                   })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        if (v==btn_logout){
            mAuth=FirebaseAuth.getInstance();
            mAuth.signOut();
            finish();
            startActivity(new Intent(this,SignInActivity.class));
        }
        if(v==btn_save){

            saveUserInfo();
        }
        if(v==img_view_profile){
            showImageChooser();
        }
    }

    private void saveUserInfo()
    {
        String userName=et_profile.getText().toString();
        if (userName.isEmpty())
        {
            et_profile.setError("Name required");
            et_profile.requestFocus();
            return;
        }
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser!=null && currentUser!=null){
            UserProfileChangeRequest profile=new UserProfileChangeRequest.Builder()
                    .setDisplayName(userName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            currentUser.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
