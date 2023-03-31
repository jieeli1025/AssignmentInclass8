package com.example.assignment_8;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.assignment_8.model.Friends;
import com.example.assignment_8.model.FriendsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class MainFragment extends Fragment {

    private static final String ARG_FRIENDS = "friendsarray";
    private ArrayList<Friends> mFriends;
    private int position;
    private RecyclerView recyclerView;
    private OnLogoutButtonClickedListener mListener;
    private FriendsAdapter friendsAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    //private ArrayList<Friends> mFriends;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
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
                Log.d("main fragment - initial friends data", mFriends.toString());
            }
        } else {
            mFriends = new ArrayList<>(); // initialize the mFriends ArrayList here
        }
            //            Initializing Firebase...
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();

            //            Loading initial data...
        loadData();
        }


    private void loadData() {
        Log.d("loading", "loadData: ");
        ArrayList<Friends> friends = new ArrayList<>();
        Log.d("Main fragment - users got back ", mUser.getUid());
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
                                Log.d("MainFragment: friend snapshot received back from firebase", friend.toString());
                                friends.add(friend);

                            }
                            updateRecyclerView(friends);
                        }
                    }
                });


    }

    public void updateRecyclerView(ArrayList<Friends> friends){
        this.mFriends = friends;
        Log.d("Main fragment - updating recyler view ", mFriends.toString());
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

        Button logoutButton = rootView.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLogoutButtonClicked();
            }
        });



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
    }


}