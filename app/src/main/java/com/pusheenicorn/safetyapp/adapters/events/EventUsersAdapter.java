package com.pusheenicorn.safetyapp.adapters.events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventUsersAdapter extends RecyclerView.Adapter<EventUsersAdapter.ViewHolder> {
    List<User> mUsers;
    List<Friend> mFriends;
    Context context;
    User mCurrentUser;
    EventFriendsAdapter mEventFriendsAdapter;

    // constructor
    public EventUsersAdapter(List<User> users, List<Friend> friends, User currentUser,
                             EventFriendsAdapter eventFriendsAdapter) {
        mUsers = users;
        mFriends = friends;
        mCurrentUser = currentUser;
        mEventFriendsAdapter = eventFriendsAdapter;
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
        String phoneNumber = user.getPhonNumber();
        phoneNumber = "( " + phoneNumber.substring(0, 3) + ") "
                + phoneNumber.substring(3, 6)
                + " - " + phoneNumber.substring(6, 10);
        holder.tvPhoneNumber.setText(phoneNumber);

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
                Toast.makeText(context, "Hello", Toast.LENGTH_LONG).show();
                // get the tweet at the position, this won't work if the class is static
                final User user = mUsers.get(position);
                if (user.getUserName().equals(mCurrentUser.getUserName()))
                {
                    Toast.makeText(context, "Hello", Toast.LENGTH_LONG).show();
                    return;
                }
                final Friend newFriend = new Friend();
                try {
                    newFriend.setName(user.fetchIfNeeded().getString(Friend.KEY_NAME));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                newFriend.setUser(user);
                newFriend.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mCurrentUser.addFriend(newFriend);
                        mCurrentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                int index = mUsers.indexOf(user);
                                mUsers.remove(index);
                                mFriends.add(newFriend);
                                notifyDataSetChanged();
                                mEventFriendsAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });

            }
        }
    }
}

