package com.example.android.quakereport;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class QuakeAdapter extends ArrayAdapter<Quake> {

    private final String LOCATION_SEPARATOR = "of";

    public QuakeAdapter(Activity context, ArrayList<Quake> pQuakes) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, pQuakes);
    }

     /* Provides a view for an AdapterView (ListView, GridView, etc.)
     *
             * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
            * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);



        }

            Quake currentQuake = getItem(position);



            TextView magText = listItemView.findViewById(R.id.magText);
            // Set the proper background color on the magnitude circle.
            // Fetch the background from the TextView, which is a GradientDrawable.
             GradientDrawable magnitudeCircle = (GradientDrawable) magText.getBackground();

             // Get the appropriate background color based on the current earthquake magnitude
             // look below for the helper method getMagnitudeColor()
             int magnitudeColor = getMagnitudeColor(currentQuake.getMag());

            // Set the color on the magnitude circle
            magnitudeCircle.setColor(magnitudeColor);


            DecimalFormat formatter = new DecimalFormat("0.0");
            Double displayMag = Double.parseDouble(formatter.format(currentQuake.getMag()));
            magText.setText(displayMag.toString());

            TextView primaryLocationText = listItemView.findViewById(R.id.primaryLocationText);
            TextView locationOffsetText = listItemView.findViewById(R.id.locationOffsetText);
            String raw_location = currentQuake.getLocation();
            String primary_location, location_offset;
            //Check if the text contains a specific offset, else use "near the"
        //split into 2 parts, even if there is more than one "of" in the name
            if (raw_location.contains(LOCATION_SEPARATOR)) {
               String[] parts =  raw_location.split(LOCATION_SEPARATOR, 2);
                location_offset = parts[0] + " " + LOCATION_SEPARATOR;
               primary_location = parts[1];
            } else
                {
                    location_offset = "Near the ";
                    primary_location = raw_location;
            }
                primaryLocationText.setText(primary_location);
                locationOffsetText.setText(location_offset);


            TextView dateText = listItemView.findViewById(R.id.dateText);
            SimpleDateFormat sdfDate = new SimpleDateFormat("LLL dd, yyyy");
            String sDate = sdfDate.format(currentQuake.getTimeInMilliseconds()).toString();
            dateText.setText(sDate);

            TextView timeText = listItemView.findViewById(R.id.timeText);
            SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
            String sTime = sdfTime.format(currentQuake.getTimeInMilliseconds());
            timeText.setText(sTime);

        return listItemView;




    }

    private int getMagnitudeColor(double Mag){

        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(Mag);

        switch (magnitudeFloor){
        case 0:
        case 1:
        magnitudeColorResourceId = R.color.magnitude1;
        break;
        case 2:
        magnitudeColorResourceId = R.color.magnitude2;
        break;
        case 3:
        magnitudeColorResourceId = R.color.magnitude3;
        break;
        case 4:
        magnitudeColorResourceId = R.color.magnitude4;
        break;
        case 5:
        magnitudeColorResourceId = R.color.magnitude5;
        break;
        case 6:
        magnitudeColorResourceId = R.color.magnitude6;
        break;
        case 7:
        magnitudeColorResourceId = R.color.magnitude7;
        break;
        case 8:
        magnitudeColorResourceId = R.color.magnitude8;
        break;
        case 9:
        magnitudeColorResourceId = R.color.magnitude9;
        break;
        default:
        magnitudeColorResourceId = R.color.magnitude10plus;
        break;
    }
    return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
        //Once we find the right color resource ID, we still have one more step to convert it into an actual color value.
        // Remember that color resource IDs just point to the resource we defined, but not the value of the color.
        // For example, R.layout.earthquake_list_item is a reference to tell us where the layout is located.
        // It’s just a number, not the full XML layout.

}

















    }