package com.moduse.nakk;

import android.app.Activity;
import android.content.Context;
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
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sejung on 2017-02-11.
 */

public class Tab3_read extends Activity {

    public LinearLayout in_layout;
    public LayoutInflater Inflater;
    LinearLayout.LayoutParams layoutParams;

    AppInfo appInfo;  // 앱 정보 선언
    phpdown downsever;  // 쓰레드

    ArrayList<YoutubeData> listItem = new ArrayList<YoutubeData>();
    private ListView list = null;
    private CustomAdapter customAdapter = null;

    Tab3_read() {
        Inflater = ((Main) Main.MinContext).getLayoutInflater();
        Inflater = (LayoutInflater) Main.MinContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        in_layout = (LinearLayout) Inflater.inflate(R.layout.tab3, null);

        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        in_layout.setLayoutParams(layoutParams);

        list = (ListView) in_layout.findViewById(R.id.tab3_listview);  // 리스트 레이아웃 부분 설정

        init_tab3();
    }

    public void init_tab3() {
        appInfo = new AppInfo();
        downsever = new phpdown();

        // 통신
        try {
            String url = appInfo.Get_Tab3_YoutubeURL();  // url 설정
            HttpPost request = new HttpPost(url);
            downsever.execute(request);
        } catch (Exception e) {
            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
            Toast.makeText(Main.MinContext, "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }


    // 통신

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
            String idx;
            String title;
            String img;
            String data;
            String movURL;
            String uploadtime;

            try {
                JSONObject root = new JSONObject(str);

                JSONArray ja = root.getJSONArray("result");

                for (int i = 0; i < ja.length(); i++) {

                    JSONObject jo = ja.getJSONObject(i);
                    idx = jo.getString("idx");
                    img = jo.getString("imgurl");
                    title = jo.getString("title");
                    data = jo.getString("data");
                    movURL = jo.getString("movURL");
                    uploadtime = jo.getString("uploadtime");


                    listItem.add(new YoutubeData(idx, title, img, data, movURL, uploadtime));
                }
                customAdapter = new CustomAdapter(Main.MinContext, R.id.list_item, listItem);
                list.setAdapter(customAdapter);

            } catch (JSONException e)
            {
                e.printStackTrace();
            }

            customAdapter.notifyDataSetChanged();
            ((Main) Main.MinContext).StopShow();   // 다이얼로그 종료

        }
    }


    // 리스트 어댑터 함수

    private class CustomAdapter extends ArrayAdapter<YoutubeData> {
        private LayoutInflater m_inflager = null;
        private ArrayList<YoutubeData> items;
        private int m_recource_id;
        ViewHolder holder;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<YoutubeData> objects) {
            super(context, textViewResourceId, objects);
            items = objects;
            m_recource_id = textViewResourceId;

            m_inflager = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate xml 에 씌여져 있는 view 의 정의를 실제 view 객체로 만드는 역할
            //inflate 를 사용하기 위해서는 우선 inflater 를 얻어와야 합니다.
            //LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // final View view;
            final YoutubeData data = items.get(position);
            final int index = position;

            if (convertView == null) {
                convertView = m_inflager.inflate(R.layout.item_tab3, null);

                holder = new ViewHolder();

                //  view 에서 얻어 초기화
                holder.View_time = (TextView) convertView.findViewById(R.id.tab3_item_time);
                holder.View_title = (TextView) convertView.findViewById(R.id.tab3_item_title);
                holder.View_img = (ImageView) convertView.findViewById(R.id.tab3_item_img);
                holder.View_data = (TextView) convertView.findViewById(R.id.tab3_item_data);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (data != null) {

                //세팅창
                // 올린시간
                holder.View_time.setText(data.GET_uploadtime());
                //제목
                holder.View_title.setText(data.GET_title());

                // 이미지
                Glide.with(convertView.getContext()).load(data.GET_imgurl()).diskCacheStrategy(DiskCacheStrategy.RESULT).crossFade().thumbnail(0.5f).into(holder.View_img);
                // 영상 클릭 리스너
                holder.View_img.setOnClickListener(new ImageView.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //유튜브 연결 API
                        ((Main) Main.MinContext).YoutubePlay(data.GET_movURL());
                    }
                });

                // 내용
                holder.View_data.setText(data.GET_data());


               // customAdapter.notifyDataSetChanged();
            }


            return convertView;

        }

        public class ViewHolder
        {
            TextView View_time;
            TextView View_title;
            ImageView View_img;
            TextView View_data;
        }

    }
}

