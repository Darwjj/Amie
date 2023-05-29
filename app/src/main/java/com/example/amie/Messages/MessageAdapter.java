package com.example.amie.Messages;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amie.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolders> {

    private List<MessageObject> chatList;
    private Context context;

    public MessageAdapter(List<MessageObject> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item_chat layout
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);//later we create item_chat
        // Set the layout parameters for the inflated view
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        // Create a new ChatViewHolders object and return it
        MessageViewHolders rcv = new MessageViewHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolders holder, int position) {
        // Set the message text
        holder.mMessage.setText(chatList.get(position).getMessage());

        // Set the message text
        if (chatList.get(position).getCurrentUser()) {
            // Create a GradientDrawable object for the message background
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(20);
            shape.setCornerRadii(new float[] {25, 25, 3, 25, 25, 25, 25, 25});
            shape.setColor(Color.parseColor("#5fc9f8"));

            // Set the gravity to end for the message container
            holder.mContainer.setGravity(Gravity.END);

            // Set the message background and text color
            holder.mMessage.setBackground(shape);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else {
            // Create a GradientDrawable object for the message background
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(20);
            shape.setCornerRadii(new float[] {25, 25, 3, 25, 25, 25, 25, 25});
            shape.setColor(Color.parseColor("#53d769"));

            // Set the gravity to start for the message container
            holder.mContainer.setGravity(Gravity.START);

            // Set the message background and text color
            holder.mMessage.setBackground(shape);
            holder.mMessage.setTextColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    // Return the number of chat messages
    public int getItemCount() {
        return this.chatList.size();
    }
}
