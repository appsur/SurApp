package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventUsersAdapter extends RecyclerView.Adapter<EventUsersAdapter.ViewHolder> {
    List<User> mUsers;
    Context context;

    // constructor
    public EventUsersAdapter(List<User> users) {
        mUsers = users;
    }

    @NonNull
    @Override
    public EventUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View userView = inflater.inflate(R.layout.item_event_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventUsersAdapter.ViewHolder holder, int position) {
        // get the data according to position
        User user = mUsers.get(position);

        // populate the views according to this data
        // populate the views according to this data
        holder.tvName.setText(user.getName());
        holder.tvPhoneNumber.setText(user.getPhonNumber());

        if (user.getProfileImage() != null)
        {
            Glide.with(context).load(user.getProfileImage().getUrl()).into(holder.ivProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Nullable
        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        @BindView(R.id.tvPhoneNumber)
        TextView tvPhoneNumber;

        @BindView(R.id.tvName) TextView tvName;

        public ViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                User user = mUsers.get(position);
            }

            itemView.setOnClickListener(this);
        }

        @Override
        // when the user clicks on a row, show DetailsActivity for the selected movie
        public void onClick(View v) {
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                User user = mUsers.get(position);
                // TODO -- Add as friend??
            }
        }
    }
}

