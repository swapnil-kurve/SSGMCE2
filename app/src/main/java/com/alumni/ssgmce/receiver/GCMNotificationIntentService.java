package com.alumni.ssgmce.receiver;

/**
 * Created by Dell on 30-11-2015.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.alumni.ssgmce.R;
import com.alumni.ssgmce.activity.Activity_GroupPosts;
import com.alumni.ssgmce.activity.Activity_Groups;
import com.alumni.ssgmce.activity.Activity_Jobs;
import com.alumni.ssgmce.activity.Activity_LatestEvents;
import com.alumni.ssgmce.activity.Activity_Messages;
import com.alumni.ssgmce.classes.Constant;
import com.alumni.ssgmce.starts.Activity_Home;
import com.alumni.ssgmce.starts.Activity_Login;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMNotificationIntentService extends IntentService {
    // Sets an ID for the profile, so it can be updated
    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString(), "" + extras.get("AdminApproval"), "" + extras.get("title"), "" + extras.get("subtitle"),""+extras.get("NotificationType"), ""+extras.get("grpId"), ""+extras.get("isEventreg"));
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.toString(), "" + extras.get("AdminApproval"), "" + extras.get("title"), "" + extras.get("subtitle"),""+extras.get("NotificationType"), ""+extras.get("grpId"), ""+extras.get("isEventreg"));
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                sendNotification("" + extras.get("title"), "" + extras.get("AdminApproval"), "" + extras.get("title"), "" + extras.get("subtitle"),""+extras.get("NotificationType"), ""+extras.get("grpId"), ""+extras.get("isEventreg"));
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String message, String AdminApproval, String title, String subtitle, String notificationType, String grpID, String isEventreg) {

        SharedPreferences preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (AdminApproval.equalsIgnoreCase("1")) {
            editor.putString("AdminApproved", "true");
            Constant.mIsApproved = "true";
        } else if (AdminApproval.equalsIgnoreCase("0")){
            editor.putString("AdminApproved", "false");
            Constant.mIsApproved = "false";
        }
        editor.apply();

        preferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        String IsLoggedIn = preferences.getString("IsLoggedIn", "No");
        Intent intent;
        if (IsLoggedIn.equalsIgnoreCase("No")) {
            intent = new Intent(this, Activity_Login.class);
        } else if(notificationType.equalsIgnoreCase("Job")){
            intent = new Intent(this, Activity_Jobs.class);
        }else if(notificationType.equalsIgnoreCase("Event")){
            intent = new Intent(this, Activity_LatestEvents.class);
            editor = preferences.edit();
            editor.putString("isEventreg", isEventreg);
            editor.apply();

        }else if(notificationType.equalsIgnoreCase("Group")){
            intent = new Intent(this, Activity_GroupPosts.class);
            intent.putExtra("GroupId", grpID);
        }else if(notificationType.equalsIgnoreCase("Message")){
            intent = new Intent(this, Activity_Messages.class);
        }else
        {
            intent = new Intent(this, Activity_Home.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.logo)
                .setContentTitle(title)
                .setContentText(subtitle)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
