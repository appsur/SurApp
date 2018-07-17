package com.pusheenicorn.safetyapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.User;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private List<User> mFriends;
    Context context;

    //pass in the Tweets array in the constructor
    public FriendsAdapter(List<User> friends) {
        mFriends = friends;
    }

    //for each row, inflate the layout and cache references (only invoked if creating a new row)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    public void clear() {
        mFriends.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> list) {
        mFriends.addAll(list);
        notifyDataSetChanged();
    }
    //bind the values based on the position of the element

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.ViewHolder holder, int position) {
        //get the data according to position
        User friends = mFriends.get(position);
        //populate the views according to this data
//        holder.tvTimeElapsed.setText(friends.getDescription());
//        holder.tvUsername.setText(post.getUser().getUsername());

        //load image into a viewable
//        Glide.with(context).load(post.getImage().getUrl()).into(holder.ivImagePost);
    }

    //get item count
    public int getItemCount() {
        return mFriends.size();
    }


    //create the ViewHolder class

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public TextView location;
        public TextView tvTimeElapsed;
        public TextView tvTime;

        ParseUser user;

        public ViewHolder(final View itemView) {
            super(itemView);

            //perform findViewById lookups

            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTimeElapsed = (TextView) itemView.findViewById(R.id.tvTimeElapsed);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);

            user = ParseUser.getCurrentUser();

        }
    }
}
