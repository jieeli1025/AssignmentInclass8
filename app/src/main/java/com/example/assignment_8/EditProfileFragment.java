package com.example.assignment_8;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.assignment_8.model.Friends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    private Button buttonSave;
    private ImageView camera;
    ProgressDialog progressDialog;
    private EditText editTextName;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private StorageReference mStorageRef;

    private String timestamp;
    private String timeNow;
    private String oldImage;


    private EditProfileFragmentListener mlistener;



    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance() {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
        Date now = new Date();
        timeNow = formatter.format(now);
        getImage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        buttonSave = rootView.findViewById(R.id.editupdateButton);
        camera = rootView.findViewById(R.id.EditprofileImage);
        editTextName = rootView.findViewById(R.id.EditTextName);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mlistener.editProfiletoCamera();
            }
        });


        loadData();

        buttonSave.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Friends friend = new Friends();
                        friend.setName(editTextName.getText().toString());
                        friend.setEmail(mUser.getEmail());
                        friend.setImage(oldImage);
                        Log.d("Edit profile save button clciked: new data saved  ", friend.toString());

                        progressDialog = new ProgressDialog(EditProfileFragment.this.getActivity());
                        progressDialog.setTitle("Updating File....");
                        progressDialog.show();

                        db.collection("users")
                                .document("authenticatedUsers")
                                .collection("friends")
                                .document(mUser.getEmail())
                                .set(friend)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Edit profile successful updated info: ", "onSuccess: ");


                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mlistener.editProfiletoMain();
                                                progressDialog.dismiss();
                                            }
                                        }, 2000);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("demo: ", "onFailure: ");
                                    }
                                });
                    }
                }
        );

        return rootView;
    }


    private void getImage(){
        db.collection("users")
                .document("authenticatedUsers")
                .collection("friends")
                .document(mUser.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Friends friend = documentSnapshot.toObject(Friends.class);
                    oldImage = friend.getImage();
                    Log.d("demo: old image", oldImage);
            }

            }
        });}



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
                                if(friend.getEmail().equals(mUser.getEmail())){
                                    timestamp = friend.getImage();
                                    editTextName.setText(friend.getName());
                                }

                            }


                            String replacedemail = mUser.getEmail();
                            replacedemail = replacedemail.replace("@", "1");
                            mStorageRef = FirebaseStorage.getInstance().getReference().child("images/"+replacedemail+ timestamp);
                            Log.d("Edit profile mstorageRef", mStorageRef.toString());

                            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Glide.with(getContext()).load(imageUrl).into(camera);
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EditProfileFragmentListener){
            mlistener = (EditProfileFragmentListener) context;
        }else{
            throw new RuntimeException(context.toString() + "must implement EditProfileFragmentListener");
        }
    }

    public interface EditProfileFragmentListener{
        void editProfiletoCamera();
        void editProfiletoMain();
    }

}