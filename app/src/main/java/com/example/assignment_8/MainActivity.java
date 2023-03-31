package com.example.assignment_8;

import androidx.appcompat.app.AppCompatActivity;

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
