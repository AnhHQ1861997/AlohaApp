package com.englandstudio.aloha.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.englandstudio.aloha.ProfileActivity;
import com.englandstudio.aloha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FriendRequestService extends Service {

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id;

    //Reference
    DatabaseReference mRefUser, mRefAddFriend;

    //Notification
    private NotificationCompat.Builder builder;

    private static final int MY_NOTIFICATION_ID = 99999;

    private static final int MY_REQUEST_CODE = 100;

    public FriendRequestService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        anhXa();
        authentication();
        reference();
    }

    private void anhXa() {

    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefUser = FirebaseDatabase.getInstance().getReference().child("User");
        mRefAddFriend = FirebaseDatabase.getInstance().getReference().child("AddFriend");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        this.builder.setAutoCancel(true);

        final MediaPlayer mp = MediaPlayer.create(FriendRequestService.this, R.raw.ringtone);
        //Uri alarmSound = RingtoneManager.getDefaultUri(R.raw.ringtone);
        //builder.setSound(mp);

        builder.setSmallIcon(R.drawable.ic_launcher);
        this.builder.setTicker("Ticker");

        this.builder.setWhen(System.currentTimeMillis() + 10 * 1000);

        Intent messageIntent = new Intent(this, MessageService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, MY_REQUEST_CODE, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.builder.setContentIntent(pendingIntent);
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mRefAddFriend.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.exists()) {
                        String user_id = snap.getKey();
                        String firstName = snap.child("firstName").getValue().toString();
                        String lastName = snap.child("lastName").getValue().toString();
                        String type = snap.child("type").getValue().toString();
                        String received = snap.child("received").getValue().toString();

                        builder.setContentTitle(firstName + " " + lastName);
                        builder.setContentText("Bạn có 1 lời mời kết bạn");

                        String NOTIFICATION_CHANNEL_ID = user_id;
                        String NOTIFICATION_CHANNEL_NAME = "FriendRequestService";

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            int importance = NotificationManager.IMPORTANCE_LOW;
                            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance);
                            notificationChannel.enableLights(true);
                            notificationChannel.setLightColor(Color.RED);
                            notificationChannel.enableVibration(true);
                            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.createNotificationChannel(notificationChannel);
                            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
                        }

                        if (type.equals("received") && received.equals("false")) {
                            mp.start();
                            Intent messageIntent = new Intent(FriendRequestService.this, ProfileActivity.class);
                            messageIntent.putExtra("type", "guest");
                            messageIntent.putExtra("user_id", user_id);
                            PendingIntent resultPendingIntent =
                                    PendingIntent.getActivity(
                                            getApplicationContext(),
                                            0,
                                            messageIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );
                            // Set content intent;
                            builder.setContentIntent(resultPendingIntent);

                            Notification notification = builder.build();
                            manager.notify(MY_NOTIFICATION_ID, notification);
                            mRefAddFriend.child(current_id).child(user_id).child("received").setValue("true");
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
