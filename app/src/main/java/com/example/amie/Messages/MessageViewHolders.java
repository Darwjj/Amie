package com.example.amie.Messages;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amie.R;

public class MessageViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView mMessage;
    public LinearLayout mContainer;

    // Constructor that takes in a view and sets the itemView's onClickListener
    public MessageViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        // Finding the TextView and LinearLayout elements in the itemView
        mMessage = itemView.findViewById(R.id.message);
        mContainer = itemView.findViewById(R.id.container);
    }

    // Overriding the onClick method from the View.OnClickListener interface
    @Override
    public void onClick(View v) {

    }
}

