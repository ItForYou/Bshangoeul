package com.dreamforone.bshangoeul;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class RepeatBroadcastReceiver extends BroadcastReceiver {
    MediaPlayer mediaPlayer;
    @Override
    public void onReceive(Context context, Intent intent) {
        mediaPlayer = MediaPlayer.create(context, R.raw.order);
        mHandler.sendEmptyMessage(0);
    }
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("player","player");
            if(MainActivity.repeat==true) {
                mediaPlayer.start();
            }else{
                // 정지버튼
                mediaPlayer.stop();
                // 초기화
                mediaPlayer.reset();
                if(mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                MainActivity.repeat=false;
            }
            mHandler.sendEmptyMessageDelayed(0,5000);
        }
    };

}
