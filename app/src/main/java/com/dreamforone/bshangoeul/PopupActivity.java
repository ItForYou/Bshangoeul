package com.dreamforone.bshangoeul;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.webkit.WebView;

public class PopupActivity extends Activity {
    String url,goUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        url=getIntent().getExtras().getString("url").toString();
        goUrl=getIntent().getExtras().getString("goUrl").toString();
        String gubun=getIntent().getExtras().getString("gubun").toString();

        View dialogView = getLayoutInflater().inflate(R.layout.popup_dialog, null);
        WebView popupWebView=(WebView)dialogView.findViewById(R.id.popupWebView);
        popupWebView.loadUrl(url);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.repeat = false;
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("goUrl",goUrl);

                startActivity(intent);

                finish();
            }
        }).setView(dialogView);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Uri defaultSoundUri= Uri.parse("android.resource:com.dreamforone.bshangoeul/raw/push.mp3");
        //주문시 사운드 변경하기
        if(gubun.equals("order")){
            defaultSoundUri=Uri.parse("android.resource:com.dreamforone.bshangoeul/raw/order.mp3");
        }
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), defaultSoundUri);
        r.play();








    }


}
