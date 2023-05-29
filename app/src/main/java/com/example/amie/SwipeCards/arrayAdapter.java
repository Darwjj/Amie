package com.example.amie.SwipeCards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.amie.R;

import java.util.List;

// Define the "arrayAdapter" class, which extends the ArrayAdapter class and is used to display a list of "cards" objects.
public class arrayAdapter extends ArrayAdapter<cards> {

    Context context;

    // Constructor that initializes the "arrayAdapter" object with the given parameters.
    public arrayAdapter(Context context, int resourceId, List<cards> items) {
        super(context, resourceId, items);
    }

    // Override the "getView" method to customize the display of "cards" objects in the list.
    public View getView(int position, View convertView, ViewGroup parent) {
        cards card_item = getItem(position);

        // If the convertView is null, inflate a new view from the "item" layout.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        // If the "Interest" field of the "cards" object is not null, customize the display of the object in the list.
        if(card_item.getInterest()!=null) {

            // Get references to the TextView and ImageView elements in the convertView.
            TextView name = (TextView) convertView.findViewById(R.id.name);
            ImageView image = (ImageView) convertView.findViewById(R.id.image);
            TextView location = (TextView) convertView.findViewById(R.id.budget);
            ImageView mInterestImage = (ImageView) convertView.findViewById(R.id.needImage);
            ImageView mUniversityImage = (ImageView) convertView.findViewById(R.id.giveImage);

            // Set the name and location text in the convertView.
            name.setText(card_item.getName());
            location.setText(card_item.getLocation());
            //Interest Image
            if (card_item.getInterest().equals("Art"))
                mInterestImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.art));
            else if (card_item.getInterest().equals("Travel"))
                mInterestImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.travel));
            else if (card_item.getInterest().equals("Books"))
                mInterestImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.books));
            else if (card_item.getInterest().equals("Films"))
                mInterestImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.film));
            else if (card_item.getInterest().equals("Science"))
                mInterestImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.science));
            else
                mInterestImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.none));


            // University Image
            if (card_item.getUniversity().equals("De Montfort University"))
                mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dmu));
            else if (card_item.getUniversity().equals("University of Leicester"))
                mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.uol));
            else if (card_item.getUniversity().equals("University of Birmingham"))
                mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.birmingham));
            else if (card_item.getUniversity().equals("Coventry University"))
                mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.coventry));
            else if (card_item.getUniversity().equals("University of Nottingham"))
                mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.universityofnottingham));
            else if (card_item.getUniversity().equals("University of Manchester"))
                mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.manchester));
            else
                mUniversityImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.none));

            switch (card_item.getProfileImg()) {
                case "default":
                    Glide.with(convertView.getContext()).load(R.drawable.suelo).into(image);
                    break;
                default:
                    Glide.clear(image);
                    Glide.with(convertView.getContext()).load(card_item.getProfileImg()).into(image);
                    break;
            }
        }
        // Return the customized convertView.
        return convertView;
    }

}
