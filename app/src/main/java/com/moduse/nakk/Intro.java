package com.moduse.nakk;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Vector;

public class Intro extends AppCompatActivity {

    AppInfo appInfo;

    final int APPUPDATE = 1;
    final int AVD = 2;
    ProgressDialog loading;

    Boolean isvm_blue = false;
    Boolean isvm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        appInfo = new AppInfo();

        //다이얼로그 설정
        loading = new ProgressDialog(Intro.this);
        InitShow();

        if (isvm() || isvm_bluestick())
        {
            showDialog(AVD);
        }
        else
        {
            startlogin();
        }


    }

    // 블루스택 막기 실행중인 프로세스 검사
    public boolean isvm_bluestick()
    {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        List<ActivityManager.RunningServiceInfo> listservice = am.getRunningServices(Integer.MAX_VALUE);

        for(int i = 0; i < listservice.size(); i++)
        {
            //실행중인 서비스 이름(패키지명)
           // info1+=(listservice.get(i).process)+"\n";

            if(listservice.get(i).process.toString().equals("com.bluestacks.home")
                    || listservice.get(i).process.toString().equals("com.bluestacks.s2p")
                    || listservice.get(i).process.toString().equals("com.bluestacks.bstfolder")
                    || listservice.get(i).process.toString().equals("nullcom.bluestacks.bstfolder"))
            {
                isvm_blue = true;
            }
        }

       // info1+="======================================\n";

        return isvm_blue;
    }


    // 지니모션, 녹스, 윈드로이 막기 (가상디바이스면 true)
    public boolean isvm()
    {

        StringBuilder deviceInfo = new StringBuilder();
        deviceInfo.append("Build.PRODUCT " + Build.PRODUCT +"\n");
        deviceInfo.append("Build.FINGERPRINT " +Build.FINGERPRINT+"\n");
        deviceInfo.append("Build.MANUFACTURER " +Build.MANUFACTURER+"\n");
        deviceInfo.append("Build.MODEL " +Build.MODEL+"\n");
        deviceInfo.append("Build.BRAND " +Build.BRAND+"\n");
        deviceInfo.append("Build.DEVICE " +Build.DEVICE+"\n");
        //info2 = deviceInfo.toString();

        if(
                "google_sdk".equals(Build.PRODUCT) ||
                        "sdk_google_phone_x86".equals(Build.PRODUCT) ||
                        "sdk".equals(Build.PRODUCT) ||
                        "sdk_x86".equals(Build.PRODUCT) ||
                        "vbox86p".equals(Build.PRODUCT) ||
                        Build.FINGERPRINT.contains("generic") ||
                        Build.MANUFACTURER.contains("Genymotion") ||
                        Build.MANUFACTURER.contains("Windroy") ||
                        Build.MODEL.contains("Emulator") ||
                        Build.MODEL.contains("nox") ||
                        Build.MODEL.contains("Andy") ||
                        Build.MODEL.contains("Droid4X") ||
                        Build.MODEL.contains("Android SDK built for x86") ||
                        Build.TAGS.contains("test-keys") ||
                        Build.TYPE.contains("userdebug") ||
                        Build.DEVICE.contains("nox") ||
                        Build.DEVICE.contains("ttVM_Hdragon")  ||
                        Build.DEVICE.contains("vbox86p")
                ){
            isvm =  true;
        }

        if(Build.BRAND.contains("generic")&&Build.DEVICE.contains("generic")){
            isvm =  true;
        }
        return isvm;
    }


    public void startlogin()
    {
        Handler hd = new Handler();
        hd.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                accreditationToDatabase(appInfo.Get_AppVer(),appInfo.Get_CertificationKey(),appInfo.Get_CertificationURL());
                //Intent intent = new Intent(Intro.this, Login.class);
                // startActivity(intent);
            }
        }, 2000);
    }

    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("정보를 불러오는 중입니다..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}



    private void accreditationToDatabase(String ver, String key, String url)
    {
        class phpdown extends AsyncTask<String, Integer, String>
        {

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                StartShow();  // 다이얼로그 시작
            }

            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);

                StopShow();   // 다이얼로그 종료

                if (result.toString().equals("SUCCESS"))
                {
                    Intent intent = new Intent(Intro.this, Login.class);
                    startActivity(intent);
                    finish();

                }
                else
                {
                    showDialog(APPUPDATE);
                }

            }

            @Override
            protected String doInBackground(String... params)
            {
                boolean state = false;

                // TODO Auto-generated method stub
                try
                {
                    String url = (String)params[2];   // url 설정
                    HttpPost request = new HttpPost(url);
                    Vector<NameValuePair> list = new Vector<NameValuePair>();
                    //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.

                    String ver_ = (String) params[0];
                    String key_ = (String) params[1];


                    list.add(new BasicNameValuePair("ver", ver_));
                    list.add(new BasicNameValuePair("key", key_));
                    HttpEntity resEntity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
                    request.setEntity(resEntity);


                    HttpClient client = new DefaultHttpClient();
                    HttpResponse res = client.execute(request);

                    //웹서버에서 값 받기
                    HttpEntity entityResponse = res.getEntity();

                    InputStream im = entityResponse.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(im, HTTP.UTF_8));

                    String total = "";
                    String tmp = "";
                    //한줄 한줄 읽기
                    while ((tmp = reader.readLine()) != null)
                    {
                        if (tmp != null)
                        {
                            total += tmp;
                        }
                    }
                    im.close();

//이곳에서 UI를 변경하면 에러

                    return total;
                }
                catch (UnsupportedEncodingException e)
                {
                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch (IOException e)
                {

                }
                return null;     //오류시 null

            }
        }

        phpdown task = new phpdown();  // 함수 쓰레드 설정
        task.execute(ver,key,url);  // 함수 쓰레드 시작

    }



// 업데이트 다이얼 로그

    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch (id)
        {
            case APPUPDATE:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("낚중일기 업데이트") // 제목 설정
                        .setMessage("최신 버전의 업데이트가 필요 합니다.")  // 중앙 메세시 설정
                        .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                        .setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                            // 예 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.dwonload_url)));

                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            // 아니요 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intro.this.finish();
                            }
                        });

                AlertDialog alert = builder.create();  //알림 객체 생성
                return alert;
            }
            case AVD:
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("혹시...") // 제목 설정
                        .setMessage("에뮬레이터에서 실행하십니까?")  // 중앙 메세시 설정
                        .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            // 예 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                Toast.makeText(getApplicationContext(),"죄송합니다. 에뮬레이터는 승인 안됩니다.",Toast.LENGTH_SHORT);
                                Intro.this.finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            // 아니요 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                Toast.makeText(getApplicationContext(),"거짓말 하지마세요.",Toast.LENGTH_SHORT);
                                Intro.this.finish();
                            }
                        });

                AlertDialog alert = builder.create();  //알림 객체 생성
                return alert;
            }
        }
        return null;
    }

}
