package com.pusheenicorn.safetyapp;


import android.app.Activity;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    List<Event> mEvents;
    Context context;

    // constructor
    public EventAdapter(List<Event> events) {
        mEvents = events;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View eventView = inflater.inflate(R.layout.item_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Event event = mEvents.get(position);

        // populate the views according to this data
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
//
//        @Nullable
//        @BindView(R.id.ivImage)
//        ImageView ivImage;
//
//        @BindView(R.id.tvDescription) TextView tvDescription;
//        @BindView(R.id.tvUser) TextView tvUser;
//        @BindView(R.id.tvUser2) TextView tvUser2;

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