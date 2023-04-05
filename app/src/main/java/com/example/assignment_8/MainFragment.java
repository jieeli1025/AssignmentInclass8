package com.example.assignment_8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.example.assignment_8.model.Friends;
import com.example.assignment_8.model.FriendsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainFragment extends Fragment {

    private static final String ARG_FRIENDS = "friendsarray";
    private ArrayList<Friends> mFriends;
    private int position;
    private RecyclerView recyclerView;
    private OnLogoutButtonClickedListener mListener;
    private FriendsAdapter friendsAdapter;
    private StorageReference mStorageRef;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    //private ArrayList<Friends> mFriends;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ImageView profileImage;
    private TextView profileName;
    private String timestamp;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIENDS, new ArrayList<Friends>());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_FRIENDS)) {
                mFriends = (ArrayList<Friends>) args.getSerializable(ARG_FRIENDS);
                //Log.d("demo: main fragment - initial friends data", mFriends.toString());
            }
        } else {
            mFriends = new ArrayList<>(); // initialize the mFriends ArrayList here
        }
            //            Initializing Firebase...
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
        Log.d("MainmAuth", mAuth.toString());
            mUser = mAuth.getCurrentUser();

            //            Loading initial data...
        loadData();
        }




    private void loadData() {
        Log.d("demo: loading", "loadData: ");
        ArrayList<Friends> friends = new ArrayList<>();
   //     Log.d("demo: Main fragment - users got back ", mUser.getUid());
        db.collection("users")
                .document("authenticatedUsers")
                .collection("friends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){

                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//
                                Friends friend = documentSnapshot.toObject(Friends.class);
                              //  Log.d("demo: MainFragment: friend snapshot received back from firebase", friend.toString());
                                friends.add(friend);
                              if(friend.getEmail().equals(mUser.getEmail())){
                                  timestamp = friend.getImage();
                                    profileName.setText(friend.getName());
                              }

                            }

                            updateRecyclerView(friends);

                            String replacedemail = mUser.getEmail();
                            replacedemail = replacedemail.replace("@", "1");
                            mStorageRef = FirebaseStorage.getInstance().getReference().child("images/"+replacedemail+ timestamp);
                            Log.d("mstorageRef", mStorageRef.toString());

                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Glide.with(getContext()).load(imageUrl).into(profileImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("demo: ", "onFailure: unable to get profileimage ");
                                }
                            });



                        }
                    }
                });


    }



    public void updateRecyclerView(ArrayList<Friends> friends){
        this.mFriends = friends;
        //Log.d("Demo Main fragment - updating recyler view ", mFriends.toString());
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        friendsAdapter = new FriendsAdapter(mFriends, getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(friendsAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Main");
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        profileImage = rootView.findViewById(R.id.mainProfileImage);
        profileName = rootView.findViewById(R.id.mainNameText);

        Button logoutButton = rootView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLogoutButtonClicked();
            }
        });


        profileImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.gotoEditProfile();
                    }
                }
        );



        //setting up recycler view
        recyclerView = rootView.findViewById(R.id.FriendsRecyclerView);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        friendsAdapter = new FriendsAdapter(mFriends, getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(friendsAdapter);


        return rootView;
    }




    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        if(context instanceof OnLogoutButtonClickedListener){
            mListener = (OnLogoutButtonClickedListener) context;
        }else{
            throw new RuntimeException(context.toString() + " must implement OnLogoutButtonClickedListener");
        }
    }
    public interface OnLogoutButtonClickedListener{
        void onLogoutButtonClicked();
        void gotoEditProfile();
    }


}