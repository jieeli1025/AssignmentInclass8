package com.example.assignment_8;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.example.assignment_8.model.Friends;
import com.example.assignment_8.model.FriendsAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.IloginFragmentActions, RegisterFragment.IregisterFragmentAction, FriendsAdapter.IfriendsListRecyclerAction,
        MainFragment.OnLogoutButtonClickedListener,  chatFragment.backpressed{

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static Bitmap imageBitmap = null;

    ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        // get a bitMap from the camera
                        imageBitmap = (Bitmap) result.getData().getExtras().get("image");
                        Log.d("Main: imageBitmap received back from camera", String.valueOf(imageBitmap));
                        populateRegisterFragment();

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login page -activity");
        mAuth = FirebaseAuth.getInstance();






    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        Log.d("currentUser", String.valueOf(currentUser));
        populateScreen();
    }

    private void populateScreen() {
        if (currentUser != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainLayout, MainFragment.newInstance(),"mainFragment")
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainLayout, LoginFragment.newInstance(),"loginFragment")
                    .commit();
        }
    }


    @Override
    public void populateMainFragment(FirebaseUser user) {
        this.currentUser = user;
        populateScreen();
    }

    @Override
    public void populateRegisterFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, RegisterFragment.newInstance(),"registerFragment")
                .commit();

    }


    public void logout() {
        mAuth.signOut();
        currentUser = null;
        populateScreen();
    }

        @Override
        public void registerDone(FirebaseUser mUser) {
            this.currentUser = mUser;
            populateScreen();
        }

    @Override
    public void registerToCamera() {
        Intent toCamera = new Intent(this, CameraControl.class);
        startActivityForResult.launch(toCamera);
    }


    @Override
    public void chatButtonPressedFromRecyclerView(Friends friend) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, chatFragment.newInstance(friend),"chatFragment")
                .commit();

    }

        @Override
        public void onLogoutButtonClicked() {
            logout();
        }


    @Override
    public void BackPressed() {
        populateScreen();
    }
}
