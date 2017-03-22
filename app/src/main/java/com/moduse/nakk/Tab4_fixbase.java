package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sejung on 2017-02-27.
 */

public class Tab4_fixbase extends Activity
{
    AppInfo appInfo;
    phpdown task;

    ProgressDialog loading;
    CheckBox push_box;
    boolean push_value;

    //푸시 저장부분 로컬
    SharedPreferences setting;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab4_fixbase);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);

        push_box = (CheckBox) findViewById(R.id.tab4_fixbase_push);

        PushSwitch();

    }

    // 푸시 스위치
    public void PushSwitch()
    {
        try {
            setting = getSharedPreferences("setting", 0);  // 로컬 세팅값 불러오기 & 저장하기
            editor= setting.edit();

            push_value = AppInfo.Push_state;

            push_box.setChecked(push_value);

            push_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    if(isChecked)
                    {
                        push_value = true;
                        editor.putBoolean("PUSH_STATE", push_value);
                        AppInfo.Push_state = true;
                    }
                    else
                    {
                        push_value = false;
                        editor.putBoolean("PUSH_STATE", push_value);
                        AppInfo.Push_state = false;
                    }
                    editor.commit();
                }
            });
        }
       catch (Exception e)
       {

       }
    }

    // 탈퇴하기
    public void tab4_fixbase_signout(View v)
    {
        // 마지막에 작업하기
        // 계정, 토크, 멘트, 이미지, 포인트 삭제


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("[회원 탈퇴]") // 제목 설정
                .setMessage("저장한 모든 데이터가 삭제됩니다.\n(이후 복구 불가능)\n탈퇴 하시겠습니까?")
                .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    // 예 버튼 클릭시 설정
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        PHPsignout();

                       // finish();
                    }
                })
                .setNegativeButton("아니요", null).show();

        AlertDialog alert = builder.create();  //알림 객체 생성
    }


    // 이용약관 보기 1
    public void tab4_fixbase_agee1(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(),Ageeinfo.class);
        intent.putExtra("ageetype","agee1");

        startActivity(intent);
    }

    // 이용약관 보기 1
    public void tab4_fixbase_agee2(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(),Ageeinfo.class);
        intent.putExtra("ageetype","agee2");

        startActivity(intent);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////// 탈퇴하기 함수
    public void PHPsignout()
    {
        StopShow();
        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("LOGIN_ID", AppInfo.MY_LOGINID));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab4_signoutURL());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }


    /////////////////////////////////////////////////////////////////////////////// 통신 부분 ////////////////////////////////////////////////////////////////////////
    private class phpdown extends AsyncTask<HttpPost, Integer, String>
    {

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //  loading = ProgressDialog.show(Intro_app.this, "버전 체크중입니다.", null, true, true);
            Log.i("SIGNresult","true");
        }

        @Override
        protected String doInBackground(HttpPost... urls) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = new HttpResponse() {
                @Override
                public StatusLine getStatusLine() {
                    return null;
                }
                @Override
                public void setStatusLine(StatusLine statusLine) {}
                @Override
                public void setStatusLine(ProtocolVersion protocolVersion, int i) {}
                @Override
                public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {}
                @Override
                public void setStatusCode(int i) throws IllegalStateException {}
                @Override
                public void setReasonPhrase(String s) throws IllegalStateException {}
                @Override
                public HttpEntity getEntity() {
                    return null;
                }
                @Override
                public void setEntity(HttpEntity httpEntity) {}
                @Override
                public Locale getLocale() {
                    return null;
                }
                @Override
                public void setLocale(Locale locale) {}
                @Override
                public ProtocolVersion getProtocolVersion() {
                    return null;
                }
                @Override
                public boolean containsHeader(String s) {
                    return false;
                }
                @Override
                public Header[] getHeaders(String s) {
                    return new Header[0];
                }
                @Override
                public Header getFirstHeader(String s) {
                    return null;
                }
                @Override
                public Header getLastHeader(String s) {
                    return null;
                }
                @Override
                public Header[] getAllHeaders() {
                    return new Header[0];
                }
                @Override
                public void addHeader(Header header) {}
                @Override
                public void addHeader(String s, String s1) {}
                @Override
                public void setHeader(Header header) {}
                @Override
                public void setHeader(String s, String s1) {}
                @Override
                public void setHeaders(Header[] headers) {}
                @Override
                public void removeHeader(Header header) {}
                @Override
                public void removeHeaders(String s) {}
                @Override
                public HeaderIterator headerIterator() {
                    return null;
                }
                @Override
                public HeaderIterator headerIterator(String s) {
                    return null;
                }
                @Override
                public HttpParams getParams() {
                    return null;
                }
                @Override
                public void setParams(HttpParams httpParams) {}
            };

            String returnData = "";

            try {
                response = httpclient.execute(urls[0]);
            } catch (Exception e) {
                Log.e("Exception talk", e.toString());
            }


            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder builder = new StringBuilder();
                String str = "";

                while ((str = rd.readLine()) != null) {
                    builder.append(str);
                }

                returnData = builder.toString();
            } catch (Exception e) {
                Log.e("Exception talk", e.toString());
            }

            return returnData;
        }

        protected void onPostExecute(String result)
        {
            Log.i("SIGNresult",result.toString());

            if(result.toString().equals("SUCCESS"))
            {
                Toast.makeText(getApplicationContext(),"그동안 감사했습니다...훌쩍 ㅠ",Toast.LENGTH_SHORT).show();

                ActivityCompat.finishAffinity(Main.mainAC);
                finish();


            }
            else
            {
                Toast.makeText(getApplicationContext(),"회원탈퇴 에러 : "+result.toString(),Toast.LENGTH_SHORT).show();
            }

            StopShow();
        }
    }

















    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("정보를 불러오는 중입니다..");
        loading.setCanceledOnTouchOutside(false);
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}

}
