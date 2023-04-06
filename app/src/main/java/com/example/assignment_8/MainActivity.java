package com.example.assignment_8;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;

import com.example.assignment_8.model.Friends;
import com.example.assignment_8.model.FriendsAdapter;
import com.example.assignment_8.model.MyFirebaseAuth;
import com.example.assignment_8.model.MyFirebaseFirestore;
import com.example.assignment_8.model.MycurrentFriend;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LoginFragment.IloginFragmentActions, RegisterFragment.IregisterFragmentAction, FriendsAdapter.IfriendsListRecyclerAction,
        MainFragment.OnLogoutButtonClickedListener,  chatFragment.backpressed, EditProfileFragment.EditProfileFragmentListener {

    MyFirebaseAuth myFirebaseAuth = MyFirebaseAuth.getInstance();
    FirebaseAuth mAuth = myFirebaseAuth.getAuth();

    FirebaseFirestore db = MyFirebaseFirestore.getInstance().getDb();

    private FirebaseUser currentUser;
    public static Bitmap imageBitmap = null;
    public static Bitmap chatImageBitmap = null;
    ProgressDialog progressDialog;

    private Friends friend;


    private String chatCount = "a";

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA);
    Date now = new Date();
    String fileName = formatter.format(now);



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




    ActivityResultLauncher<Intent> startActivityForResultss = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK){
                        // get a bitMap from the camera
                        imageBitmap = (Bitmap) result.getData().getExtras().get("profile");
                        currentUser = result.getData().getExtras().getParcelable("currentUser");
                        Log.d("Main: imageBitmap received back from camera(profile)", String.valueOf(imageBitmap));


                        mAuth = myFirebaseAuth.getAuth();
                        db = MyFirebaseFirestore.getInstance().getDb();

                        Log.d("mAuth", mAuth.toString());
                        Log.d("current user", currentUser.getEmail());
                        Log.d("db", db.toString());


                            String uploademail = currentUser.getEmail().replace("@", "1");
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


                        Friends updatedInfo = new Friends(currentUser.getDisplayName(), currentUser.getEmail(), fileName);
                        Log.d("filename", fileName);
                        db.collection("users")
                                .document("authenticatedUsers")
                                .collection("friends")
                                .document(currentUser.getEmail())
                                .set(updatedInfo)
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

                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("Updating your profile picture....");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    }, 2000);


                    populateScreen();
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
                        friend = MycurrentFriend.getInstance().getSavedData();
                        mAuth = myFirebaseAuth.getAuth();
                        chatCount = result.getData().getExtras().getString("chatCount");
                        Log.d("Main: chatBitmap received back from camera", String.valueOf(chatImageBitmap));
                        Log.d("Main: currentUser received back from camera", String.valueOf(currentUser));
                        Log.d("Main: mAuth received back from camera", String.valueOf(mAuth));
                        Log.d("Main: friend received back from camera", String.valueOf(friend));
                        Log.d("Main: chatCount received back from camera", String.valueOf(chatCount));


                        db = FirebaseFirestore.getInstance();





                        String username = mAuth.getCurrentUser().getDisplayName();
                        String email = mAuth.getCurrentUser().getEmail();



                        // upload image to storage
                        String uploademail = email.replace("@", "1");
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/"+ uploademail + fileName );
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        MainActivity.chatImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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



                        Map<String, Object> newChat = new HashMap<>();
                        newChat.put("message", "");
                        newChat.put("sender", username);
                        newChat.put("type", 1);
                        newChat.put("image", fileName);





                        db.collection("users")
                                .document("authenticatedUsers")
                                .collection("friends")
                                .document(email)
                                .collection(friend.getEmail())
                                .document(chatCount+"")
                                .set(newChat)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("chat fragment: chat added to sender", "friends added");
                                        chatCount += "a";

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("FAILED TO ADD A chat", String.valueOf(e));
                                    }
                                });



                        db.collection("users")
                                .document("authenticatedUsers")
                                .collection("friends")
                                .document(friend.getEmail())
                                .collection(email)
                                .document(chatCount+"")
                                .set(newChat)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("chat fragment: chat added to friends", "friends added");
                                        chatCount += "a";
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("FAILED TO ADD A chat", String.valueOf(e));
                                    }
                                });


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
                    .addToBackStack(null)
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainLayout, LoginFragment.newInstance(),"loginFragment")
                    .addToBackStack(null)
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
                .addToBackStack(null)
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
                .addToBackStack(null)
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
    public void chattophotos(FirebaseUser user, String chatCount) {
        this.currentUser = user;
        Intent toCamerachat = new Intent(this, ChatCameraActivity.class);
        toCamerachat.putExtra("currentUser", currentUser);
        toCamerachat.putExtra("friend", friend);
        toCamerachat.putExtra("chatCount", chatCount);
        setResult(RESULT_OK, toCamerachat);
        startActivityForResults.launch(toCamerachat);
    }


    @Override
    public void editProfiletoCamera() {
        Intent toCamera = new Intent(this, ProfileCameraControl.class);
        toCamera.putExtra("currentUser", currentUser);
        setResult(RESULT_OK, toCamera);
        startActivityForResultss.launch(toCamera);
    }

    @Override
    public void editProfiletoMain() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainLayout, MainFragment.newInstance(),"mainFragment")
                .addToBackStack(null)
                .commit();
    }
}
