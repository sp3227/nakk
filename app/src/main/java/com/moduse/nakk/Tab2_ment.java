package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
 * Created by sejung on 2017-03-07.
 */

public class Tab2_ment extends Activity
{
    AppInfo appInfo;  // 앱 정보 선언

    // 프로그레스 설정
    ProgressDialog loading;

    //쓰레드
    phpdown downsever;

    // 타입상수 설정
    final int MENT_LOAD = 0;
    final int MENT_ADD = 1;
    final int MENT_DELETE = 2;
    int phptype;

    // 담을 데이터 설정
    ArrayList<Tab2_mentdata> listItem = new ArrayList<Tab2_mentdata>();
    private ListView list = null;
    private CustomAdapter customAdapter = null;

    // talk에서 넘어오는 값들
    private Intent talkintent;
    String type;

    //멘터 데이터 가져오기
    String put_loginID;  //작성자 디바이스 아이디
    String put_deviceID;  //작성자 디바이스 아이디
    String input_data;  // 작성 내용

    String Point_ment_idx;  // 멘트 식별 ID
    String ment_idx = null;

    //레이아웃
    EditText editText_ment;
    LinearLayout btn_submit;

    //키보드
    InputMethodManager imm;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2_ment);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);
        InitShow();
        context = this;

        // 레이아웃 초기화
        editText_ment = (EditText) findViewById(R.id.tab2_ment_edit);
        btn_submit = (LinearLayout) findViewById(R.id.tab2_ment_submit);
        list = (ListView) findViewById(R.id.tab2_mentlist);

        //인텐트에서 넘어온 값들
        Intent intent = getIntent();

        Point_ment_idx= intent.getStringExtra("point_idx");
        put_loginID= intent.getStringExtra("menter_loginID");
        put_deviceID= intent.getStringExtra("menter_deviceID");


        //키보드 내리기
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        Load_ment();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////// 버튼 설정
    // 멘트 등록
    public void tab2_btn_ment_submit(View v)
    {
        Load_add();
        imm.hideSoftInputFromWindow(editText_ment.getWindowToken(),0);
        editText_ment.setText("");
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////// 통신 함수

    // 멘트 불러오기 함수
    public void Load_ment()
    {
        StartShow();  // 다이얼로그 시작
        phptype = MENT_LOAD;

        //post 인자값 전달
        Vector<NameValuePair> list = new Vector<NameValuePair>();

        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("pointidx",Point_ment_idx));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab2_PointmentselectURL();  // url 설정
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

    // 멘트 추가하기 함수
    public void Load_add()
    {
        if(!editText_ment.getText().toString().equals(""))
        {
            StartShow();  // 다이얼로그 시작
            phptype = MENT_ADD;

            input_data = editText_ment.getText().toString();

            //post 인자값 전달
            Vector<NameValuePair> list = new Vector<NameValuePair>();

            //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
            list.add(new BasicNameValuePair("pointidx", Point_ment_idx));
            list.add(new BasicNameValuePair("writeloginID", put_loginID));
            list.add(new BasicNameValuePair("writedeviceID", put_deviceID));
            list.add(new BasicNameValuePair("menterloginID", AppInfo.MY_LOGINID));
            list.add(new BasicNameValuePair("menterdeviceID", AppInfo.MY_DEVICEID));
            list.add(new BasicNameValuePair("mentdata", input_data));

            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
                String url = appInfo.Get_Tab2_PointmentaddURL();  // url 설정
                HttpPost request = new HttpPost(url);
                request.setEntity(entity);

                downsever = new phpdown();  // 쓰레드 생성
                downsever.execute(request);
            } catch (Exception e) {
                // 서버에 연결할 수 없습니다 토스트 메세지 보내기
                Toast.makeText(this.getApplicationContext(), "서버접속이 불안정합니다. 인터넷 환경을 확인해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("Exception Error", e.toString());
            }
            input_data = "";
        }
        else
        {
            Toast.makeText(this.getApplicationContext(), "입력한 멘트가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 멘트 삭제하기 함수
    public void delete_ment()
    {
        StartShow();  // 다이얼로그 시작
        phptype = MENT_DELETE;

        //post 인자값 전달
        Vector<NameValuePair> list = new Vector<NameValuePair>();

        //여기에 전달할 인자를 담는다. String으로 넣는것이 안전하다.
        list.add(new BasicNameValuePair("pointidx",Point_ment_idx));
        list.add(new BasicNameValuePair("idx",ment_idx));  //어탭터에서 받아야함
        list.add(new BasicNameValuePair("menterid",AppInfo.MY_LOGINID));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, "UTF-8");
            String url = appInfo.Get_Tab2_PointmentdeleteURL();  // url 설정
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

        Remove_list();
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////통신부분

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
            Log.i("MENT_STR",str);
            switch (phptype)
            {
                case MENT_LOAD :  //멘트 불러오기
                {
                    // 보기 좋은 형태로 변수에 대입
                    String idx_;
                    String point_idx_;
                    String write_loginID_;
                    String write_deviceID_;
                    String menter_loginID_;
                    String menter_deviceID_;
                    String user_nickname_;
                    String user_proimg_;
                    String ment_addtime_;
                    String ment_data_;
                    String user_sponsor_;

                    if(str.toString().equals("CHARFALSE"))
                    {
                        Toast.makeText(getApplicationContext(),"서버 접속이 불안정 합니다. 잠시후 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    }
                    else {
                        try {
                            JSONObject root = new JSONObject(str);

                            JSONArray ja = root.getJSONArray("result");

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jo = ja.getJSONObject(i);
                                idx_ = jo.getString("idx");
                                point_idx_ = jo.getString("point_idx");
                                write_loginID_ = jo.getString("write_loginID");
                                write_deviceID_ = jo.getString("write_deviceID");
                                menter_loginID_ = jo.getString("menter_loginID");
                                menter_deviceID_ = jo.getString("menter_deviceID");
                                user_nickname_ = jo.getString("user_nickname");
                                user_proimg_ = jo.getString("user_proimg");
                                ment_addtime_  = jo.getString("ment_addtime");
                                ment_data_  = jo.getString("ment_data");
                                user_sponsor_  = jo.getString("user_sponsor");

                                listItem.add(new Tab2_mentdata(idx_, point_idx_, write_loginID_, write_deviceID_, menter_loginID_, menter_deviceID_, user_nickname_, user_proimg_, ment_addtime_,ment_data_,user_sponsor_));
                            }
                            customAdapter = new CustomAdapter(getApplicationContext(), R.id.list_item, listItem);
                            list.setAdapter(customAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        customAdapter.notifyDataSetChanged();
                        StopShow();    // 다이얼로그 종료
                        break;
                    }
                }
                case MENT_ADD :  //멘트 추가
                {
                    if(str.toString().equals("SUCCESS"))
                    {
                        Remove_list();
                        Toast.makeText(getApplicationContext(),"그대에게 어복이 있기를...",Toast.LENGTH_SHORT).show();
                        Load_ment();  // 멘트 다시 불러오기
                    }
                    else if(str.toString().equals("PUSHFALSE"))
                    {
                        Remove_list();
                        Toast.makeText(getApplicationContext(),"푸시는 전송 안됨",Toast.LENGTH_SHORT).show();
                        Load_ment();  // 멘트 다시 불러오기
                    }
                    else if(str.toString().equals("CHARNULL"))
                    {
                        Toast.makeText(getApplicationContext(),"입력된 값이 없습니다.",Toast.LENGTH_SHORT).show();
                    }
                    else if(str.toString().equals("FAILURE"))
                    {
                        Toast.makeText(getApplicationContext(),"인터넷 환경이 불안정합니다. 잠시후 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"인터넷 환경이 불안정합니다. 잠시후 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
                    }

                    //customAdapter.notifyDataSetChanged();
                    StopShow();    // 다이얼로그 종료
                    break;
                }
                case MENT_DELETE :  //멘트 삭제
                {
                    if(str.toString().equals("SUCCESS"))
                    {
                        Toast.makeText(getApplicationContext(),"해당 글이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    }
                    else if(str.toString().equals("SEVERFAILED"))
                    {
                        Toast.makeText(getApplicationContext(),"서버 접속이 불안정 합니다. 잠시후 다시 시도 해주세요.",Toast.LENGTH_SHORT).show();
                    }

                    Load_ment();  // 멘트 다시 불러오기
                    customAdapter.notifyDataSetChanged();
                    StopShow();   // 다이얼로그 종료
                    break;
                }

            }

        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 리스트 어탭터

    private class CustomAdapter extends ArrayAdapter<Tab2_mentdata>
    {
        private LayoutInflater m_inflager = null;
        private ArrayList<Tab2_mentdata> items;
        private int m_recource_id;
        CustomAdapter.ViewHolder holder;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Tab2_mentdata> objects)
        {
            super(context, textViewResourceId, objects);
            items = objects;
            m_recource_id = textViewResourceId;
            Log.i("put_idx :","true");
            m_inflager = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate xml 에 씌여져 있는 view 의 정의를 실제 view 객체로 만드는 역할
            //inflate 를 사용하기 위해서는 우선 inflater 를 얻어와야 합니다.
            //LayoutInflater inflater = (LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // final View view;
            final Tab2_mentdata data = items.get(position);
            final int index = position;

            if(convertView == null)
            {
                convertView = m_inflager.inflate(R.layout.item_ment, null);

                holder = new ViewHolder();

                //  view 에서 얻어 초기화
                holder.View_img = (ImageView) convertView.findViewById(R.id.tab1_ment_proimg);
                holder.View_nickname = (TextView) convertView.findViewById(R.id.tab1_ment_nickname);
                holder.View_user_sponsor = (ImageView) convertView.findViewById(R.id.tab1_ment_sponsor);
                holder.View_date = (TextView) convertView.findViewById(R.id.tab1_ment_date);
                holder.View_data = (TextView) convertView.findViewById(R.id.tab1_ment_data);
                holder.View_btn_delete = (LinearLayout) convertView.findViewById(R.id.tab1_ment_btn_delete);

                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if(data != null)
            {
                //프로필 이미지 부분
                if(data.GET_user_proimg().equals("none"))
                {
                    // 프로필 이미지가 없을때
                    Glide.with(convertView.getContext()).load(R.drawable.profile_default).dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).bitmapTransform(new CropCircleTransformation(convertView.getContext())).thumbnail(0.1f).into(holder.View_img);

                    // 사진 클릭 리스너(사진 없음)
                    holder.View_img.setOnClickListener(new ImageView.OnClickListener()
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
                    Glide.with(convertView.getContext()).load(data.GET_user_proimg()).dontAnimate().centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).bitmapTransform(new CropCircleTransformation(convertView.getContext())).thumbnail(0.1f).into(holder.View_img);
                    // 사진 클릭 리스너
                    holder.View_img.setOnClickListener(new ImageView.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            ((Main) Main.MinContext).View_ZoomImage(data.GET_user_proimg());
                        }
                    });
                }

                // 닉네임 부분
                holder.View_nickname.setText(data.GET_user_nickname()+" ");

                // 스폰서 부분
                if(data.GET_user_sponsor().toString().equals("true"))
                {
                    holder.View_user_sponsor.setVisibility(View.VISIBLE);
                }
                else
                {
                    holder.View_user_sponsor.setVisibility(View.GONE);
                }

                // 작성날짜 부분
                holder.View_date.setText(data.GET_ment_addtime());

                // 작성 내용 부분
                holder.View_data.setText(data.GET_ment_data());

                //삭제 버튼 부분
                if(data.GET_menter_loginID().equals(AppInfo.MY_LOGINID))
                {
                    holder.View_btn_delete.setVisibility(View.VISIBLE);

                    // 삭제 클릭리스너 다이얼로그
                    holder.View_btn_delete.setOnClickListener(new LinearLayout.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            new AlertDialog.Builder(context)
                                    .setMessage("해당 댓글 삭제을 하시겠습니까?")
                                    .setNegativeButton("네", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            ment_idx = data.GET_idx();
                                            delete_ment();
                                        }

                                    })
                                    .setPositiveButton("아니요",null)
                                    .show();
                        }
                    });
                }
                else
                {
                    holder.View_btn_delete.setVisibility(View.INVISIBLE);
                }
            }


            return convertView;

        }

        public class ViewHolder
        {
            ImageView       View_img;
            TextView        View_nickname;
            ImageView       View_user_sponsor;
            TextView        View_date;
            TextView        View_data;
            LinearLayout    View_btn_delete;
        }
    }



    // 뒤로가기 (댓글창 닫기)

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("fix_type", "true");
        setResult(2, intent);

        finish();

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


    public void Remove_list()
    {
        listItem.clear();
    }
}
