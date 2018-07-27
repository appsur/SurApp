package com.pusheenicorn.safetyapp.adapters;

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
import com.pusheenicorn.safetyapp.R;
import com.pusheenicorn.safetyapp.models.Alert;
import com.pusheenicorn.safetyapp.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlertAdapter extends RecyclerView.Adapter<com.pusheenicorn.safetyapp.adapters.AlertAdapter.ViewHolder> {
    List<Alert> mAlerts;
    Context context;

    // constructor
    public AlertAdapter(List<Alert> alerts) {
        mAlerts = alerts;
    }

    @NonNull
    @Override
    public com.pusheenicorn.safetyapp.adapters.AlertAdapter.ViewHolder onCreateViewHolder
            (@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertView = inflater.inflate(R.layout.item_event_user, parent, false);
        com.pusheenicorn.safetyapp.adapters.AlertAdapter.ViewHolder viewHolder =
                new com.pusheenicorn.safetyapp.adapters.AlertAdapter.ViewHolder(alertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull com.pusheenicorn.safetyapp.adapters.
            AlertAdapter.ViewHolder holder, int position) {
        // get the data according to position
        Alert alert = mAlerts.get(position);

        // alerts
    }

    @Override
    public int getItemCount() {
        return mAlerts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mAlerts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Alert> list) {
        mAlerts.addAll(list);
        notifyDataSetChanged();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {

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
                Alert alert = mAlerts.get(position);
            }
        }
    }
}


