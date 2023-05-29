package com.example.amie;

import android.util.Log;

import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationActivity {
    // Constructor for SendNotification class
    public NotificationActivity(String message, String heading, String notificationKey, String extraField, String extraData){
        // Check if extraField and extraData are not null
        if(extraField != null && extraData != null) {
            try {
                // Create a JSON object with the provided message, heading, notificationKey, and extra data
                JSONObject notificationContent = new JSONObject(
                        "{'contents':{'en':'" + message + "'}," +
                                "'include_player_ids':['" + notificationKey + "']," +
                                "'headings':{'en': '" + heading + "'}," + "'data':{'" + extraField + "':'" + extraData + "'}}");
                // Send the notification using OneSignal API
                OneSignal.postNotification(notificationContent, null);
            } catch (JSONException e) {
                // Log the error if there is an issue creating the JSON object
                Log.d("error", e.toString());
                e.printStackTrace();
            }
        }
        else {
            try {
                // Create a JSON object with the provided message, heading, and notificationKey (without extra data)
                JSONObject notificationContent = new JSONObject(
                        "{'contents':{'en':'" + message + "'}," +
                                "'include_player_ids':['" + notificationKey + "']," +
                                "'headings':{'en': '" + heading + "'}}");
                // Send the notification using OneSignal API
                OneSignal.postNotification(notificationContent, null);
            } catch (JSONException e) {
                // Log the error if there is an issue creating the JSON object
                Log.d("error", e.toString());
                e.printStackTrace();
            }
        }
    }
}
