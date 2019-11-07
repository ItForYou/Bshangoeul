/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dreamforone.bshangoeul;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static int identity=0;
    public static MediaPlayer mediaPlayer;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        sendNotification(remoteMessage);
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     *
     */
    private void sendNotification(RemoteMessage remote) {

        String messageBody=remote.getData().get("message");
        String subject=remote.getData().get("subject");
        String goUrl=remote.getData().get("goUrl");
        String channelId = "bshangoeul";
        String gubun=remote.getData().get("gubun");
        String viewUrl=remote.getData().get("viewUrl");
        Log.d("remote","Message:"+remote.getFrom());


        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("goUrl",goUrl);
        Log.d("goUrl",goUrl);


        Log.d("remote",remote.toString());


        //팝업창 띄우기
        Intent popupIntent=new Intent(getApplicationContext(),PopupActivity.class);
        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        popupIntent.putExtra("url",viewUrl);
        popupIntent.putExtra("goUrl",goUrl);
        popupIntent.putExtra("gubun",gubun);
        Log.d("gubun1",gubun);
        if(gubun.equals("order")) {

            startActivity(popupIntent);
            MainActivity.repeat=true;
            mediaPlayer = MediaPlayer.create(this, R.raw.push);
            mHandler.sendEmptyMessage(0);

        }else{
            mediaPlayer = MediaPlayer.create(this, R.raw.order);
            mHandler.sendEmptyMessageDelayed(1,1000);
        }


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        PendingIntent pendingIntent1 =PendingIntent.getActivity(this, 0, popupIntent, PendingIntent.FLAG_UPDATE_CURRENT);




        Uri defaultSoundUri= Uri.parse("android.resource://"+getPackageName()+"/raw/order.mp3");



        Bitmap BigPictureStyle= BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        long vibrate[]={500,0,500,0};
        /**/
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,channelId)
                //new NotificationCompat.Builder(this, channelId,channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(subject)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));
        if(gubun.equals("order")){
            notificationBuilder.setContentIntent(pendingIntent1);
        }
        notificationBuilder.setContentIntent(pendingIntent);













        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(defaultSoundUri != null)
                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();

            Log.d("channel111","111"+defaultSoundUri.toString());
            channel.setSound(defaultSoundUri,audioAttributes);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());



    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("player","player");
            switch (msg.what){
                case 0:
                    if(MainActivity.repeat==true) {
                        mediaPlayer.start();
                        mHandler.sendEmptyMessageDelayed(0,5000);
                    }else{

                        // 초기화
                        if(mediaPlayer != null) {
                            mediaPlayer.release();
                            mediaPlayer = null;
                        }
                        MainActivity.repeat=false;
                    }
                    break;
                case 1:
                    mediaPlayer.start();
                    mHandler.sendEmptyMessageDelayed(2,2000);
                    break;
                case 2:

                    // 초기화
                    mediaPlayer.reset();
                    if(mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    mHandler.removeMessages(1);
                    mHandler.removeMessages(2);

                    break;
            }


        }
    };
}
