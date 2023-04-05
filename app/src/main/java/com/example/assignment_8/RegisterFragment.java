package com.example.assignment_8;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.assignment_8.model.Friends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth mAuth;

    String fileName;

    private ImageView profileImage;
    private FirebaseUser mUser;
    private EditText editTextName, editTextEmail, editTextPassword, editTextRepPassword;
    private Button buttonRegister;
    private String name, email, password, rep_password;
    private IregisterFragmentAction mListener;
    private FirebaseFirestore db;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mUser = mAuth.getCurrentUser();


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IregisterFragmentAction){
            this.mListener = (IregisterFragmentAction) context;
        } else{
            throw new RuntimeException(context.toString()
                    + "must implement RegisterRquest");
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Register Acccount");
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        editTextName = rootView.findViewById(R.id.EditTextName);
        editTextEmail = rootView.findViewById(R.id.registerEditEmail);
        editTextPassword = rootView.findViewById(R.id.registerEditPassword);
        editTextRepPassword = rootView.findViewById(R.id.repeatedPassword);
        buttonRegister = rootView.findViewById(R.id.editupdateButton);
        profileImage = rootView.findViewById(R.id.EditprofileImage);
        if (profileImage != null) {
            profileImage.setImageBitmap(MainActivity.imageBitmap);
        }

        buttonRegister.setOnClickListener(this);


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to CameraControl activity
                mListener.registerToCamera();


            }
        });


        return rootView;
    }

    @Override
    public void onClick(View view) {
        this.name = String.valueOf(editTextName.getText()).trim();
        this.email = String.valueOf(editTextEmail.getText()).trim();
        this.password = String.valueOf(editTextPassword.getText()).trim();
        this.rep_password = String.valueOf(editTextRepPassword.getText()).trim();

        if(view.getId()== R.id.editupdateButton){
//            Validations........
            if(name.equals("")){
                Toast.makeText(getActivity(), "Name must not be empty!", Toast.LENGTH_SHORT).show();
            }
            if(email.equals("")){
               Toast.makeText(getActivity(), "Email must not be empty!", Toast.LENGTH_SHORT).show();
            }
            if(password.equals("")){
                Toast.makeText(getActivity(), "Password must not be empty!", Toast.LENGTH_SHORT).show();
            }
            if(!rep_password.equals(password)){
                Toast.makeText(getActivity(), "Password must match", Toast.LENGTH_SHORT).show();
            }
            if (password.length() < 6){
                Toast.makeText(getActivity(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            }
            if (MainActivity.imageBitmap == null){
                Toast.makeText(getActivity(), "Please select a profile image", Toast.LENGTH_SHORT).show();
            }



//            Validation complete.....
            if(!name.equals("") && !email.equals("")
                    && !password.equals("")
                    && rep_password.equals(password)){
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
                Date now = new Date();
                fileName = formatter.format(now);

                mUser = mAuth.getCurrentUser();
                Friends newFriend = new Friends(name, email, fileName);
                Log.d("demo: Register fragment new friend object ", newFriend.toString());


                // Firebase authentication: Create user.......
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Log.d("demo register fragment", "successfully registered ");
                                    mUser = task.getResult().getUser();
//                                    Adding name to the FirebaseUser...
                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    mUser.updateProfile(profileChangeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Log.d("demo register fragment", "name added to user");
                                                        mListener.registerDone(mUser);
                                                    }
                                                }
                                            });

                                }
                            }


                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("demo register fragment failed", e.toString());
                            }
                        });

                addToFirebase(newFriend);
                uploadImage();

            }
        }

    }

    private void uploadImage(){
        String uploademail = email.replace("@", "1");
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+ uploademail + fileName );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MainActivity.imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("demo", "upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("demo", "upload success");
            }
        });

    }

    private void addToFirebase(Friends friend) {
        db.collection("users")
                .document("authenticatedUsers")
                .collection("friends")
                .document(friend.getEmail())
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("demo register fragment: friends added", "friends added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("demo FAILED TO ADD A FRIEND", String.valueOf(e));
                    }
                });
    }




    public interface IregisterFragmentAction {
        void registerDone(FirebaseUser mUser);
        void registerToCamera();
    }
}