package com.example.assignment_8;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.example.assignment_8.model.Friends;
import com.example.assignment_8.model.FriendsAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.IloginFragmentActions, RegisterFragment.IregisterFragmentAction, FriendsAdapter.IfriendsListRecyclerAction,
        MainFragment.OnLogoutButtonClickedListener,  chatFragment.backpressed, EditProfileFragment.EditProfileFragmentListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    public static Bitmap imageBitmap = null;
    public static Bitmap chatImageBitmap = null;

    private Friends friend;


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



    ActivityResultLauncher<Intent> startActivityForResults = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("Main: activityforResults back", "onActivityResult: ");
                    if(result.getResultCode() == RESULT_OK){
                        // get a bitMap from the camera
                        chatImageBitmap = (Bitmap) result.getData().getExtras().get("chat");
                        currentUser = result.getData().getExtras().getParcelable("currentUser");
                        mAuth= result.getData().getExtras().getParcelable("currentUser");
                        friend = result.getData().getExtras().getParcelable("friend");
                        Log.d("Main: chatBitmap received back from camera", String.valueOf(chatImageBitmap));
                        Log.d("Main: currentUser received back from camera", String.valueOf(currentUser));
                        Log.d("Main: mAuth received back from camera", String.valueOf(mAuth));
                        Log.d("Main: friend received back from camera", String.valueOf(friend));

                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.mainLayout, chatFragment.newInstance(friend),"chatFragment")
                                .commit();
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
    public void chatButtonPressedFromRecyclerView(Friends chatfriend) {
        friend = chatfriend;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, chatFragment.newInstance(friend),"chatFragment")
                .commit();

    }

        @Override
        public void onLogoutButtonClicked() {
            logout();
        }

    @Override
    public void gotoEditProfile() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, EditProfileFragment.newInstance(),"editProfileFragment")
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void BackPressed() {
        populateScreen();
    }

    @Override
    public void chattophotos(FirebaseUser user) {
        this.currentUser = user;
        Intent toCamerachat = new Intent(this, ChatCameraActivity.class);
        toCamerachat.putExtra("currentUser", currentUser);
        toCamerachat.putExtra("friend", friend);
        setResult(RESULT_OK, toCamerachat);
        startActivityForResults.launch(toCamerachat);
    }


    @Override
    public void editProfiletoCamera() {
        Intent toCamera = new Intent(this, CameraControl.class);
        startActivityForResult.launch(toCamera);
    }

    @Override
    public void editProfiletoMain() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, MainFragment.newInstance(),"mainFragment")
                .commit();
    }
}
