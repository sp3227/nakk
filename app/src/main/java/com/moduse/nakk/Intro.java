package com.moduse.nakk;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

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
import java.util.Vector;

public class Intro extends AppCompatActivity {

    AppInfo appInfo;

    final int APPUPDATE = 1;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        appInfo = new AppInfo();

        //다이얼로그 설정
        loading = new ProgressDialog(Intro.this);
        InitShow();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("낚중일기 업데이트") // 제목 설정
                        .setMessage("최신 버전의 업데이트가 필요 합니다.")  // 중앙 메세시 설정
                        .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                        .setPositiveButton("업데이트", new DialogInterface.OnClickListener() {
                            // 예 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                Intent intent =  new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.dwonload_url)));

                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("종료", new DialogInterface.OnClickListener() {
                            // 아니요 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                Intro.this.finish();
                            }
                        });

                AlertDialog alert = builder.create();  //알림 객체 생성
                return alert;
        }
        return null;
    }

}
