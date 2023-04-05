package com.example.assignment_8;

import android.content.Context;
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
import android.widget.EditText;

import com.example.assignment_8.model.Chat;
import com.example.assignment_8.model.ChatAdapter;
import com.example.assignment_8.model.Friends;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link chatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class chatFragment extends Fragment {


    private static final String ARG_PARAM1 = "friend";

    private backpressed mListener;
    private String email, username, message;
    private ArrayList<Chat> chatArray;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;
    private String chatCount = "a";
    private ChatAdapter chatAdapter;
    private Button buttonPhotos;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private Friends friend;

    public chatFragment() {
        // Required empty public constructor
    }



    public static chatFragment newInstance(Friends friend) {
        chatFragment fragment = new chatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1,friend);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
              friend = (Friends) getArguments().getSerializable(ARG_PARAM1);
        }
        chatArray = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Log.d("chatfragment my currentuser mUser;", mUser.getEmail());

        loadchat();
    }


    private void loadchat() {
        Log.d("chat fragment loadingchat", "loadData: ");
        ArrayList<Chat> ChatArray = new ArrayList<>();

        db.collection("users")
                .document("authenticatedUsers")
                .collection("friends")
                .document(mAuth.getCurrentUser().getEmail())
                .collection(friend.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){


                                Chat chats = documentSnapshot.toObject(Chat.class);
                                Log.d("ChatFragment: friend snapshot received back from firebase", chats.toString());
                                ChatArray.add(chats);
                                chatCount = (documentSnapshot.getId() + "a");

                            }
                            updateRecyclerView(ChatArray);
                        }
                    }
                });


    }


    public void updateRecyclerView(ArrayList<Chat> chatArray){
        this.chatArray = chatArray;
        Log.d("chat fragment - updating recyler view ", chatArray.toString());
        chatAdapter.setChats(chatArray);
        chatAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = rootView.findViewById(R.id.chatRecyclerVieww);
        buttonPhotos = rootView.findViewById(R.id.cameraButton);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        chatAdapter = new ChatAdapter(chatArray);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(chatAdapter);

        getActivity().setTitle("Chat with friends" );
        EditText editText = rootView.findViewById(R.id.EditChat);
        Button sendButton = rootView.findViewById(R.id.editChatButton);
        Button backButton = rootView.findViewById(R.id.backButton);

        buttonPhotos.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.chattophotos(mUser);
                    }
                }
        );

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.BackPressed();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editText.getText().toString().trim();
                username = mAuth.getCurrentUser().getDisplayName();
                email = mAuth.getCurrentUser().getEmail();
                Map<String, Object> newChat = new HashMap<>();
                newChat.put("message", message);
                newChat.put("sender", username);
                newChat.put("type", 0);






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



                loadchat();
                editText.setText("");

            }





});



        return rootView;
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof backpressed) {
            mListener = (backpressed) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement backpressed");
        }
    }


    public interface backpressed{
        void BackPressed();
        void chattophotos(FirebaseUser mUser);
    }
}