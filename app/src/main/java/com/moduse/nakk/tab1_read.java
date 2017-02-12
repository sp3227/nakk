package com.moduse.nakk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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



    Tab1_read()
    {
        Inflater = ((Main) Main.MinContext).getLayoutInflater();
        Inflater = (LayoutInflater) ((Main) Main.MinContext).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        in_layout = (LinearLayout) Inflater.inflate(R.layout.tab1, null);

        list = (ListView) in_layout.findViewById(R.id.tab1_listview);  // 리스트 레이아웃 부분 설정
    }

    public void init_tab1()
    {
        appInfo = new AppInfo();
        downsever = new phpdown();  // 쓰레드 생성

        downsever.execute(appInfo.Get_Tab1_TalkInselectURL());  // URL 삽입

        // 받은 데이터 초기화  (일단 무시)
        listItem.clear();
    }


    // 통신

    private class phpdown extends AsyncTask<String, Integer,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls)
        {
            StringBuilder josnHtml = new StringBuilder();

            try
            {
                // 연결 URL 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                // 연결되었으면
                if(conn != null)
                {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    //연결 되었음 코드가 리턴되면.
                    if(conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                        for(;;)
                        {
                            // 웹상에 보여지는 텍스트 라인 단위로 읽어 저장
                            String line = br.readLine();
                            if(line == null) break;  // 라인 널이면 정지
                            //저장된 텍스트 라인을 josnHtml에 붙여넣음
                            josnHtml.append(line+ "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return josnHtml.toString();
        }

        @Override
        protected void onPostExecute(String str)
        {
            // 보기 좋은 형태로 변수에 대입
            String idx;
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

            try
            {
                JSONObject root = new JSONObject(str);

                JSONArray ja = root.getJSONArray("result");

                for(int i=0; i<ja.length(); i++)
                {

                    JSONObject jo = ja.getJSONObject(i);
                    idx = jo.getString("idx");
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


                    listItem.add(new TalkData(idx,talk_writeid,talk_img,talk_data,talk_likecount,talk_mentcount,talk_locationstate,talk_latitude,talk_longitude,talk_writetime,user_id,user_nickname,user_profile));
                }
                customAdapter = new CustomAdapter((Main)Main.MinContext,R.id.list_item,listItem);
                list.setAdapter(customAdapter);

            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

            ((Main)Main.MinContext).StopShow();   // 다이얼로그 종료
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


            if(convertView == null)
            {
                convertView = m_inflager.inflate(R.layout.item_tab1, null);

                holder = new ViewHolder();

                //  view 에서 얻어 초기화
                holder.View_img = (ImageView) convertView.findViewById(R.id.tab1_item_talkimg);
                holder.View_data = (TextView) convertView.findViewById(R.id.tab1_item_data);
                holder.View_likecount = (TextView) convertView.findViewById(R.id.tab1_item_likecount);
                holder.View_mentcount = (TextView) convertView.findViewById(R.id.tab1_item_mentcount);
                holder.View_loactionstate = (TextView) convertView.findViewById(R.id.tab1_item_loationstate);
                holder.View_writetime = (TextView) convertView.findViewById(R.id.tab1_item_date);

                holder.View_user_nickname = (TextView) convertView.findViewById(R.id.tab1_item_nickname);
                holder.View_user_profile = (ImageView) convertView.findViewById(R.id.tab1_item_prfileimg);
                holder.View_delete = (LinearLayout) convertView.findViewById(R.id.tab1_item_delete);

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
                    Glide.with(convertView.getContext()).load(data.GET_talk_img()).diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(holder.View_img);
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
                holder.View_likecount.setText(data.GET_talk_likecount());
                // 자랑하기 멘트 부분(클릭 이벤트 줘야함)
                holder.View_mentcount.setText(data.GET_talk_mentcount());
                // 자랑하기 위치 부분(클릭 이벤트 줘야함) 공개 / 비공개
                if(data.GET_talk_locationstate().equals("none") || data.GET_talk_img() == "" || data.GET_talk_img() == null)
                {
                    holder.View_loactionstate.setText("비공개");
                    String strColor1 = "#777777";
                    holder.View_loactionstate.setTextColor(Color.parseColor(strColor1));
                }
                else
                {
                    String strColor2 = "#fa0175";
                    holder.View_loactionstate.setText("공개");
                    holder.View_loactionstate.setTextColor(Color.parseColor(strColor2));
                }
                // 자랑하기 작성시간 부분
                holder.View_writetime.setText(data.GET_talk_writetime());
                // 자랑하기 작성자 닉네임 부분
                holder.View_user_nickname.setText(data.GET_user_nickname());
                // 자랑하기 작성자 프로필 사진 부분(클릭이벤트 필요)
                if(data.GET_user_profile().equals("none") || data.GET_user_profile() == "" || data.GET_user_profile() == null)
                {
                    Glide.with(convertView.getContext()).load(R.drawable.textimg).centerCrop().bitmapTransform(new CropCircleTransformation(convertView.getContext())).thumbnail(0.1f).into(holder.View_user_profile);
                }
                else
                {
                    // 이미지 있음
                    Glide.with(convertView.getContext()).load(data.GET_user_profile()).centerCrop().bitmapTransform(new CropCircleTransformation(convertView.getContext())).thumbnail(0.1f).into(holder.View_user_profile);
                    // 사진 클릭 리스너
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
                if(data.GET_talk_writeid().toString().equals(((Main) Main.MinContext).Get_DeviceID()))
                {
                    holder.View_delete.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.View_delete.setVisibility(View.GONE);
                }
                Log.i("user_device",((Main) Main.MinContext).Get_DeviceID());
                customAdapter.notifyDataSetChanged();
            }


            return convertView;

        }

        class ViewHolder
        {
            ImageView View_img;
            TextView View_data;
            TextView View_likecount;
            TextView View_mentcount;
            TextView View_loactionstate;
            TextView View_writetime;
            LinearLayout View_delete;

            TextView View_user_nickname;
            ImageView View_user_profile;
        }

    }

}
