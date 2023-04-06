package com.example.assignment_8.model;

import static android.view.View.GONE;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment_8.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>{
private ArrayList<Chat> texts;
    private StorageReference mStorageRef;
    private Context context;

public ChatAdapter(ArrayList<Chat> texts, Context context){
        this.texts=texts;
        this.context=context;
        }

        public ArrayList<Chat> getTexts() {
        return texts;
        }

        public void setChats(ArrayList<Chat> texts) {
        this.texts = texts;
        }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView text;
        private final TextView sender;
        private final ImageView chatImageView;

        public TextView getText() {
            return text;
        }

        public TextView getSender() {
            return sender;
        }

        public ImageView getChatImageView() {
            return chatImageView;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.chatImageView = itemView.findViewById(R.id.chatImageView);
            this.text = itemView.findViewById(R.id.chatText);
            this.sender = itemView.findViewById(R.id.senderText);
        }


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_list, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = this.getTexts().get(position);
        Log.d("recyclerView Data received back ", chat.toString());

        if (chat.getType() == 0) {
            holder.getText().setText(chat.getMessage());
            holder.getSender().setText(chat.getSender());
            holder.getChatImageView().setVisibility(GONE); // Set ImageView to GONE
        } else {
            holder.getSender().setText(chat.getSender());
            String replacedemail = chat.getEmail();
            replacedemail = replacedemail.replace("@", "1");
            mStorageRef = FirebaseStorage.getInstance().getReference().child("images/" + replacedemail + chat.getimage());
            Log.d("mstorageRef", mStorageRef.toString());

            mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Glide.with(context)
                            .load(imageUrl).into(holder.getChatImageView());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("demo: ", "onFailure: unable to get profileimage ");
                }
            });

            holder.getText().setText("");
            holder.getChatImageView().setVisibility(View.VISIBLE); // Set ImageView to VISIBLE
        }



    }



    @Override
    public int getItemCount() {
        return this.getTexts().size();
    }









}
