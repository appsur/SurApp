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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.Friend;
import com.pusheenicorn.safetyapp.models.User;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private List<Friend> mFriends;
    Context context;

    //pass in the Tweets array in the constructor
    public FriendsAdapter(List<Friend> friends) {
        mFriends = friends;
    }

    //for each row, inflate the layout and cache references (only invoked if creating a new row)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View friendView = inflater.inflate(R.layout.item_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(friendView);
        return viewHolder;
    }

    public void clear() {
        mFriends.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Friend> list) {
        mFriends.addAll(list);
        notifyDataSetChanged();
    }
    //bind the values based on the position of the element

    @Override
    public void onBindViewHolder(@NonNull final FriendsAdapter.ViewHolder holder, int position) {
        //get the data according to position
        Friend friend = mFriends.get(position);
        // Populate the item in the recycler view
        holder.tvName.setText(friend.getUser().getUsername());
        String number = friend.getUser().getPhonNumber();
        number = "(" + number.substring(0, 3) + ") " +
                number.substring(3, 6) + "-" +
                number.substring(6, 10);
        holder.tvPhoneNumber.setText(number);

        // Get the friend's last checkin id
        Toast.makeText(context, friend.getUser().getObjectId(), Toast.LENGTH_LONG).show();

        final User friendUser = friend.getUser();
        String checkinId = friendUser.getLastCheckin().getObjectId();

        // Query by checkinId
        final Checkin.Query checkinQuery = new Checkin.Query();
        checkinQuery.getTop().whereEqualTo("objectId", checkinId);
        checkinQuery.findInBackground(new FindCallback<Checkin>() {
            @Override
            public void done(List<Checkin> objects, com.parse.ParseException e) {
                if (e == null) {
                    // Get the checkin object and format its date
                    final Checkin checkin = objects.get(0);
                    Date date = checkin.getCreatedAt();
                    String remaining = timeUntilCheckin(date, friendUser) + "";
                    holder.tvTime.setText(remaining + " minutes");
                } else {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(context).load(friendUser.getProfileImage().getUrl()).into(holder.ivProfile);
    }

    //get item count
    public int getItemCount() {
        return mFriends.size();
    }

    //create the ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvPhoneNumber) TextView tvPhoneNumber;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvTime) TextView tvTime;
        @BindView(R.id.ivProfileImage) ImageView ivProfile;
        ParseUser user;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            user = ParseUser.getCurrentUser();
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v){
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION){
                Friend friend = mFriends.get(position);
                Intent i = new Intent(context, MapActivity.class);
                i.putExtra(Friend.class.getSimpleName(), Parcels.wrap(friend));

                //show activity
                context.startActivity(i);
            }
        }
    }

    public int timeUntilCheckin (Date prevDate, User user) {
        // Define format type.
        DateFormat df = new SimpleDateFormat("MM/dd/yy/HH/mm");

        // Get current Date.
        Date currDate = new Date();

        // Split by regex "/" convert to int array and find time difference.
        String[] currDateArr = df.format(currDate).split("/");
        String[] prevDateArr = df.format(prevDate).split("/");
        int[] currDateInts = new int[5];
        int[] prevDateInts = new int[5];
        for (int i = 0; i < 5; i++) {
            currDateInts[i] = Integer.parseInt(currDateArr[i]);
            prevDateInts[i] = Integer.parseInt(prevDateArr[i]);
        }

        // true curr - truePrev is the number of minutes ago that the user checked in
        int trueCurr = (currDateInts[0] * 43800) + (currDateInts[1] * 1440)
                + (currDateInts[2] * 525600) + (currDateInts[3] * 60) + currDateInts[4];
        int truePrev = (prevDateInts[0] * 43800) + (prevDateInts[1] * 1440)
                + (prevDateInts[2] * 525600) + (prevDateInts[3] * 60) + prevDateInts[4];
        // threshold is the length of a user's checkin cycle
        int threshold = (int) user.getNumber("checkin");

        if (trueCurr - truePrev > threshold) {
            return 0;
        } else {
            return threshold - (trueCurr - truePrev);
        }
    }
}
