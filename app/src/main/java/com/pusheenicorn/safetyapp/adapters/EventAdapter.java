package com.pusheenicorn.safetyapp.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.activities.EventsActivity;
import com.pusheenicorn.safetyapp.models.Event;
import com.pusheenicorn.safetyapp.utils.CalendarUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    List<Event> mEvents;
    Context context;
    public static final String START = "STARTS: ";
    public static final String END = "       ENDS: ";
    CalendarUtil calendarUtil;

    /**
     * This is a constructor for the class
     * @param events- the events to link this adapter to
     */
    public EventAdapter(List<Event> events) {
        mEvents = events;
    }

    /**
     * This function is executed on creation. It initializes the fields and returns
     * a viewholder for the items.
     *
     * @param parent- the layout parent
     * @param viewType- the type of view
     * @return a viewholder
     */
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

    /**
     * Set the start and end times for the event, in addition to the location and name.
     *
     * @param holder- the holder for the current item
     * @param position- the position of the current item
     */
    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Event event = mEvents.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvEventLocation.setText(event.getLocation());

        String time = START + calendarUtil.getRelativeTimeAgo(event.getStart()) + END
                + calendarUtil.getRelativeTimeAgo(event.getEnd());
        if (time == null)
        {
            holder.tvTime.setText(event.getStart() + " to " + event.getEnd());
        }
        else {
            holder.tvTime.setText(time);
        }
    }

    /**
     * This function returns the length of the recycler view's list.
     *
     * @return the length of the recycler view's list
     */
    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    /**
     * This function clears the recycler view.
     */
    public void clear() {
        mEvents.clear();
        notifyDataSetChanged();
    }

    /**
     * This is a new view holder class.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvEventName) TextView tvEventName;
        @BindView(R.id.tvEventLocation) TextView tvEventLocation;
        @BindView(R.id.tvStartTime) TextView tvTime;

        /**
         * This constructor takes an item an intializes a view holder for it.
         * @param itemView
         */
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

        /**
         * A function that executes the item on click to go to the
         * event detail page for that event item.
         * @param v- the view that was clicked on
         */
        @Override
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