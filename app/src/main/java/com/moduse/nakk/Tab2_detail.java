package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by sejung on 2017-03-06.
 */

public class Tab2_detail extends Activity
{

    // 지도쪽 리플래시
    Tab2_read tab2_read;

    //데이터
    AppInfo appInfo;

    //프로그레스
    ProgressDialog loading;


    // 읽어 들일 인텐트 정보
    String Load_PointIdx = "";
    Intent DetailIntent;

    // 통신으로 받아올 데이터
    HashMap<String,Object> pointdata;

    //레이아웃
    ImageView point_img;
    TextView  point_time;
    TextView  point_address;
    TextView  point_datafield;
    TextView  point_datapreparation;
    TextView  point_dataetc;

    // 댓글 갯수
    TextView mentnumText;

    LinearLayout fix_btn;
    LinearLayout delete_btn;

    // 통신
    phpdown task;
    String PHPTYPE = "";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2_read);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);
        tab2_read = new Tab2_read();
        InitShow();

        // 인텐트 데이터 불러오기
        DetailIntent = getIntent();
        Load_PointIdx = DetailIntent.getStringExtra("Point_idx");

        // 레이아웃 설정

        point_img = (ImageView) findViewById(R.id.tab2_read_point_img);
        point_time = (TextView) findViewById(R.id.tab2_read_point_time);
        point_address = (TextView) findViewById(R.id.tab2_read_point_address);
        point_datafield = (TextView) findViewById(R.id.tab2_read_point_datafield);
        point_datapreparation = (TextView) findViewById(R.id.tab2_read_point_datapreparation);
        point_dataetc = (TextView) findViewById(R.id.tab2_read_point_dataetc);

        mentnumText = (TextView) findViewById(R.id.tab2_ment_num);

        fix_btn = (LinearLayout) findViewById(R.id.tab2_read_fix_lin);
        delete_btn = (LinearLayout) findViewById(R.id.tab2_read_delete_lin);

        pointdata = new HashMap<String, Object>();  // 데이터를 넣을 리스트 선언

        Load_Point_detail();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////인텐트 REDULT
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1)
        {
            String tmpType = "";
            tmpType = data.getStringExtra("fix_type");

            if(tmpType.toString().equals("false"))
            {
                Toast.makeText(this,"수정을 취소했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(tmpType.toString().equals("true"))
            {
                Load_Point_detail();
            }
        }
        if(requestCode == 2)
        {
            String tmpType = "";
            tmpType = data.getStringExtra("fix_type");

            if(tmpType.toString().equals("false"))
            {
               // Toast.makeText(this,"수정을 취소했습니다.",Toast.LENGTH_SHORT).show();
            }
            else if(tmpType.toString().equals("true"))
            {
                Load_Point_detail();
            }
        }

    }

        ////////////////////////////////////////////////////////////// 버튼 설정
    // 수정 버튼
    public void tab2_btn_read_fix(View v)
    {

        Intent intent = new Intent(this, Tab2_addpoint.class);

        intent.putExtra("point_type","FIX");
        intent.putExtra("point_idx",String.valueOf(Load_PointIdx));

        startActivityForResult(intent,2);

      //  Load_PointIdx ="";
       // finish();
    }

    // 멘트 엑티비티 실행
    public void tab2_btn_mentopen(View v)
    {
        Intent intent = new Intent(this, Tab2_ment.class);
        intent.putExtra("point_idx",String.valueOf(Load_PointIdx));
        intent.putExtra("menter_loginID",pointdata.get("loginID").toString());
        intent.putExtra("menter_deviceID",pointdata.get("deviceID").toString());

        startActivityForResult(intent,1);
    }

    // 삭제 버튼
    public void tab2_btn_read_delete(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("[포인트 삭제]") // 제목 설정
                .setMessage("해당 포인트를 삭제 합니다.\n(삭제 이후 복구 불가능 합니다.)")
                .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    // 예 버튼 클릭시 설정
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // finish();

                        Delete_Point();
                       // finish();
                    }
                })
                .setNegativeButton("아니요", null).show();

        AlertDialog alert = builder.create();  //알림 객체 생성
    }

    /////////////////////////////////////////////////////////////////  포인트 삭제 PHP 통신
    public void Delete_Point()
    {
        StartShow();
        String tmpidx = String.valueOf(Load_PointIdx);

        PHPTYPE = "DELETE";
        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();
        post.add(new BasicNameValuePair("pointidx", tmpidx));
        post.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab2_PointdeleteURL());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);
            //Load_PointIdx ="";

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }

    /////////////////////////////////////////////////////////////////  정보 불러오기 PHP 통신
    public void Load_Point_detail()
    {
        StartShow();
        PHPTYPE = "LOAD";
        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("point_idx", Load_PointIdx));


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab2_PointdetailselectURL());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////// 토크 업로드


    private class phpdown extends AsyncTask<HttpPost, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //  loading = ProgressDialog.show(Intro_app.this, "버전 체크중입니다.", null, true, true);
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

        protected void onPostExecute(String result) {
            //loading.dismiss();
            Log.i("detailresult",result);

            if(PHPTYPE.toString().equals("LOAD"))
            {
                if (result.toString().equals("CHARFALSE") || result.toString().equals("sql_error"))
                {
                    Toast.makeText(getApplicationContext(), "서버 접속이 불안정 합니다. 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    String idx_;
                    String loginID_;
                    String deviceID_;
                    String point_idx_;
                    String point_state_;
                    String point_time_;
                    String point_address_;
                    String point_datafield_;
                    String point_datapreparation_;
                    String point_dataetc_;
                    String point_img_;
                    String ment_num_;


                    try {
                        JSONObject root = new JSONObject(result);

                        JSONArray ja = root.getJSONArray("result");

                        for (int i = 0; i < ja.length(); i++) {

                            JSONObject jo = ja.getJSONObject(i);
                            idx_ = jo.getString("idx");
                            loginID_ = jo.getString("loginID");
                            deviceID_ = jo.getString("deviceID");
                            point_idx_ = jo.getString("point_idx");
                            point_state_ = jo.getString("point_state");
                            point_time_ = jo.getString("point_time");
                            point_address_ = jo.getString("point_address");
                            point_datafield_ = jo.getString("point_datafield");
                            point_datapreparation_ = jo.getString("point_datapreparation");
                            point_dataetc_ = jo.getString("point_dataetc");
                            point_img_ = jo.getString("point_img");
                            ment_num_ = jo.getString("ment_num");

                            pointdata.put("idx", idx_);
                            pointdata.put("loginID", loginID_);
                            pointdata.put("deviceID", deviceID_);
                            pointdata.put("point_idx", point_idx_);
                            pointdata.put("point_state", point_state_);
                            pointdata.put("point_time", point_time_);
                            pointdata.put("point_address", point_address_);
                            pointdata.put("point_datafield", point_datafield_);
                            pointdata.put("point_datapreparation", point_datapreparation_);
                            pointdata.put("point_dataetc", point_dataetc_);
                            pointdata.put("point_img", point_img_);
                            pointdata.put("ment_num", ment_num_);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // UI 적용 함수 시작
                    UiUpdate();
                }
            }
            else if(PHPTYPE.toString().equals("DELETE"))
            {
                if (result.toString().equals("SUCCESS"))
                {
                    Toast.makeText(getApplicationContext(), "해당 포인트가 삭제 되었습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("updatecode","REFRASH");
                    setResult(2,intent);

                    finishAfterTransition();
                }
                else if (result.toString().equals("CHARFALSE"))
                {
                    Toast.makeText(getApplicationContext(), "서버 접속이 불안정 합니다. 확인해주세요.1", Toast.LENGTH_SHORT).show();
                }
                else if (result.toString().equals("sql_error"))
                {
                    Toast.makeText(getApplicationContext(), "서버 접속이 불안정 합니다. 확인해주세요.2", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "서버 접속이 불안정 합니다. 확인해주세요.3", Toast.LENGTH_SHORT).show();
                }
                StopShow();
            }
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////// UI 업데이트
    public void UiUpdate()
    {
        try
        {

            if(!pointdata.get("point_img").toString().equals("none"))
            {
                Glide.with(this).load(appInfo.Get_Tab2_PointimgFTP_URL()+pointdata.get("point_img")).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(point_img);
            }
            else
            {
                Glide.with(this).load(R.drawable.write_talkimg).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(point_img);
            }

            // 핀치줌
            point_img.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(!pointdata.get("point_img").toString().equals("none"))
                    {
                        View_ZoomImage(appInfo.Get_Tab2_PointimgFTP_URL() + pointdata.get("point_img").toString());
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"기록된 사진이 없습니다.",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            point_time.setText(pointdata.get("point_time").toString());
            point_address.setText(pointdata.get("point_address").toString());
            point_datafield.setText(pointdata.get("point_datafield").toString());
            point_datapreparation.setText(pointdata.get("point_datapreparation").toString());
            point_dataetc.setText(pointdata.get("point_dataetc").toString());

            if(pointdata.get("loginID").toString().equals(AppInfo.MY_LOGINID))
            {
                fix_btn.setVisibility(View.VISIBLE);
                delete_btn.setVisibility(View.VISIBLE);
            }
            else
            {
                fix_btn.setVisibility(View.GONE);
                delete_btn.setVisibility(View.GONE);
            }


            // 멘트 갯수
            mentnumText.setText(pointdata.get("ment_num").toString()+" ");
        }
        catch (Exception e)
        {
            Log.i("Exception",e.toString());
        }

        StopShow();
    }

    //  핀치 줌으로 이동
    public void View_ZoomImage(String imgurl)
    {
        Intent intent = new Intent(Tab2_detail.this,View_img.class);
        intent.putExtra("ImuUrl",imgurl);
        startActivity(intent);

    }

    // 뒤로가기 (댓글창 닫기)
    @Override
    public void onBackPressed() {
        Log.i("Back","true");
        Load_PointIdx = "";
        Intent intent = new Intent();
        intent.putExtra("updatecode", "REFRASH");
        setResult(2, intent);

        finishAfterTransition();
    }


    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("정보를 불러오고 있습니다..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}


}
