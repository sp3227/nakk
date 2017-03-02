package com.moduse.nakk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import static com.moduse.nakk.R.layout.tab4_sponsorlist;

/**
 * Created by sejung on 2017-02-27.
 */

public class Tab4_sponsorlist extends Activity
{

    ProgressDialog loading;

    // 앱 데이터
    AppInfo appInfo;

    //쓰레드
    phpdown downsever;

    // 담을 데이터 설정
    ArrayList<SponsorData> listItem = new ArrayList<SponsorData>();
    private ListView list = null;
    private CustomAdapter customAdapter = null;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(tab4_sponsorlist);

        loading = new ProgressDialog(this);
        InitShow();

        appInfo = new AppInfo();  // 앱 데이터 설정

        // 레이아웃 초기화
        list = (ListView) findViewById(R.id.tab4_sponsor_listview);

        //불러오기 후원자
        sponsorinit();
    }


    // 후원자 리스트 불러오기 함수
    public void sponsorinit()
    {
        StartShow();  // 다이얼로그 시작

        //post 인자값 전달
        Vector<NameValuePair> list = new Vector<NameValuePair>();

        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("type", "sponsortype"));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab4_sponsorlist_select();  // url 설정
            HttpPost request = new HttpPost(url);
            request.setEntity(entity);

            downsever = new phpdown();  // 쓰레드 생성
            downsever.execute(request);
        }
        catch(Exception e)
        {
            // 서버에 연결할 수 없습니다 토스트 메세지 보내기
            Toast.makeText(this.getApplicationContext(), "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
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

            Log.i("sponsor_str",str);
            // 보기 좋은 형태로 변수에 대입
            String idx__;

            String loginID__;
            String deviceID__;

            String user_nickname__;
            String money__;
            String recipe__;

            String token__;
            String sponsortime__;

            if (str.toString().equals("CHARFALSE")) {
                Toast.makeText(getApplicationContext(), "서버 접속이 불안정 합니다. 잠시후 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                finish();

            } else if (str.toString().equals("sql_error")) {
                Toast.makeText(getApplicationContext(), "서버 접속이 불안정 합니다. 잠시후 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject root = new JSONObject(str);

                    JSONArray ja = root.getJSONArray("result");

                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jo = ja.getJSONObject(i);
                        idx__ = jo.getString("idx");
                        loginID__ = jo.getString("loginID");
                        deviceID__ = jo.getString("deviceID");
                        user_nickname__ = jo.getString("user_nickname");
                        money__ = jo.getString("money");
                        recipe__ = jo.getString("recipe");
                        token__ = jo.getString("token");
                        sponsortime__ = jo.getString("sponsortime");

                        listItem.add(new SponsorData(idx__, loginID__, deviceID__, user_nickname__, money__, recipe__, token__, sponsortime__));
                    }
                    customAdapter = new CustomAdapter(getApplicationContext(), R.id.list_item, listItem);
                    list.setAdapter(customAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                customAdapter.notifyDataSetChanged();
                StopShow();    // 다이얼로그 종료


            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////// 리스트 어댑터 함수

    private class CustomAdapter extends ArrayAdapter<SponsorData>
    {
        private LayoutInflater m_inflager = null;
        private ArrayList<SponsorData> items;
        private int m_recource_id;
        ViewHolder holder;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<SponsorData> objects)
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
            final SponsorData data = items.get(position);
            final int index = position;

            if(convertView == null)
            {
                convertView = m_inflager.inflate(R.layout.item_sponsor, null);

                holder = new ViewHolder();

                //  view 에서 얻어 초기화
                holder.View_time = (TextView) convertView.findViewById(R.id.tab4_sponsorlist_time);
                holder.View_nickname = (TextView) convertView.findViewById(R.id.tab4_sponsorlist_nickname);
                holder.View_money = (TextView) convertView.findViewById(R.id.tab4_sponsorlist_money);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if(data != null)
            {

                //시간
                holder.View_time.setText(data.GET_sponsortime()+" ");

                //닉네임
                holder.View_nickname.setText(data.GET_user_nickname()+" ");

                //금액
                holder.View_money.setText(data.GET_money()+"원 후원 ");
            }


            return convertView;

        }

        public class ViewHolder
        {
            TextView        View_time;
            TextView        View_nickname;
            TextView        View_money;
        }
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
