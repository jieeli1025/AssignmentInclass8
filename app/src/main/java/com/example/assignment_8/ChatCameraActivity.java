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

import com.example.assignment_8.model.Friends;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatCameraActivity extends AppCompatActivity {

    private ImageView imageCamera;
    private Button bthCamera, sendImage;
    private final int CAMERA_REQUEST_CODE = 100;
    private Bitmap bitmap;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private Friends friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_camera);

        imageCamera = findViewById(R.id.chatimageCamera);
        bthCamera = findViewById(R.id.chatbuttonTakePicture);
        sendImage = findViewById(R.id.chatSendImage);

        Intent intent = getIntent();

        mUser = intent.getExtras().getParcelable("currentUser");
        friends = intent.getExtras().getParcelable("friends");

        Log.d("muser in chat camera", mUser.getEmail());

        bthCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainthenChat = new Intent(ChatCameraActivity.this, MainActivity.class);
                toMainthenChat.putExtra("chat", bitmap);
                toMainthenChat.putExtra("currentUser", mUser);
                toMainthenChat.putExtra("friends", friends);
                setResult(RESULT_OK, toMainthenChat);
                // go back to the main activity
                startActivity(toMainthenChat);

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