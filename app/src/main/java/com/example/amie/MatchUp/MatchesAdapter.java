package com.example.amie.MatchUp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amie.R;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders> {

    private List<MatchesObject> matchesList;
    private Context context;

    public MatchesAdapter(List<MatchesObject> matchesList, Context context) {
        this.matchesList = matchesList;
        this.context = context;
    }

    // This method is called when RecyclerView needs a new ViewHolder of the given type to represent an item
    @NonNull
    @Override
    public MatchesViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_matches layout and set the layout parameters
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        // Create a new MatchesViewHolders object and return it
        MatchesViewHolders  rcv = new MatchesViewHolders(layoutView);

        return rcv;
    }

    // This method is called by RecyclerView to display the data at the specified position
    @Override
    public void onBindViewHolder(@NonNull MatchesViewHolders holder, int position) {
        holder.mMatchId.setText(matchesList.get(position).getUserId());
        holder.mLocation.setText(matchesList.get(position).getLocation());
        holder.mUniversity.setText(matchesList.get(position).getUniversity());
        holder.mProfile.setText(matchesList.get(position).getProfileImg());
        holder.mInterest.setText(matchesList.get(position).getInterest());
        holder.mMatchName.setText(matchesList.get(position).getName());
        holder.mLastMessage.setText(matchesList.get(position).getLastMessage());

        // Check if last seen is "true" and set the visibility of the notification dot accordingly
        String lastSeen = "";
        lastSeen = matchesList.get(position).getLastSeen();
        if (lastSeen.equals("true"))
            holder.mNotificationDot.setVisibility(View.VISIBLE);
        else
            holder.mNotificationDot.setVisibility(View.INVISIBLE);
        // Set last timestamp data
        holder.mLastTimeStamp.setText(matchesList.get(position).getLastTimeStamp());
        // Load the profile image if it's not the default image
        if (!matchesList.get(position).getProfileImg().equals("default")) {
            Glide.with(context).load(matchesList.get(position).getProfileImg()).into(holder.mMatchImage);
        }
    }

    // Return the total number of items in the data set held by the adapter
    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}

