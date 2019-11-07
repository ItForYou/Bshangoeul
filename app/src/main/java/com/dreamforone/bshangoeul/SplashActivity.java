package com.dreamforone.bshangoeul;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


/*
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;*/

public class SplashActivity extends AppCompatActivity {
    private static final int APP_PERMISSION_STORAGE = 9787;
    private final int APPS_PERMISSIONREQUEST=1000;
    final int SEC=1000;//다음 화면에 넘어가기 전에 머물 수 있는 시간(초)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //버전 체킹
        VersionCheck versionCheck=new VersionCheck(getPackageName().toString(),this);
        versionCheck.execute();



    }
    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission(){
        try {

            //권한이 없는 경우
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED )
            {
                //최초 거부를 선택하면 두번째부터 이벤트 발생 & 권한 획득이 필요한 이유를 설명
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                }

                //요청 팝업 팝업 선택시 onRequestPermissionsResult 이동
                requestPermissions(new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.RECORD_AUDIO
                        },
                        APP_PERMISSION_STORAGE);

            }
            //권한이 있는 경우
            else {
                goHandler();

                //writeFile();
            }
        }catch(Exception e){
            goHandler();
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSION_STORAGE:
                if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED) {
                    goHandler();
                }else{

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivityForResult(intent,APPS_PERMISSIONREQUEST);
                    //startActivity(intent);

                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==APPS_PERMISSIONREQUEST){
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermission();
                } else {
                    goHandler();
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //핸들러로 이용해서 3초간 머물고 이동이 됨
    public void goHandler() {
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();

            }
        }, SEC);
    }

    class VersionCheck extends AsyncTask<Void,Void,String> {
        String pakage;
        public String marketVersion;
        Context mContext;

        public VersionCheck(String pakage,Context context){
            this.pakage=pakage;
            mContext=context;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        //플레이스토어 파싱하기
        @Override
        protected String doInBackground(Void... params) {

            try {
                Document doc =
                        Jsoup.connect("https://play.google.com/store/apps/details?id=" + pakage).get();
                Elements Version = doc.select(".htlgb").eq(7);
                for (Element mElement : Version) {
                    return mElement.text().trim();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }




            return null;
        }
        //버전이 맞지 않으면 AlertDialog창 띄우기
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            marketVersion=result;

            try {
                String versionName = getApplicationContext().getPackageManager()
                        .getPackageInfo(getApplicationContext().getPackageName(), 0)
                        .versionName;
                Log.d("versionName",versionName);
                Log.d("versionName",result);
                if(!marketVersion.equals(versionName)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                    builder.setTitle("버전체크");
                    builder.setMessage("버전업데이트가 되었습니다. 업데이트 후에 이용이 가능합니다.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));

                                    finishAffinity();
                                    System.runFinalization();
                                    System.exit(0);
                                }
                            });

                    builder.show();

                }else{
                    //버전별 체크를 한 후 마시멜로 이상이면 퍼미션 체크 여부
                    Log.d("check","check");
                    try {
                        if (Build.VERSION.SDK_INT >= 23) {
                            checkPermission();
                        } else {
                            goHandler();
                        }
                    } catch (Exception e) {
                    }
                }
                return;
            } catch (PackageManager.NameNotFoundException e) {

            }
        }






    }
}
