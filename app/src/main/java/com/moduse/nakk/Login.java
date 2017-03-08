package com.moduse.nakk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

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

public class Login extends AppCompatActivity {

    ProgressDialog loading;
    AppInfo appInfo;

    EditText user_id;
    EditText user_pass;

    CheckBox loginsave;

    SharedPreferences setting;

    SharedPreferences.Editor editor;


    private InputMethodManager imm;

    //GCM
    GoogleCloudMessaging gcm;
    String SENDER_ID;
    String token;




    // 푸시 아이디 가져오기
    public void Pushtoken()
    {
        SENDER_ID = getString(R.string.gcm_defaultSenderId);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    token = gcm.register(SENDER_ID);
                    AppInfo.MY_PUSHID = token;
                   // Log.i("Pushtoken",token);
                } catch (IOException ex) {
                }
                return "";
            }

            @Override
            protected void onPostExecute(String msg)
            {
            }
        }.execute(null, null, null);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        AppInfo.TargetContext = this;

        appInfo = new AppInfo();

        loading = new ProgressDialog(Login.this);

        user_id = (EditText) findViewById(R.id.edit_userid);
        user_pass = (EditText) findViewById(R.id.edit_userpass);
        loginsave = (CheckBox) findViewById(R.id.login_save);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);   // 키보드 관리 내리기 올리기 설정

        //초기화
        InitShow();

        setting = getSharedPreferences("setting", 0);  // 자동 로그인 부분 설정
        editor= setting.edit();

        // 로그인 부분 불러오기 (로컬)
        if(setting.getBoolean("Auto_Login_enabled",false))
        {
            user_id.setText(setting.getString("ID", ""));
            user_pass.setText(setting.getString("PW", ""));
            loginsave.setChecked(true);
        }

        // 로그인 부분 불러오기 (로컬)
        AppInfo.Push_state = !setting.getBoolean("PUSH_STATE", false);

        user_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                user_id.setText(null);
            }
        });

        user_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                user_pass.setText(null);
            }
        });

        gcm = GoogleCloudMessaging.getInstance(this);
        Pushtoken();

        AppInfo.MY_DEVICEID =  appInfo.Get_DeviceID();
    }



    public void btn_login(View v)
    {
        StartShow();
        String id = user_id.getText().toString();
        String pass = user_pass.getText().toString();

        //키보드 내림
        imm.hideSoftInputFromWindow(user_pass.getWindowToken(),0);

        // php 함수 호출
        Login_user(id,pass,token,appInfo.Get_LoginURL());

        if(loginsave.isChecked())
        {
            setting = getSharedPreferences("setting",0);

            editor.putString("ID", id);
            editor.putString("PW", pass);
            editor.putBoolean("Auto_Login_enabled", true);
            editor.commit();
        }
        else
        {
            /**
             * remove로 지우는것은 부분삭제
             * clear로 지우는것은 전체 삭제 입니다
             */
//					editor.remove("ID");
//					editor.remove("PW");
//					editor.remove("Auto_Login_enabled");
            editor.clear();
            editor.commit();
        }
    }

    public void btn_signup (View v)
    {
        Intent intent = new Intent(Login.this, Signup.class);
        startActivity(intent);
    }

    /// 비밀번호 찾기 이메일
    public void login_sand_emmail(View v)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        String[] tos = {"jaybustu@gmail.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_SUBJECT, "[낚중일기] 비밀번호 재발급");
        intent.putExtra(Intent.EXTRA_TEXT, "다음 양식을 기입하여 보내 주시면 가입하신 이메일로 임시 비밀번호를 발송해드립니다. " +
                "\n\n " +
                "가입 ID : \n"+
                "가입 이메일 : \n"+
                "가입 닉네임 : \n\n"+
                "해당 항목을 작성하여 보내주시기 바랍니다."+
                "\n\n\n\n-낚중일기-");
        startActivity(intent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("[앱 종료]") // 제목 설정
                        .setMessage("낚중일기를 종료 하시겠습니까?")
                        .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            // 예 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", null).show();

                AlertDialog alert = builder.create();  //알림 객체 생성
        }
        return super.onKeyDown(keyCode, event);
    }

    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("낚중일기에 접속중입니다..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}



    private void Login_user(String id, String pass, String token, String url)
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
                    AppInfo.MY_LOGINID = user_id.getText().toString();
                    Intent intent = new Intent(Login.this, Main.class);
                    startActivity(intent);
                    finish();

                }
                else if(result.toString().equals("FAILURE"))
                {
                    Toast.makeText(getApplicationContext(), "아이디 또는 패스워드가 잘못 입력되었습니다.", Toast.LENGTH_LONG).show();
                }
                else if(result.toString().equals("CHARFALSE"))
                {
                    Toast.makeText(getApplicationContext(), "아이디와 패스워드에 특수문자를 사용할수 없습니다.", Toast.LENGTH_LONG).show();
                }
                else if(result.toString().equals("CHARNULL"))
                {
                    Toast.makeText(getApplicationContext(), "찾을수 없는 아이디 입니다. 회원가입이 필요합니다.", Toast.LENGTH_LONG).show();
                }
                else if(result.toString().equals("BLOCK"))
                {
                    Toast.makeText(getApplicationContext(), "정지된 아이디 입니다. 고객센터에 문의주세요.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "통신이 불안정 합니다. 다시 시도해 주세요.", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            protected String doInBackground(String... params)
            {
                boolean state = false;

                // TODO Auto-generated method stub
                try
                {
                    String url = params[3];   // url 설정
                    HttpPost request = new HttpPost(url);
                    Vector<NameValuePair> list = new Vector<NameValuePair>();
                    //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.

                    String id_ = params[0];
                    String pass_ = params[1];
                    String token_ = params[2];


                    list.add(new BasicNameValuePair("LOGIN_ID", id_));
                    list.add(new BasicNameValuePair("LOGIN_PASS", pass_));
                    list.add(new BasicNameValuePair("TOKEN", token_));
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
        task.execute(id,pass,token,url);  // 함수 쓰레드 시작

    }




}
