package com.project.foodflux;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ExpiryNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log to confirm the receiver is triggered
        Log.d("ExpiryNotificationReceiver", "Notification triggered");

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "food_flux_channel")
                .setSmallIcon(R.drawable.ic_notification) // Your icon resource
                .setContentTitle("Food Expiry Alert")
                .setContentText("Your food item is approaching expiry!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        // Create an Intent to open the app when the notification is clicked
        Intent appIntent = new Intent(context, AddFoodActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Get the NotificationManager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }
}
