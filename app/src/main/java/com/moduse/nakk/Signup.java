package com.moduse.nakk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.Vector;

/**
 * Created by sejung on 2017-02-06.
 */

public class Signup extends AppCompatActivity
{
    ProgressDialog loading;
    AppInfo appInfo;

    String user_device;
    EditText user_id;
    EditText user_pass;
    EditText user_repass;
    EditText user_email;
    EditText user_nickname;

    CheckBox User_agee1;
    CheckBox User_agee2;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        appInfo = new AppInfo();   // info 데이터 초기화

        loading = new ProgressDialog(Signup.this);  //  다이얼로그 초기화
        InitShow();

        // 레이아웃 초기화
        user_id = (EditText) findViewById(R.id.signup_edit_id);
        user_pass = (EditText) findViewById(R.id.signup_edit_pass);
        user_repass = (EditText) findViewById(R.id.signup_edit_repass);
        user_email = (EditText) findViewById(R.id.signup_edit_email);
        user_nickname = (EditText) findViewById(R.id.signup_edit_nickname);

        User_agee1 = (CheckBox) findViewById(R.id.signup_check_agee1);
        User_agee2 = (CheckBox) findViewById(R.id.signup_check_agee2);

        AppInfo.TargetContext = this;


    }


    // 버튼 리스너
    public void btn_signup_submit(View v)
    {

        Check_validity();
        StartShow();
    }

    // 회원가입 유효성 항목 체크후 -> 가입 PHP gka
    public void Check_validity()
    {

        user_device = appInfo.Get_DeviceID();         //  디바이스 아이디 가져오기

        String push = AppInfo.MY_PUSHID;
        String id = user_id.getText().toString();
        String pass = user_pass.getText().toString();
        String repass = user_repass.getText().toString();
        String nickname = user_nickname.getText().toString();
        String email = user_email.getText().toString();


        boolean agee_1 = User_agee1.isChecked();
        boolean agee_2 = User_agee2.isChecked();

        if(id.toString().equals(null) || id.toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "아이디가 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else if(pass.toString().equals(null) || pass.toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "패스워드가 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else if(repass.toString().equals(null) || repass.toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "재입력 패스워드가 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else if(email.toString().equals(null) || email.toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "이메일이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else if(nickname.toString().equals(null) || nickname.toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "닉네임이 입력되지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else if(!agee_1)
        {
            Toast.makeText(getApplicationContext(), "이용약관에 동의하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else if(!agee_2)
        {
            Toast.makeText(getApplicationContext(), "위치정보 약관에 동의하지 않았습니다.", Toast.LENGTH_LONG).show();
        }
        else if(!pass.toString().equals(repass))
        {
            Toast.makeText(getApplicationContext(), "패스워드가 올바르지 않습니다. 확인 해주세요.", Toast.LENGTH_LONG).show();
        }
        else
        {
            insertToDatabase(push,id, pass,user_device, nickname, email);
        }
    }

    // 이용약관보기
    public void signup_check_agee1(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(),Ageeinfo.class);
        intent.putExtra("ageetype","agee1");

        startActivity(intent);
    }
    public void signup_check_agee2(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(),Ageeinfo.class);
        intent.putExtra("ageetype","agee2");

        startActivity(intent);
    }


    private void insertToDatabase(String pushid ,String id, String pass, String deviceid, String nickname, String email)
    {

        class InsertData extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                StopShow();


                if(result.toString().equals("SUCCESS"))
                {
                    finish();

                    Toast.makeText(getApplicationContext(),"회원 가입에 성공하였습니다." , Toast.LENGTH_LONG).show();
                }
                else if(result.toString().equals("DQLICATE"))
                {
                    Toast.makeText(getApplicationContext(), "기존에 가입되어 있는 아이디 입니다.", Toast.LENGTH_LONG).show();
                }
                else if(result.toString().equals("CHARFAILURE"))
                {
                    Toast.makeText(getApplicationContext(), "모든 항목을 작성해야 합니다.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "인터넷 환경이 불안정 합니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            protected String doInBackground(String... params) {
                boolean state = false;

                // TODO Auto-generated method stub
                try {
                    String postURL = appInfo.Get_SignUpURL();
                    HttpPost request = new HttpPost(postURL);
                    Vector<NameValuePair> list = new Vector<NameValuePair>();
                    //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.

                    String push = params[0];
                    String id = params[1];
                    String pass = params[2];
                    String device = params[3];
                    String nickname = params[4];
                    String email = params[5];

                    Log.i("Value","push : "+push);
                    Log.i("Value","id : "+id);
                    Log.i("Value","pass : "+pass);
                    Log.i("Value","device : "+device);
                    Log.i("Value","nickname : "+nickname);
                    Log.i("Value","email : "+email);


                   // Log.i("result "," /push :"+push+" /id :"+id+" /pass :"+pass+" /device :"+device+" /nickname :"+nickname+" /email :"+email);
                    // Log.i("TAG","device_id :"+device_id +"\nid :"+id+"\npass :"+pass+"\nname :"+name+"\naddress :"+address+"\nage :"+age);

                    list.add(new BasicNameValuePair("user_push",push));
                    list.add(new BasicNameValuePair("user_id",id));
                    list.add(new BasicNameValuePair("user_pass", pass));
                    list.add(new BasicNameValuePair("user_device", device));
                    list.add(new BasicNameValuePair("user_nickname", nickname));
                    list.add(new BasicNameValuePair("user_email", email));

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
                    while((tmp = reader.readLine()) != null)
                    {
                        if(tmp!=null)
                        {
                            total += tmp;
                        }
                    }
                    im.close();

//이곳에서 UI를 변경하면 에러

                    return total;

                } catch (UnsupportedEncodingException e) {

                    // TODO Auto-generated catch block

                    e.printStackTrace();

                } catch(IOException e){

                }
                return null;     //오류시 null

            }
        }

        InsertData task = new InsertData();  // 함수 쓰레드 설정
        task.execute(pushid,id,pass,deviceid,nickname, email);  // 함수 쓰레드 시작
    }


    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("잠시만 기다려주세요..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}
}
