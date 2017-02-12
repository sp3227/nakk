package com.moduse.nakk;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

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


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private Button mRegistrationButton;
    private ProgressBar mRegistrationProgressBar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;

    String token;

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */



    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }
    /**
     * LocalBroadcast 리시버를 정의한다. 토큰을 획득하기 위한 READY, GENERATING, COMPLETE 액션에 따라 UI에 변화를 준다.
     */



    public void registBroadcastReceiver(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if(action.equals(QuickstartPreferences.REGISTRATION_READY)){

                } else if(action.equals(QuickstartPreferences.REGISTRATION_GENERATING)){

                } else if(action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)){
                    token = intent.getStringExtra("token");
                }

            }
        };
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        registBroadcastReceiver();

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

        try
        {
            // 디바이스 ID 검사
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            user_device = manager.getDeviceId();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

         getInstanceIdToken();

    }

    /**
     * 앱이 실행되어 화면에 나타날때 LocalBoardcastManager에 액션을 정의하여 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));

    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }


    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    // 버튼 리스너
    public void btn_signup_submit(View v)
    {
        Check_validity();
    }

    // 회원가입 유효성 항목 체크후 -> 가입 PHP gka
    public void Check_validity()
    {
        String push = token;
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


    private void insertToDatabase(String pushid ,String id, String pass, final String deviceid, String nickname, String email)
    {

        class InsertData extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                StartShow();
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

                    String push = (String)params[0];
                    String id = (String)params[1];
                    String pass = (String)params[2];
                    String device = (String)params[3];
                    String nickname = (String)params[4];
                    String email = (String)params[5];


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
