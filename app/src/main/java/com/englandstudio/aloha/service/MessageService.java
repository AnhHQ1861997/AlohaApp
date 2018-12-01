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

import com.englandstudio.aloha.MessageActivity;
import com.englandstudio.aloha.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MessageService extends Service {

    //Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String current_id, user_id;

    //Reference
    DatabaseReference mRefLastMessage;

    //Notification
    private NotificationCompat.Builder builder;

    private static final int MY_NOTIFICATION_ID = 12345;

    private static final int MY_REQUEST_CODE = 100;

    public MessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        authentication();
        reference();
        noti();

    }

    private void authentication() {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        current_id = mUser.getUid();
    }

    private void reference() {
        mRefLastMessage = FirebaseDatabase.getInstance().getReference().child("LastMessage");
    }

    private void noti() {


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
        this.builder.setAutoCancel(true);

        final MediaPlayer mp = MediaPlayer.create(MessageService.this, R.raw.ringtone);
        //Uri alarmSound = RingtoneManager.getDefaultUri(R.raw.ringtone);
        //builder.setSound(mp);

        builder.setSmallIcon(R.drawable.ic_launcher);
        this.builder.setTicker("Ticker");

        this.builder.setWhen(System.currentTimeMillis() + 10 * 1000);

        Intent messageIntent = new Intent(this, MessageService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, MY_REQUEST_CODE, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        this.builder.setContentIntent(pendingIntent);
        final NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mRefLastMessage.child(current_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (snap.exists()) {
                        String user_id = snap.getKey();
                        String avatar = snap.child("avatar").getValue().toString();
                        String firstName = snap.child("firstName").getValue().toString();
                        String lastName = snap.child("lastName").getValue().toString();
                        String message = snap.child("message").getValue().toString();
                        String service_id = snap.child("serviceid").getValue().toString();
                        String received = snap.child("received").getValue().toString();

                        builder.setContentTitle(firstName + " " + lastName);
                        builder.setContentText(message);

                        String NOTIFICATION_CHANNEL_ID = user_id;
                        String NOTIFICATION_CHANNEL_NAME = "MessageService";

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

                        if (!service_id.equals(current_id) && received.equals("false")) {
                            mp.start();
                            Intent messageIntent = new Intent(MessageService.this, MessageActivity.class);
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
                            mRefLastMessage.child(current_id).child(user_id).child("received").setValue("true");
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
