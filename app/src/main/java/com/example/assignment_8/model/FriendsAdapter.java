package com.example.assignment_8.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignment_8.R;

import java.util.ArrayList;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder>
{

    private ArrayList<Friends> friends;
    private IfriendsListRecyclerAction mListener;
    public FriendsAdapter(ArrayList<Friends> friends, Context context)
    {
        this.friends = friends;
        if(context instanceof IfriendsListRecyclerAction){
            this.mListener = (IfriendsListRecyclerAction) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IeditButtonAction");
        }
    }

    public ArrayList<Friends> getFriends() {
        return friends;
    }
    public void setUsers(ArrayList<Friends> friends) {
        this.friends = friends;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView nameText, emailText;
        private final Button chatButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.friendName);
            emailText = itemView.findViewById(R.id.emailName);
            chatButton = itemView.findViewById(R.id.chat_button);

        }

        public TextView getNameText() {
            return nameText;
        }

        public TextView getEmailText() {
            return emailText;
        }

        public Button getChatButton() {
            return chatButton;
        }

    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_friends, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friends currentFriend = this.getFriends().get(position);
        holder.getEmailText().setText(currentFriend.getEmail());
        holder.getNameText().setText(currentFriend.getName());
        holder.getChatButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.chatButtonPressedFromRecyclerView(friends.get(holder.getAdapterPosition()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.getFriends().size();
    }






    public interface IfriendsListRecyclerAction {
        void chatButtonPressedFromRecyclerView(Friends friend);
    }

}
