package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by sejung on 2017-02-11.
 */

public class Tab1_read extends Activity
{

    public LinearLayout in_layout;
    public LayoutInflater Inflater;

    AppInfo appInfo;  // 앱 정보 선언

    phpdown downsever;  // 쓰레드

    ArrayList<TalkData> listItem = new ArrayList<TalkData>();
    private ListView list = null;
    private CustomAdapter customAdapter = null;

    String talk_type;
    int last_list_number;

    // php 핸들러 분류
    final int phptype_TalkALL = 0;
    final int phptype_TalkMY = 1;
    final int phptype_TalkDELETE = 3;
    final int phptype_TalkLIKE = 4;
    int phptype;

    //좋아요 임시 저장
    int tmpIndex;
    TextView tmpLikecount;


    Tab1_read()
    {
        Inflater = ((Main) Main.MinContext).getLayoutInflater();
        Inflater = (LayoutInflater) Main.MinContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        in_layout = (LinearLayout) Inflater.inflate(R.layout.tab1, null);

        list = (ListView) in_layout.findViewById(R.id.tab1_listview);  // 리스트 레이아웃 부분 설정

    }

    public void init_tab1()    //  기본글 불러오기  (전체)
    {
        phptype = phptype_TalkALL;
        talk_type = "ALL";
        appInfo = new AppInfo();

        // post 전달 인자
        Vector<NameValuePair> list = new Vector<NameValuePair>();
        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("type",talk_type));
        list.add(new BasicNameValuePair("deviceid",appInfo.Get_DeviceID()));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab1_TalkInselectURL();  // url 설정
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);

            downsever = new phpdown();  // 쓰레드 생성
            downsever.execute(request);
        }
        catch(Exception e)
        {
            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
            Toast.makeText(Main.MinContext, "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }

        // 받은 데이터 초기화  (일단 무시)
        //Remove_list();
    }

    public void all_tab1()    //  기본글 불러오기  (전체)
    {
        phptype = phptype_TalkALL;
        talk_type = "ALL";
        appInfo = new AppInfo();

        // post 전달 인자
        Vector<NameValuePair> list = new Vector<NameValuePair>();
        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("type",talk_type));
        list.add(new BasicNameValuePair("deviceid",appInfo.Get_DeviceID()));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab1_TalkInselectURL();  // url 설정
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);

            downsever = new phpdown();  // 쓰레드 생성
            downsever.execute(request);
        }
        catch(Exception e)
        {
            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
            Toast.makeText(Main.MinContext, "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }

        // 리스트 아이템 지우기
        Remove_list();
    }

    public void my_tab1()    //  내글 불러오기  (내글)
    {
        phptype = phptype_TalkMY;
        talk_type = "MY";
        appInfo = new AppInfo();

        // post 전달 인자
        Vector<NameValuePair> list = new Vector<NameValuePair>();
        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("type",talk_type));
        list.add(new BasicNameValuePair("deviceid",appInfo.Get_DeviceID()));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab1_TalkInselectURL();  // url 설정
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);

            downsever = new phpdown();  // 쓰레드 생성
            downsever.execute(request);
        }
        catch(Exception e)
        {
            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
            Toast.makeText(Main.MinContext, "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }

        // 리스트 아이템 지우기
        Remove_list();
    }

    // 글 삭제 하기
    public void delete_Talk(String device_id, String talk_idx)
    {
        phptype = phptype_TalkDELETE;
        appInfo = new AppInfo();

        // post 전달 인자
        Vector<NameValuePair> list = new Vector<NameValuePair>();
        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("talkidx",talk_idx));
        list.add(new BasicNameValuePair("deviceid",appInfo.Get_DeviceID()));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab1_TalkdeleteURL();  // url 설정
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);

            downsever = new phpdown();  // 쓰레드 생성
            downsever.execute(request);
        }
        catch(Exception e)
        {
            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
            Toast.makeText(Main.MinContext, "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }

        // 리스트 아이템 지우기
        Remove_list();
    }

    // 좋아요 하기
    public void like_talk(String talk_idx, String device_id_taget, String device_id_liker)
    {
        phptype = phptype_TalkLIKE;
        appInfo = new AppInfo();

        // post 전달 인자
        Vector<NameValuePair> list = new Vector<NameValuePair>();

        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("talk_idx",talk_idx));
        list.add(new BasicNameValuePair("talk_writeid",device_id_taget));
        list.add(new BasicNameValuePair("liker_deviceid",appInfo.Get_DeviceID()));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab1_TalklikeURL();  // url 설정
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);

            downsever = new phpdown();  // 쓰레드 생성
            downsever.execute(request);
        }
        catch(Exception e)
        {
            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
            Toast.makeText(Main.MinContext, "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }

        // 리스트 아이템 지우기
        //Remove_list();
    }




    // 통신

    private class phpdown extends AsyncTask<HttpPost, Integer, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(HttpPost... urls)
        {
            String returnData = "";
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = new HttpResponse()
            {
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
                public void setReasonPhrase(String s) throws IllegalStateException { }
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
                public void addHeader(Header header) { }
                @Override
                public void addHeader(String s, String s1) {}
                @Override
                public void setHeader(Header header) { }
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


            try
            {
                response = httpclient.execute(urls[0]);
            }
            catch (Exception e)
            {
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
        protected void onPostExecute(String str)
        {
            switch (phptype)
            {
                case phptype_TalkALL :  //전체보기
                {
                    // 보기 좋은 형태로 변수에 대입
                    String idx;
                    String talk_idx;
                    String talk_writeid;
                    String talk_img;
                    String talk_data;
                    String talk_likecount;
                    String talk_mentcount;
                    String talk_locationstate;
                    String talk_latitude;
                    String talk_longitude;
                    String talk_writetime;

                    String user_id;
                    String user_nickname;
                    String user_profile;

                    try {
                        JSONObject root = new JSONObject(str);

                        JSONArray ja = root.getJSONArray("result");

                        for (int i = 0; i < ja.length(); i++) {

                            JSONObject jo = ja.getJSONObject(i);
                            idx = jo.getString("idx");
                            talk_idx = jo.getString("talk_idx");
                            talk_writeid = jo.getString("talk_writeid");
                            talk_img = jo.getString("talk_img");
                            talk_data = jo.getString("talk_data");
                            talk_likecount = jo.getString("talk_likecount");
                            talk_mentcount = jo.getString("talk_mentcount");
                            talk_locationstate = jo.getString("talk_locationstate");
                            talk_latitude = jo.getString("talk_latitude");
                            talk_longitude = jo.getString("talk_longitude");
                            talk_writetime = jo.getString("talk_writetime");

                            user_id = jo.getString("user_id");
                            user_nickname = jo.getString("user_nickname");
                            user_profile = jo.getString("user_profile");

                            listItem.add(new TalkData(idx, talk_idx, talk_writeid, talk_img, talk_data, talk_likecount, talk_mentcount, talk_locationstate, talk_latitude, talk_longitude, talk_writetime, user_id, user_nickname, user_profile));
                        }
                        customAdapter = new CustomAdapter(Main.MinContext, R.id.list_item, listItem);
                        list.setAdapter(customAdapter);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    customAdapter.notifyDataSetChanged();
                    ((Main)Main.MinContext).StopShow();   // 다이얼로그 종료
                    break;
                }
                case phptype_TalkMY :  // 내글보기
                {
                    // 보기 좋은 형태로 변수에 대입
                    String idx;
                    String talk_idx;
                    String talk_writeid;
                    String talk_img;
                    String talk_data;
                    String talk_likecount;
                    String talk_mentcount;
                    String talk_locationstate;
                    String talk_latitude;
                    String talk_longitude;
                    String talk_writetime;

                    String user_id;
                    String user_nickname;
                    String user_profile;

                    try {
                        JSONObject root = new JSONObject(str);

                        JSONArray ja = root.getJSONArray("result");

                        for (int i = 0; i < ja.length(); i++) {

                            JSONObject jo = ja.getJSONObject(i);
                            idx = jo.getString("idx");
                            talk_idx = jo.getString("talk_idx");
                            talk_writeid = jo.getString("talk_writeid");
                            talk_img = jo.getString("talk_img");
                            talk_data = jo.getString("talk_data");
                            talk_likecount = jo.getString("talk_likecount");
                            talk_mentcount = jo.getString("talk_mentcount");
                            talk_locationstate = jo.getString("talk_locationstate");
                            talk_latitude = jo.getString("talk_latitude");
                            talk_longitude = jo.getString("talk_longitude");
                            talk_writetime = jo.getString("talk_writetime");

                            user_id = jo.getString("user_id");
                            user_nickname = jo.getString("user_nickname");
                            user_profile = jo.getString("user_profile");

                            listItem.add(new TalkData(idx, talk_idx, talk_writeid, talk_img, talk_data, talk_likecount, talk_mentcount, talk_locationstate, talk_latitude, talk_longitude, talk_writetime, user_id, user_nickname, user_profile));
                        }
                        customAdapter = new CustomAdapter(Main.MinContext, R.id.list_item, listItem);
                        list.setAdapter(customAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    customAdapter.notifyDataSetChanged();
                    AppInfo.onGcmUdate = false;
                    ((Main)Main.MinContext).StopShow();   // 다이얼로그 종료
                    break;
                }
                case phptype_TalkDELETE :  //삭제
                {
                    Log.i("str_del",str);
                    Log.i("Get_DeviceID",appInfo.Get_DeviceID());
                    if(str.toString().equals("SUCCESS"))
                    {
                        Toast.makeText(Main.MinContext,"해당 글이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                    else if(str.toString().equals("SEVERFAILED"))
                    {
                        Toast.makeText(Main.MinContext,"서버 접속이 불안정 합니다. 잠시후 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
                    }

                    if(talk_type.equals("ALL"))
                    {
                        all_tab1();
                    }
                    else
                    {
                        my_tab1();
                    }
                    customAdapter.notifyDataSetChanged();
                    ((Main)Main.MinContext).StopShow();   // 다이얼로그 종료
                    break;
                }
                case phptype_TalkLIKE:  //좋아요
                {
                    if(str.toString().equals("SUCCESS"))
                    {
                        int likenum = Integer.parseInt(listItem.get(tmpIndex).GET_talk_likecount()) +1;
                        listItem.get(tmpIndex).SET_talk_likecount(String.valueOf(likenum));
                       // tmpLikecount.setText(likenum);
                        customAdapter.notifyDataSetChanged();

                        Toast.makeText(Main.MinContext,"해당 글을 좋아합니다.",Toast.LENGTH_SHORT).show();
                    }
                    else if(str.toString().equals("OVERLAP"))
                    {
                        Toast.makeText(Main.MinContext,"이미 좋아 하셨잔아요...-_-",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(Main.MinContext,"서버 접속이 불안정 합니다. 잠시후 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    ((Main)Main.MinContext).StopShow();   // 다이얼로그 종료
                    break;
                }
            }

        }
    }



    // 리스트 어댑터 함수

    private class CustomAdapter extends ArrayAdapter<TalkData>
    {
        private LayoutInflater m_inflager = null;
        private ArrayList<TalkData> items;
        private int m_recource_id;
        ViewHolder holder;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<TalkData> objects)
        {
            super(context, textViewResourceId, objects);
            items = objects;
            m_recource_id = textViewResourceId;

            m_inflager = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate xml 에 씌여져 있는 view 의 정의를 실제 view 객체로 만드는 역할
            //inflate 를 사용하기 위해서는 우선 inflater 를 얻어와야 합니다.
            //LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // final View view;
            final TalkData data = items.get(position);
            final int index = position;


            if(convertView == null)
            {
                convertView = m_inflager.inflate(R.layout.item_tab1, null);

                holder = new ViewHolder();

                //  view 에서 얻어 초기화
                holder.View_img = (ImageView) convertView.findViewById(R.id.tab1_item_talkimg);
                holder.View_data = (TextView) convertView.findViewById(R.id.tab1_item_data);
                holder.View_likecount = (TextView) convertView.findViewById(R.id.tab1_item_likecount);
                holder.View_likesubmit = (LinearLayout) convertView.findViewById(R.id.tab1_item_like_submit);
                holder.View_mentcount = (TextView) convertView.findViewById(R.id.tab1_item_mentcount);
                holder.View_mentsubmit = (LinearLayout) convertView.findViewById(R.id.tab1_item_ment_submit);
                holder.View_loactionstate = (TextView) convertView.findViewById(R.id.tab1_item_loationstate);
                holder.View_loacaionsatatesubmit = (LinearLayout) convertView.findViewById(R.id.tab1_item_location_submit);
                holder.View_writetime = (TextView) convertView.findViewById(R.id.tab1_item_date);

                holder.View_user_nickname = (TextView) convertView.findViewById(R.id.tab1_item_nickname);
                holder.View_user_profile = (ImageView) convertView.findViewById(R.id.tab1_item_prfileimg);
                holder.View_delete = (LinearLayout) convertView.findViewById(R.id.tab1_item_delete);
                holder.View_fix = (LinearLayout) convertView.findViewById(R.id.tab1_item_fix);



                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if(data != null)
            {
                // 자랑하기 이미지 부분
                if(data.GET_talk_img().equals("none") || data.GET_talk_img() == "" || data.GET_talk_img() == null)
                {
                   // holder.View_img.setVisibility(View.INVISIBLE);
                }
                else
                {
                    // 이미지 있음
                   // holder.View_img.setVisibility(View.VISIBLE);

                     Glide.with(convertView.getContext()).load(data.GET_talk_img()).dontAnimate().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().thumbnail(0.5f).into(holder.View_img);

                    // 사진 클릭 리스너
                    holder.View_img.setOnClickListener(new ImageView.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            ((Main) Main.MinContext).View_ZoomImage(data.GET_talk_img());
                        }
                    });
                }
                // 자랑하기 글 부분
                holder.View_data.setText(data.GET_talk_data());

                // 자랑하기 좋아요 부분(클릭 이벤트 줘야함)
                if(data.GET_talk_likecount().equals("0"))
                {
                    //좋아요 레이아웃 셋팅
                    String strColor1 = "#777777";
                    holder.View_likecount.setText("-");
                    holder.View_likecount.setTextColor(Color.parseColor(strColor1));
                }
                else
                {
                    String strColor2 = "#fa0175";
                    holder.View_likecount.setText(data.GET_talk_likecount());
                    holder.View_likecount.setTextColor(Color.parseColor(strColor2));
                }

                // 좋아요 + 버튼 리스너
                holder.View_likesubmit.setOnClickListener(new LinearLayout.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // tmpLikecount = holder.View_likecount;
                        tmpIndex = index;
                        like_talk(data.GET_talk_idx(), data.GET_user_id(), appInfo.Get_DeviceID());

                    }
                });

                // 자랑하기 멘트 부분(클릭 이벤트 줘야함)
                if(data.GET_talk_mentcount().equals("0"))
                {
                    //좋아요 레이아웃 셋팅
                    String strColor1 = "#777777";
                    holder.View_mentcount.setText("-");
                    holder.View_mentcount.setTextColor(Color.parseColor(strColor1));

                    // 자랑하기 멘트 클릭 (멘트 없을때)
                    holder.View_mentsubmit.setOnClickListener(new LinearLayout.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            //멘트 없음 클릭시 토스트
                            Toast.makeText(Main.MinContext,"해당글은 인기가 없어서 그런지.. 댓글이 없습니다.",Toast.LENGTH_LONG).show();
                            ((Main) Main.MinContext).tab1_ment_start(true,data.GET_talk_idx(), data.GET_talk_idx(), data.GET_talk_writeid());
                        }
                    });
                }
                else
                {
                    String strColor2 = "#fa0175";
                    holder.View_mentcount.setText(data.GET_talk_mentcount());
                    holder.View_mentcount.setTextColor(Color.parseColor(strColor2));

                    // 자랑하기 멘트 클릭 (멘트 있을때)
                    holder.View_mentsubmit.setOnClickListener(new LinearLayout.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            ((Main) Main.MinContext).tab1_ment_start(true,data.GET_talk_idx(), data.GET_talk_idx(), data.GET_talk_writeid());
                        }
                    });
                }


                // 자랑하기 위치 부분(클릭 이벤트 줘야함) 공개 / 비공개
                if(data.GET_talk_locationstate().equals("none") || data.GET_talk_img() == "" || data.GET_talk_img() == null)
                {
                    holder.View_loactionstate.setText("비공개");
                    String strColor1 = "#777777";
                    holder.View_loactionstate.setTextColor(Color.parseColor(strColor1));

                    // 위치 부분 클릭 리스너
                    holder.View_loacaionsatatesubmit.setOnClickListener(new LinearLayout.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            //위치 비공개 클릭시 토스트
                            Toast.makeText(Main.MinContext,"위치 정보가 기록되어 있지 않습니다.",Toast.LENGTH_LONG).show();
                        }

                    });
                }
                else
                {
                    String strColor2 = "#fa0175";
                    holder.View_loactionstate.setText("공개함");
                    holder.View_loactionstate.setTextColor(Color.parseColor(strColor2));

                    // 위치 부분 클릭 리스너
                    holder.View_loacaionsatatesubmit.setOnClickListener(new LinearLayout.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            //위치 공개 클릭시 Main tab1_loaction_start 함수
                            ((Main) Main.MinContext).tab1_loaction_start(data.GET_talk_locationstate(),data.GET_talk_latitude(),data.GET_talk_longitude());
                        }

                    });
                }



                // 자랑하기 작성시간 부분
                holder.View_writetime.setText(data.GET_talk_writetime());
                // 자랑하기 작성자 닉네임 부분
                holder.View_user_nickname.setText(data.GET_user_nickname());
                // 자랑하기 작성자 프로필 사진 부분(클릭이벤트 필요)

                if(data.GET_user_profile().equals("none"))
                {

                    Glide.with(convertView.getContext()).load(R.drawable.profile_default).dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).bitmapTransform(new CropCircleTransformation(convertView.getContext())).thumbnail(0.1f).into(holder.View_user_profile);

                        // 사진 클릭 리스너(사진 없음)
                    holder.View_user_profile.setOnClickListener(new ImageView.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Toast.makeText(Main.MinContext,"프로필 사진이 없습니다.",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    // 이미지 있음

                    Glide.with(convertView.getContext()).load(data.GET_user_profile()).dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).bitmapTransform(new CropCircleTransformation(convertView.getContext())).thumbnail(0.1f).into(holder.View_user_profile);

                        // 사진 클릭 리스너(사진 있음)
                    holder.View_user_profile.setOnClickListener(new ImageView.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                           ((Main) Main.MinContext).View_ZoomImage(data.GET_user_profile());
                        }
                    });
                }

                // 삭제 버튼 (자신 글만 삭제글)
                if(data.GET_talk_writeid().toString().equals(appInfo.Get_DeviceID()))
                {
                    holder.View_delete.setVisibility(View.VISIBLE);
                    holder.View_fix.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.View_delete.setVisibility(View.GONE);
                    holder.View_fix.setVisibility(View.GONE);
                }

                // 삭제버튼 클릭 리스너
                holder.View_delete.setOnClickListener(new LinearLayout.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        new AlertDialog.Builder(Main.MinContext)
                                .setMessage("해당 글을 하시겠습니까?")
                                .setNegativeButton("네", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        delete_Talk(data.GET_user_id(),data.GET_talk_idx());
                                    }

                                })
                        .setPositiveButton("아니요",null)
                        .show();
                    }
                });

               // customAdapter.notifyDataSetChanged();
            }


            return convertView;

        }

       public class ViewHolder
        {
            ImageView View_img;
            TextView View_data;
            TextView View_likecount;
            LinearLayout View_likesubmit;
            TextView View_mentcount;
            LinearLayout View_mentsubmit;
            TextView View_loactionstate;
            LinearLayout View_loacaionsatatesubmit;
            TextView View_writetime;
            LinearLayout View_delete;
            LinearLayout View_fix;

            TextView View_user_nickname;
            ImageView View_user_profile;

        }
    }

    public void Remove_list()
    {
        listItem.clear();
        last_list_number = 0;
    }


}
