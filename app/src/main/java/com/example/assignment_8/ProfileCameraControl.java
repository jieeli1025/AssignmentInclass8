package com.example.assignment_8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseUser;

public class ProfileCameraControl extends AppCompatActivity {
    private ImageView imageCamera;
    private Button bthCamera, saveImage;
    private final int CAMERA_REQUEST_CODE = 100;
    private Bitmap bitmap;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_camera_control);

        imageCamera = findViewById(R.id.profileimageCamera);
        bthCamera = findViewById(R.id.profilebuttonTakePicture);
        saveImage = findViewById(R.id.profileSendImage);


        Intent intent = getIntent();

        mUser = intent.getExtras().getParcelable("currentUser");


        bthCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toProfile = new Intent();
                toProfile.putExtra("profile", bitmap);
                toProfile.putExtra("currentUser", mUser);
                setResult(RESULT_OK, toProfile);
                finish();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                bitmap = (Bitmap) data.getExtras().get("data");
                Log.d("bitmap", bitmap.toString());
                imageCamera.setImageBitmap(bitmap);
            }
        }



    }



}