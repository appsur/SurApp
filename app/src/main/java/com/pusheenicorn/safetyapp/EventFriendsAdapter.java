package com.pusheenicorn.safetyapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventFriendsAdapter extends RecyclerView.Adapter<EventFriendsAdapter.ViewHolder> {
    List<Friend> mFriends;
    Context context;

    // constructor
    public EventFriendsAdapter(List<Friend> friends) {
        mFriends = friends;
    }

    @NonNull
    @Override
    public EventFriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View friendView = inflater.inflate(R.layout.item_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(friendView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventFriendsAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Friend friend = mFriends.get(position);

        // populate the views according to this data
        holder.tvName.setText(friend.getName());

        try {
            User friendUser = (User) friend.fetchIfNeeded().getParseUser("user");
            String phoneNumber = friendUser.fetchIfNeeded().getString("phonenumber");
            phoneNumber = "(" + phoneNumber.substring(0, 3) + ") "
                    + phoneNumber.substring(3, 6) + " - "
                    + phoneNumber.substring(6, 10);
            holder.tvPhoneNumber.setText(phoneNumber);

            if (friendUser.fetchIfNeeded().getParseFile("profileimage") != null)
            {
                Glide.with(context)
                        .load(friendUser.fetchIfNeeded().getParseFile("profileimage")
                                .getUrl())
                        .into(holder.ivProfileImage);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mFriends.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Friend> list) {
        mFriends.addAll(list);
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
                Friend friend = mFriends.get(position);
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
                Friend friend = mFriends.get(position);
                // TODO -- Add as friend??
            }
        }
    }
}
