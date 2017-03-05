package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class Main extends Activity
{
    public static Context MinContext;  // context 스테틱

    AppInfo appInfo;   // 데이터
    ProgressDialog loading;   // 프로그레스 설정

    // 애드 광고 관련
    phpdown task;
    HashMap<String,Object> banner_1;
    HashMap<String,Object> banner_2;
    HashMap<String,Object> banner_3;
    HashMap<String,Object> banner_4;
    HashMap<String,Object> banner_base;

    Intent adintent;
    Uri aduri;

    // 탭 JAVA 선언
    public Tab1_read tab1_;
    public Tab2_read tab2_;
    public Tab3_read tab3_;
    public Tab4_read tab4_;

    LinearLayout add_Linear;  // 내부 삽입 레이아웃
    LayoutInflater Inflater;
    LinearLayout.LayoutParams layoutParams;

    //디바이스 ID  GET
    public TelephonyManager manager;

    //레이아웃 선언
    ImageView adView;      // 배너

    // 탭 아이콘 연결
    ImageView icon_tab1;
    ImageView icon_tab2;
    ImageView icon_tab3;
    ImageView icon_tab4;

    BitmapDrawable tab1img;
    BitmapDrawable tab2img;
    BitmapDrawable tab3img;
    BitmapDrawable tab4img;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MinContext = this;

        loading = new ProgressDialog(this);
        InitShow();

        // 아이콘 세팅
        icon_tab1 = (ImageView) findViewById(R.id.icon_tab1_init);
        icon_tab2 = (ImageView) findViewById(R.id.icon_tab2_init);
        icon_tab3 = (ImageView) findViewById(R.id.icon_tab3_init);
        icon_tab4 = (ImageView) findViewById(R.id.icon_tab4_init);

        appInfo = new AppInfo();  // 데이터
       // appInfo = (AppInfo) getApplicationContext();
        AppInfo.TargetContext = this;  // 타겟 Context 변경

        AppInfo.StateApp = true;  // 앱 실행상태로 변경
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        adView = (ImageView) findViewById(R.id.adView);  // 애드 뷰 (광고 배너)

        ad_submit();
        ad_load();

    }


    public void ad_load()
    {

        banner_1 = new HashMap<String, Object>();       // 배너1 데이터
        banner_2 = new HashMap<String, Object>();       // 배너2 데이터
        banner_3 = new HashMap<String, Object>();       // 배너3 데이터
        banner_4 = new HashMap<String, Object>();       // 배너4 데이터
        banner_base = new HashMap<String, Object>();    // 배너5 데이터

        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Ad_loadURL());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }

    }

    public void init_Layout()        // 초기 세팅
    {
        icon_changeTab("tab1");

        add_Linear = (LinearLayout) findViewById(R.id.inLayout);

        Inflater = getLayoutInflater();

        Inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tab1_ = new Tab1_read();
        tab2_ = new Tab2_read();
        tab3_ = new Tab3_read();
        tab4_ = new Tab4_read();


        // 초기 1번탭 처음 실행
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        add_Linear.removeAllViews();
        add_Linear.addView(tab1_.in_layout,layoutParams);


        if(!banner_1.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_1";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_1.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        tab1_.init_tab1();

    }

    // TAB 버튼 세팅

    public void Btn_Tab_1(View v)   // 탭1 자랑하기
    {
        icon_changeTab("tab1");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab1_.in_layout,layoutParams);

        if(!banner_1.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_1";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_1.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        tab1_.all_tab1();
    }

    public void Btn_Tab_2(View v)   // 탭2  포인트
    {

        Intent intent = new Intent(this, Tab2_read.class);

        if(!banner_2.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_2";
            intent.putExtra("ad_type",banner_2.get("type").toString());
            intent.putExtra("ad_data",banner_2.get("data").toString());
            intent.putExtra("ad_img",banner_2.get("adimg").toString());
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            intent.putExtra("ad_type",banner_base.get("type").toString());
            intent.putExtra("ad_data",banner_base.get("data").toString());
            intent.putExtra("ad_img",banner_base.get("adimg").toString());

        }

        startActivity(intent);
        overridePendingTransition(0, 0);

    }

    public void Btn_Tab_3(View v)   // 탭3  낚중일기
    {
        icon_changeTab("tab3");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab3_.in_layout);

        if(!banner_3.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_3";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_3.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        //tab3_.init_tab3();
    }

    public void Btn_Tab_4(View v)   // 탭4  설정
    {
        icon_changeTab("tab4");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        add_Linear.addView(tab4_.in_layout,layoutParams);

        if(!banner_4.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_4";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_4.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        tab4_.init_tab4();
    }


   /////////////////////////////////////////////////////// // 탭 함수로 설정


    public void ftn_Tab_1()   // 탭1 자랑하기
    {
        icon_changeTab("tab1");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab1_.in_layout,layoutParams);

        if(!banner_1.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_1";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_1.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        tab1_.all_tab1();
    }

    public void ftn_Tab_2()   // 탭2  포인트
    {

        Intent intent = new Intent(this, Tab2_read.class);
        startActivity(intent);
        overridePendingTransition(0, 0);


        /*
        icon_changeTab("tab2");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab2_.in_layout);

        if(!banner_2.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_2";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_2.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        tab2_.init_tab2();
        */
    }

    public void ftn_Tab_3()   // 탭3  낚중일기
    {
        icon_changeTab("tab3");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab3_.in_layout);

        if(!banner_3.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_3";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_3.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        //tab3_.init_tab3();
    }

    public void ftn_Tab_4()   // 탭4  설정
    {
        icon_changeTab("tab4");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        add_Linear.addView(tab4_.in_layout,layoutParams);

        if(!banner_4.get("state").toString().equals("none"))
        {
            AppInfo.ViewAD = "banner_4";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_4.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }
        else
        {
            AppInfo.ViewAD = "banner_base";
            Glide.with(this.getApplicationContext()).load(appInfo.Get_Ad_loadimgURLL()+banner_base.get("adimg").toString()).asGif().thumbnail(0.5f).diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
        }

        tab4_.init_tab4();
    }





    ////////////////////////애드 광고 클릭 부분 //////////////////////
    public void ad_submit()
    {
        adView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String type ="";
                String data ="";


                if(AppInfo.ViewAD.toString().equals("banner_1"))
                {
                    type = banner_1.get("type").toString();
                    data = banner_1.get("data").toString();
                }
                else if(AppInfo.ViewAD.toString().equals("banner_2"))
                {
                    type = banner_2.get("type").toString();
                    data = banner_2.get("data").toString();
                }
                else if(AppInfo.ViewAD.toString().equals("banner_3"))
                {
                    type = banner_3.get("type").toString();
                    data = banner_3.get("data").toString();
                }
                else if(AppInfo.ViewAD.toString().equals("banner_4"))
                {
                    type = banner_4.get("type").toString();
                    data = banner_4.get("data").toString();
                }
                else if(AppInfo.ViewAD.toString().equals("banner_base"))
                {
                    type = banner_base.get("type").toString();
                    data = banner_base.get("data").toString();
                }


                // 실행
                if(type.toString().equals("url"))
                {
                    adintent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                    startActivity(adintent);
                }
                else if(type.toString().equals("call"))
                {
                    adintent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+data));
                    startActivity(adintent);
                }
                else if(type.toString().equals("email"))
                {
                    adintent = new Intent(Intent.ACTION_SEND);
                    adintent.setType("plain/text");
                    String[] tos = {data};
                    adintent.putExtra(Intent.EXTRA_EMAIL, tos);
                    adintent.putExtra(Intent.EXTRA_SUBJECT, "[낚중일기] 광고 문의합니다.");
                    adintent.putExtra(Intent.EXTRA_TEXT, "광고 하고자 하는 내용을 작성 또는 첨부하여 보내주시면 답변드리겠습니다. \n\n-낚중일기-");
                    startActivity(adintent);
                }

            }
        });
    }


/*

///////////////// 탭 1  부분 //////////////////////
 */

    public void View_ZoomImage(String imgurl)
    {
        Intent intent = new Intent(Main.this,View_img.class);
        intent.putExtra("ImuUrl",imgurl);
        startActivity(intent);

    }

    // 보기 선택 (전체 , 내글)
    public void tab1_btn_category(View v)
    {
        showDialog(1);
    }


    // 타입별 불러오기  전체, 내글 (선택형 다이얼로그) 탭 1
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                final CharSequence[] item = {"전체", "내글"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("자랑 유형을 선택하세요.") // 제목 설정
                        .setItems(item, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (item[i].toString().equals(item[0])) {
                                    tab1_.all_tab1();   // 전체글
                                } else if (item[i].toString().equals(item[1])) {
                                    tab1_.my_tab1();  // 내글
                                } else {
                                    dialogInterface.dismiss();
                                }
                            }
                        });

                AlertDialog alert = builder.create();  //알림 객체 생성
                return alert;
        }
        return null;
    }

    //자랑하기에서 위치 이동
    public void tab1_loaction_start(String type, String latitude, String longitude)
    {
        if(type.toString().equals("true"))
        {
            Double tmpA = Double.parseDouble(latitude);
            Double tmpB = Double.parseDouble(longitude);

            Intent intent = new Intent(Main.MinContext,Tab1_map.class);
            intent.putExtra("type","view_point");
            intent.putExtra("latitude",tmpA);
            intent.putExtra("longitude",tmpB);
            startActivity(intent);
        }
    }

    // 멘트창 열기
    public void tab1_ment_start(boolean type, String idx, String talk_idx, String talk_writeid)
    {
        Intent intent = new Intent(Main.MinContext, Tab1_ment.class);
        if(type)
        {
            // 댓글이 1개라도 있을떄
            intent.putExtra("type","tre");
            intent.putExtra("putidx",idx);
            intent.putExtra("puttalkidx",talk_idx);
            intent.putExtra("puttalkwriteid",talk_writeid);

            startActivity(intent);
        }
        else
        {
          Toast.makeText(getApplicationContext(),"서버연결이 불안정합니다.",Toast.LENGTH_SHORT).show();
        }

    }


  // 메인 부분
    // 자랑하기 글쓰기
    public void tab1_btn_write(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(), Tab_addtalk.class);
        intent.putExtra("add_type","ADD");
        intent.putExtra("talk_id","none");
        startActivity(intent);
    }

    // 자랑하기 수정하기
    public void tab1_fix(String talk_id)
    {
        Intent intent = new Intent(this.getApplicationContext(), Tab_addtalk.class);
        intent.putExtra("add_type","FIX");
        intent.putExtra("talk_id",talk_id);
        startActivity(intent);
    }



///////////////// 탭 3 부분 //////////////////////

    public void YoutubePlay(String value)
    {
        Intent intent = new Intent(Main.MinContext, YoutubeActivity.class);
        intent.putExtra("movurl",value);
        startActivity(intent);
    }


    ///////////////// 탭 4  부분 //////////////////////  설정

    // 프로필 설정 부분
    public void tab4_setting_fixprofile(View v)
    {
        Intent intent = new Intent(MinContext,Tab4_fixprofile.class);

        startActivity(intent);

    }

    // 기본설정 부분
    public void tab4_setting_fixbase(View v)
    {
        Intent intent = new Intent(MinContext,Tab4_fixbase.class);

        startActivity(intent);
    }

    // 후원하기 부분
    public void tab4_setting_fixsponsor(View v)
    {
        Intent intent = new Intent(MinContext,Tab4_sponsor.class);

        startActivity(intent);
    }

    // 후원자 기록 부분
    public void tab4_setting_fixsponsorlist(View v)
    {
        Intent intent = new Intent(MinContext,Tab4_sponsorlist.class);

        startActivity(intent);
    }



    public void icon_changeTab(String value)
    {
        if(value.toString().equals("tab1")) {
            tab1img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab1_);
            tab2img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab2);
            tab3img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab3);
            tab4img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab4);

            icon_tab1.setImageDrawable(tab1img);
            icon_tab2.setImageDrawable(tab2img);
            icon_tab3.setImageDrawable(tab3img);
            icon_tab4.setImageDrawable(tab4img);
        }
        else if(value.toString().equals("tab2"))
        {
            tab1img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab1);
            tab2img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab2_);
            tab3img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab3);
            tab4img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab4);

            icon_tab1.setImageDrawable(tab1img);
            icon_tab2.setImageDrawable(tab2img);
            icon_tab3.setImageDrawable(tab3img);
            icon_tab4.setImageDrawable(tab4img);
        }
        else if(value.toString().equals("tab3"))
        {
            tab1img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab1);
            tab2img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab2);
            tab3img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab3_);
            tab4img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab4);

            icon_tab1.setImageDrawable(tab1img);
            icon_tab2.setImageDrawable(tab2img);
            icon_tab3.setImageDrawable(tab3img);
            icon_tab4.setImageDrawable(tab4img);
        }
        else if(value.toString().equals("tab4"))
        {
            tab1img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab1);
            tab2img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab2);
            tab3img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab3);
            tab4img = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_tab4_);

            icon_tab1.setImageDrawable(tab1img);
            icon_tab2.setImageDrawable(tab2img);
            icon_tab3.setImageDrawable(tab3img);
            icon_tab4.setImageDrawable(tab4img);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////// 통신


    private class phpdown extends AsyncTask<HttpPost, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(HttpPost... urls) {
            String returnData = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = new HttpResponse() {
                @Override
                public StatusLine getStatusLine() {
                    return null;
                }

                @Override
                public void setStatusLine(StatusLine statusLine) {
                }

                @Override
                public void setStatusLine(ProtocolVersion protocolVersion, int i) {
                }

                @Override
                public void setStatusLine(ProtocolVersion protocolVersion, int i, String s) {
                }

                @Override
                public void setStatusCode(int i) throws IllegalStateException {
                }

                @Override
                public void setReasonPhrase(String s) throws IllegalStateException {
                }

                @Override
                public HttpEntity getEntity() {
                    return null;
                }

                @Override
                public void setEntity(HttpEntity httpEntity) {
                }

                @Override
                public Locale getLocale() {
                    return null;
                }

                @Override
                public void setLocale(Locale locale) {
                }

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
                public void addHeader(Header header) {
                }

                @Override
                public void addHeader(String s, String s1) {
                }

                @Override
                public void setHeader(Header header) {
                }

                @Override
                public void setHeader(String s, String s1) {
                }

                @Override
                public void setHeaders(Header[] headers) {
                }

                @Override
                public void removeHeader(Header header) {
                }

                @Override
                public void removeHeaders(String s) {
                }

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
                public void setParams(HttpParams httpParams) {
                }
            };


            try {
                response = httpclient.execute(urls[0]);
            } catch (Exception e) {
                // 서버에 연결할 수 없습니다 토스트 메세지 보내기
//                Toast.makeText((MainActivity) MainActivity.mContext, ((MainActivity) MainActivity.mContext).getResources().getText(R.string.server_connect_error), Toast.LENGTH_SHORT).show();
                Log.e("TalkPagePost Exception", e.toString());
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
//                Toast.makeText((MainActivity) MainActivity.mContext, ((MainActivity) MainActivity.mContext).getResources().getText(R.string.server_connect_error), Toast.LENGTH_SHORT).show();
                Log.e("TalkPagePost Exception", e.toString());
            }

            return returnData;
        }

        @Override
        protected void onPostExecute(String str) {

            // 보기 좋은 형태로 변수에 대입
            String idx_;
            String state_;
            String position_;
            String type_;
            String data_;
            String adimg_;
            String addatetime_;

            if (!str.toString().equals("sql_error"))
            {
                try {
                    JSONObject root = new JSONObject(str);

                    JSONArray ja = root.getJSONArray("result");

                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jo = ja.getJSONObject(i);
                        idx_ = jo.getString("idx");
                        state_ = jo.getString("state");
                        position_ = jo.getString("position");
                        type_ = jo.getString("type");
                        data_ = jo.getString("data");
                        adimg_ = jo.getString("adimg");
                        addatetime_ = jo.getString("addatetime");


                        if(position_.toString().equals("TAB1"))
                        {
                            banner_1.put("idx", idx_);
                            banner_1.put("state", state_);
                            banner_1.put("position", position_);
                            banner_1.put("type", type_);
                            banner_1.put("data", data_);
                            banner_1.put("adimg", adimg_);
                            banner_1.put("addatetime", addatetime_);
                        }
                        else if(position_.toString().equals("TAB2"))
                        {
                            banner_2.put("idx", idx_);
                            banner_2.put("state", state_);
                            banner_2.put("position", position_);
                            banner_2.put("type", type_);
                            banner_2.put("data", data_);
                            banner_2.put("adimg", adimg_);
                            banner_2.put("addatetime", addatetime_);
                        }
                        else if(position_.toString().equals("TAB3"))
                        {
                            banner_3.put("idx", idx_);
                            banner_3.put("state", state_);
                            banner_3.put("position", position_);
                            banner_3.put("type", type_);
                            banner_3.put("data", data_);
                            banner_3.put("adimg", adimg_);
                            banner_3.put("addatetime", addatetime_);
                        }
                        else if(position_.toString().equals("TAB4"))
                        {
                            banner_4.put("idx", idx_);
                            banner_4.put("state", state_);
                            banner_4.put("position", position_);
                            banner_4.put("type", type_);
                            banner_4.put("data", data_);
                            banner_4.put("adimg", adimg_);
                            banner_4.put("addatetime", addatetime_);
                        }
                        else if(position_.toString().equals("BASE"))
                        {
                            banner_base.put("idx", idx_);
                            banner_base.put("state", state_);
                            banner_base.put("position", position_);
                            banner_base.put("type", type_);
                            banner_base.put("data", data_);
                            banner_base.put("adimg", adimg_);
                            banner_base.put("addatetime", addatetime_);
                        }

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                init_Layout();
            }
        }
    }





    // 뒤로가기 (종료 메인이라서)
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
        loading.setMessage("정보를 불러오는 중입니다..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}
}
