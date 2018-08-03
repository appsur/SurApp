package com.pusheenicorn.safetyapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseUser;
import com.pusheenicorn.safetyapp.models.Checkin;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.models.User;
import com.pusheenicorn.safetyapp.utils.CalendarUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.pusheenicorn.safetyapp.MainActivity.TWITTER_FORMAT;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    List<Event> mEvents;
    Context context;
    public static final String TWITTER_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
    CalendarUtil calendarUtil;

    // constructor
    public EventAdapter(List<Event> events) {
        mEvents = events;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        calendarUtil = new CalendarUtil();
        LayoutInflater inflater = LayoutInflater.from(context);
        View eventView = inflater.inflate(R.layout.item_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Event event = mEvents.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvEventLocation.setText(event.getLocation());

        String time = "STARTS: " + calendarUtil.getRelativeTimeAgo(event.getStart()) + "       ENDS: "
                + calendarUtil.getRelativeTimeAgo(event.getEnd());
        if (time == null)
        {
            holder.tvTime.setText(event.getStart() + " to " + event.getEnd());
        }
        else {
            holder.tvTime.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mEvents.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Event> list) {
        mEvents.addAll(list);
        notifyDataSetChanged();
    }
    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvEventName) TextView tvEventName;
        @BindView(R.id.tvEventLocation) TextView tvEventLocation;
        @BindView(R.id.tvStartTime) TextView tvTime;

        public ViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            int position = getAdapterPosition();
            // make sure the position is valid, i.e. actually exists in the view
            if (position != RecyclerView.NO_POSITION) {
                // get the tweet at the position, this won't work if the class is static
                Event post = mEvents.get(position);
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
                Event event = mEvents.get(position);
                // create intent for the new activity
                Intent intent = new Intent(context, EventsActivity.class);
                intent.putExtra("event", event);
                // show the activity
                context.startActivity(intent);
            }
        }
    }
}