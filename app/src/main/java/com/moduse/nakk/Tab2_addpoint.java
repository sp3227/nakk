package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by HOMESJ on 2017-03-05.
 */

public class Tab2_addpoint extends Activity
{


    //데이터
    AppInfo appInfo;

    // 테스크
    phpdown task;

    //프로그레스
    ProgressDialog loading;

    //레이아웃
    RadioButton option_open;
    RadioButton option_closed;
    ImageView   addimg;
    TextView    adddate;
    TextView    add_address;
    EditText    adddatafield;
    EditText    adddatapreparation;
    EditText    adddataetc;
    LinearLayout btn_submit;

    // 데이터를 담을 해시 맵
    HashMap<String,Object> pointdata;
    String UploadPoint_idx = null;

    //수정 식별값
    String FixPoint_idx = null;


    // 인텐트 데이터
    String phptype;
    Double add_latitude;
    Double add_longitude;
    String add_adress;

    // 받을데이터
    String Select_type = "open";      // 선택 (공개, 나만의)
    String edit_adddate = null;
    String edit_adddatafield = null;
    String edit_adddatapreparation = null;
    String edit_adddataetc = null;

    // 다이얼로그 상수
    final int DIALOG_DATE = 0;
    final int CAMERA = 1;


    // 이미지 부분
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_iMAGE = 2;

    Bitmap photo;
    String photo_path;
    Uri mImageCaptureUri;
    private int id_view;
    private String absoultePath;
    private byte[] imgbyte = null;

    //이미지 업로드 부분
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    URL connectUrl = null;

    private AndroidUploader upload;
    private FileInputStream mFileInputStream = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2_write);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);


        InitShow();

        // 레이아웃 설정
        option_open = (RadioButton) findViewById(R.id.point_open);
        option_closed = (RadioButton) findViewById(R.id.point_closed);
        addimg = (ImageView) findViewById(R.id.tab2_addimg);
        adddate = (TextView) findViewById(R.id.tab2_adddate);
        add_address = (TextView) findViewById(R.id.tab2_add_address);
        adddatafield = (EditText) findViewById(R.id.tab2_add_datafield);
        adddatapreparation = (EditText) findViewById(R.id.tab2_add_datapreparation);
        adddataetc = (EditText) findViewById(R.id.tab2_add_dataetc);
        btn_submit = (LinearLayout) findViewById(R.id.tab2_point_add_submit);


        InitAddPoint();
        tab2_btn_init(); // 버튼 리스너 (올리기)

    }

    /////////////////////////////////////////////////////////////////////////// 글쓰기 초기화 (등록, 수정)
    public void InitAddPoint()
    {
        Intent intent = getIntent();

        if(intent.getStringExtra("point_type").toString().equals("ADD"))
        {
            option_open.setChecked(true);
            phptype = intent.getStringExtra("point_type");
            add_latitude = intent.getDoubleExtra("point_latitude",0);
            add_longitude = intent.getDoubleExtra("point_longitude",0);
            add_adress = intent.getStringExtra("point_address");

            Glide.with(this).load(R.drawable.write_talkimg).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(addimg);
            add_address.setText(add_adress);
        }
        else if(intent.getStringExtra("point_type").toString().equals("FIX"))
        {
            pointdata = new HashMap<String, Object>();  // 기존 프로필 데이터를 넣을 리스트 선언

            phptype = intent.getStringExtra("point_type");
            FixPoint_idx = intent.getStringExtra("point_idx");

            PHP_LOAD_POINT();
        }

        /// 레디오 버튼 세팅
        option_open.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Select_type = "open";
                Toast.makeText(getApplicationContext(),"'공개'는 모든 사람들에게 보여집니다.(낚시발전의 기여자!)",Toast.LENGTH_SHORT).show();
            }
        });

        option_closed.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Select_type = "closed";
                Toast.makeText(getApplicationContext(),"'나만의'는 나에게만 보여집니다.(님아 공유좀 ㅠ)",Toast.LENGTH_SHORT).show();
            }
        });

    }


    ////////////////////////////////////////////////////////////////////////// 이미지 선택
    public void tab2_btn_selectimg (View v)
    {
        showDialog(CAMERA);
    }

    //////////////////////////////////////////////////////////////////////// 캘린더 불러오기
    public void tab2_btn_date(View v)
    {
        showDialog(DIALOG_DATE);
    }


    /////////////////////////////////////////////////////////////////////// 올리기
    public void tab2_btn_init()
    {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(edit_adddate != null && !edit_adddate.toString().equals(""))
                {
                    if(adddatafield.getText() != null && !adddatafield.getText().toString().equals(""))
                    {
                        edit_adddatafield = adddatafield.getText().toString();  // 필드상황 입력값

                        if(adddatapreparation.getText() != null && !adddatapreparation.getText().toString().equals(""))
                        {
                            edit_adddatapreparation = adddatapreparation.getText().toString();

                            if(adddataetc.getText() != null && !adddataetc.getText().toString().equals(""))
                            {
                                edit_adddataetc = adddataetc.getText().toString();

                                if(!phptype.toString().equals("FIX"))
                                {
                                     PHP_ADD_POINT();
                                }
                                else
                                {
                                     PHP_FIX_POINT();
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"[필수] 부가설명을 입력하세요.",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"[필수] 채비정보를 입력하세요.",Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"[필수] 필드상황을 입력하세요.",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"[필수] 날짜를 선택하세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    ///////////////////////////////////////////////////////////////////////// PHP 포인트 추가

    public void PHP_ADD_POINT()
    {
        StartShow();
        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));
        post.add(new BasicNameValuePair("deviceID", AppInfo.MY_DEVICEID));

        post.add(new BasicNameValuePair("pointstate", Select_type));
        post.add(new BasicNameValuePair("addlatitude",  String.format("%f", add_latitude)));
        post.add(new BasicNameValuePair("addlongtitude", String.format("%f", add_longitude)));

        post.add(new BasicNameValuePair("addpointtime", edit_adddate));
        post.add(new BasicNameValuePair("addpointaddress", add_adress));
        post.add(new BasicNameValuePair("addpointdatafield", edit_adddatafield));
        post.add(new BasicNameValuePair("addpointdatapreparation",edit_adddatapreparation));
        post.add(new BasicNameValuePair("addpointdataetc", edit_adddataetc));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab2_PointaddURL());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }

    ///////////////////////////////////////////////////////////////////////// PHP 수정 불러오기

    public void PHP_LOAD_POINT()
    {
        StartShow();
        phptype = "FIXLOAD";
        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("point_idx", FixPoint_idx));

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

    ///////////////////////////////////////////////////////////////////////// PHP 포인트 수정 업로드

    public void PHP_FIX_POINT()
    {
        StartShow();
        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));
        post.add(new BasicNameValuePair("deviceID", AppInfo.MY_DEVICEID));

        post.add(new BasicNameValuePair("fixpointidx", FixPoint_idx));
        post.add(new BasicNameValuePair("fixpointstate", Select_type));

        post.add(new BasicNameValuePair("fixpointtime", edit_adddate));
        post.add(new BasicNameValuePair("fixpointdatafield", edit_adddatafield));
        post.add(new BasicNameValuePair("fixpointdatapreparation",edit_adddatapreparation));
        post.add(new BasicNameValuePair("fixpointdataetc", edit_adddataetc));

        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab2_PointfixURL());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }



    /////////////////////////////////////////////////////////////////////// 날자선택 다이얼로그
    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id)
    {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        switch (id) {
            case DIALOG_DATE: {
                DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                    {
                        edit_adddate = year+"년"+(monthOfYear+1)+"월"+(dayOfMonth)+"일";
                        adddate.setText(edit_adddate);

                    }
                }
                        , // 사용자가 날짜설정 후 다이얼로그 빠져나올때
                        //    호출할 리스너 등록
                        cal.get(cal.YEAR), cal.get(cal.MONTH), cal.DATE+1); // 기본값 연월일
                return dpd;
            }
            case CAMERA: {
                final CharSequence[] item = {"카메라", "갤러리", "취소"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("사진 불러오기") // 제목 설정
                        .setItems(item, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (item[i].toString().equals(item[0])) {
                                    doTakePhotoAction();
                                }
                                else if (item[i].toString().equals(item[1]))
                                {
                                    doTakeAlbumAction();
                                }
                                else
                                {
                                    if (mImageCaptureUri != null) {
                                        // 임시 파일 삭제
                                        File f = new File(mImageCaptureUri.getPath());
                                        if (f.exists()) {
                                            f.delete();

                                        }
                                    }
                                    dialogInterface.dismiss();
                                }
                            }
                        });

                AlertDialog alert = builder.create();  //알림 객체 생성
                return alert;
            }

        }


        return super.onCreateDialog(id);
    }


    //////////////////////////////////// 사진 카메라, 앨범 //////////////////////////////////////////////

    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".png";
        //mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        // Crop된 이미지를 저장할 파일의 경로를 생성
        mImageCaptureUri = createSaveCropFile();

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }


    // 앨범에서 사진 가져오기
    public void doTakeAlbumAction() {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Toast.makeText(getBaseContext(), "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PICK_FROM_ALBUM: {   //앨범 이미지
                mImageCaptureUri = data.getData();
                File original_file = getImageFile(mImageCaptureUri);

                mImageCaptureUri = createSaveCropFile();
                File cpoy_file = new File(mImageCaptureUri.getPath());

                // SD카드에 저장된 파일을 이미지 Crop을 위해 복사한다.
                copyFile(original_file, cpoy_file);

            }

            case PICK_FROM_CAMERA: {   //촬영 이미지
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("output", mImageCaptureUri);

                /*
                intent.putExtra("outputX", 1000);
                intent.putExtra("outputY", 1000);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                */
                startActivityForResult(intent, CROP_FROM_iMAGE);

                break;
            }
            case CROP_FROM_iMAGE: {
                // 크롭이 된 이후의 이미지를 넘겨 받습니다.

                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에

                // 임시 파일을 삭제합니다.

                if (resultCode != RESULT_OK) {
                    return;
                }

                final Bundle extras = data.getExtras();
                // CROP된 이미지를 저장하기 위한 FILE 경로

                // String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/SmartWheel/"+System.currentTimeMillis()+".jpg";

                if (extras != null) {
                    /*
                    Bitmap photo = (Bitmap) data.getExtras().get("data"); // CROP된 BITMAP
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.PNG,100,stream);
                    */
                    String full_path = mImageCaptureUri.getPath();
                    photo_path = full_path.substring(0, full_path.length());
                    photo = BitmapFactory.decodeFile(photo_path);

                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 50, byteArray);
                    imgbyte = byteArray.toByteArray();


                    //write_img_select.setImageBitmap(photo);
                    Glide.with(this.getApplicationContext()).load(photo_path).centerCrop().bitmapTransform(new CropCircleTransformation(this.getApplicationContext()))
                            .error(R.drawable.jarang_upload_img_default).into(addimg);


                    //                Log.i("TAG1,", "포토 :" + photo + "   갯 :"+ photo.getGenerationId());
                    Toast.makeText(getBaseContext(), "진짜 잡은건지 체크중..", Toast.LENGTH_SHORT).show();


                }
                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
/*
                if (f.exists()) {
                    f.delete();
                }
*/
                break;
            }
        }
    }

    /**
     * Crop된 이미지가 저장될 파일을 만든다.
     *
     * @return Uri
     */

    private Uri createSaveCropFile() {
        Uri uri;
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        return uri;
    }

    /**
     * 선택된 uri의 사진 Path를 가져온다.
     * uri 가 null 경우 마지막에 저장된 사진을 가져온다.
     *
     * @param uri
     * @return
     */
    private File getImageFile(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        if (uri == null) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Cursor mCursor = getContentResolver().query(uri, projection, null, null,
                MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (mCursor == null || mCursor.getCount() < 1) {
            return null; // no cursor or no record
        }
        int column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        mCursor.moveToFirst();

        String path = mCursor.getString(column_index);

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }

        return new File(path);
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            OutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }



    /////////////////////////////////////////////////////////////////////////////// 통신 부분 ////////////////////////////////////////////////////////////////////////
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
            Log.i("result",result);

            if(phptype.toString().equals("ADD"))
            {
                if(!result.toString().equals("CHARNULL"))
                {
                    if (imgbyte != null)
                    {
                        UploadPoint_idx = result;
                        upload = new AndroidUploader();
                        upload.execute();
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "포인트가 기록 되었습니다.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("updatecode","REFRASH");
                        setResult(2,intent);

                        finish();
                    }
                }

            }
            else if(phptype.toString().equals("FIXLOAD"))
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

                        pointdata.put("idx",idx_);
                        pointdata.put("loginID",loginID_);
                        pointdata.put("deviceID",deviceID_);
                        pointdata.put("point_idx",point_idx_);
                        pointdata.put("point_state",point_state_);
                        pointdata.put("point_time",point_time_);
                        pointdata.put("point_address",point_address_);
                        pointdata.put("point_datafield",point_datafield_);
                        pointdata.put("point_datapreparation",point_datapreparation_);
                        pointdata.put("point_dataetc",point_dataetc_);
                        pointdata.put("point_img",point_img_);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                // UI 적용 함수 시작
                phptype = "FIX";
                Load_UiUpdate();
            }
            else if(phptype.toString().equals("FIX"))
            {
                if(result.toString().equals("SUCCESS"))
                {
                    if (imgbyte != null)
                    {
                        upload = new AndroidUploader();
                        upload.execute();
                    }
                    else
                    {

                        if(phptype.toString().equals("FIX"))
                        {
                            StopShow();
                            Toast.makeText(getApplicationContext(), "포인트가 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                            if (mImageCaptureUri != null) {
                                // 임시 파일 삭제
                                File f = new File(mImageCaptureUri.getPath());
                                if (f.exists()) {
                                    f.delete();

                                }
                            }

                            Intent intent = new Intent();
                            intent.putExtra("fix_type", "true");
                            setResult(1, intent);
                        }

                        finish();
                    }
                }
                else if(result.toString().equals("CHARNULL"))
                {
                    Toast.makeText(getBaseContext(), "특수문자 및 공백이 존재합니다.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "인터넷 환경이 불안정합니다. 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            StopShow();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 기존 프로필 정보 UI 적용
    public void Load_UiUpdate()
    {
        // 포인트 상태 (공개, 나만의)
        if(pointdata.get("point_state").toString().equals("open"))
        {
            option_open.setChecked(true);
            Select_type ="open";
        }
        else if(pointdata.get("point_state").toString().equals("closed"))
        {
            option_closed.setChecked(true);
            Select_type ="closed";
        }

        // 포인트 이미지 부분
        if(!pointdata.get("point_img").toString().equals("none"))
        {
            Glide.with(this).load(appInfo.Get_Tab2_PointimgFTP_URL()+pointdata.get("point_img")).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).bitmapTransform(new CropCircleTransformation(this)).into(addimg);
        }
        else
        {
            Glide.with(this).load(R.drawable.write_talkimg).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(addimg);
        }

        // 날짜 부분
        adddate.setText(pointdata.get("point_time").toString());
        edit_adddate = pointdata.get("point_time").toString();

        //주소
        add_address.setText(pointdata.get("point_address").toString());

        //필드상황
        adddatafield.setText(pointdata.get("point_datafield").toString());

        //채비정보
        adddatapreparation.setText(pointdata.get("point_datapreparation").toString());

        //부가설명
        adddataetc.setText(pointdata.get("point_dataetc").toString());


        StopShow();
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 이미지 업로드
    public class AndroidUploader extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                DoFileUpload(photo_path);
            } catch (Exception e) {
                Log.d("ProfileTestException", e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

        }

    }

    // 이미지 업로드하기
    private void DoFileUpload(String filePath) throws IOException
    {
        Log.d("ProfileTest", "file path = " + filePath);
//        HttpFileUpload(MyApplication.GetServerDomain() + "/php/upload_profile_image.php?&device_id=" + ((MainActivity) MainActivity.mContext).myApp.myInfo.getDevice_id()
//                + "&secret_key=" + MyApplication.secretKey, "", filePath);
        HttpFileUpload(appInfo.Get_Tab2_PointaddimgURL(), "", filePath);
    }

    // 이미지 업로드
    private void HttpFileUpload(String urlString, String params, String fileName) {
        try {

            mFileInputStream = new FileInputStream(fileName);
//            String filepath=String.valueOf(System.currentTimeMillis()) + ".jpg";
            connectUrl = new URL(urlString);  // 주소 삽입
            Log.i("URL",connectUrl.toString());
            Log.d("ProfileTest", "mFileInputStream  is " + mFileInputStream);
            ByteArrayInputStream mByteInputStream = new ByteArrayInputStream(imgbyte);

            Log.d("ProfileTest", "Enter HttpFileUpload 1");

            // open connection
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            String key = "happy";

            Log.d("ProfileTest", conn.toString());
            Log.d("ProfileTest", "Enter HttpFileUpload 2");

            // write data
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

            Log.d("ProfileTest", "Enter HttpFileUpload 3-1");

            //텍스트 올리기 (업로드 타입)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"addtype\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(phptype);  // 변경 타입
            dos.writeBytes(lineEnd);

            //텍스트 올리기 (포인트 식별값)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"pointidx\"" + lineEnd);
            dos.writeBytes(lineEnd);
            if(phptype.toString().equals("ADD"))
            {
                dos.writeBytes(UploadPoint_idx);  // 포인트 식별 아이디
            }
            else
            {
                dos.writeBytes(FixPoint_idx);  // 포인트 식별 아이디
            }
            dos.writeBytes(lineEnd);

            //텍스트 올리기 (로그인 아이디)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"loginID\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(AppInfo.MY_LOGINID);  // 로그인 아이디
            dos.writeBytes(lineEnd);

            //텍스트 올리기 (디바이스 아이디)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"deviceID\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(AppInfo.MY_DEVICEID);  // 디바이스 아이디
            dos.writeBytes(lineEnd);


            //파일올리기
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + URLEncoder.encode(fileName) + "\"" + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + filepath+"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            Log.d("ProfileTest", "Enter HttpFileUpload 4");

            int bytesAvailable = mByteInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead = mByteInputStream.read(buffer, 0, bufferSize);

            Log.d("ProfileTest", "image byte is " + bytesRead);

            // read image
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = mByteInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = mByteInputStream.read(buffer, 0, bufferSize);
            }

            Log.d("ProfileTest", "Enter HttpFileUpload 5");

            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            Log.e("ProfileTest", "File is written");
            mByteInputStream.close();
            dos.flush(); // finish upload...

            // get response
            int ch;
            InputStream is = conn.getInputStream();
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Log.e("ProfileTest", "result = " + s);
            dos.close();

            // 전송이 완료되었다면
            if (s.contains("SUCCESS")) {
                Log.d("ProfileTest", s.toString());
                mDrawMainUIHandler.sendEmptyMessage(0);
            }
        }
        catch (Exception e) {

            Log.d("ProfileTest", "exception " + e.toString());
            // TODO: handle exception
        }
    }

    // 메인 UI 그려주는 쓰레드 (글 작성 완료)
    public Handler mDrawMainUIHandler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what==0)
            {
                if(phptype.toString().equals("FIX"))
                {
                    StopShow();
                    Toast.makeText(getApplicationContext(), "포인트가 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                    if (mImageCaptureUri != null) {
                        // 임시 파일 삭제
                        File f = new File(mImageCaptureUri.getPath());
                        if (f.exists())
                        {
                            f.delete();

                        }
                    }

                    Intent intent = new Intent();
                    intent.putExtra("fix_type", "true");
                    setResult(1, intent);

                }
                else if(phptype.toString().equals("ADD"))
                {
                    StopShow();
                    Toast.makeText(getApplicationContext(), "포인트가 기록 되었습니다.", Toast.LENGTH_SHORT).show();
                    if (mImageCaptureUri != null) {
                        // 임시 파일 삭제
                        File f = new File(mImageCaptureUri.getPath());
                        if (f.exists())
                        {
                            f.delete();

                        }
                    }
                    Intent intent = new Intent();
                    intent.putExtra("updatecode","REFRASH");
                    setResult(3,intent);
                    // 프로세스 종료.

                }



                finish();
                Log.d("ChangeMyInfo", "CloseActivity");
            }
            else if(msg.what==4){
                try{
                    DoFileUpload(photo_path);
                }
                catch(Exception e)
                {
                    Log.d("ProfileTestException",e.toString());
                }
            }
        }
    };



    // 뒤로가기 설정  (저장 이미지 삭제)
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("포인트 등록 종료") // 제목 설정
                .setMessage("작성중인 포인트를 종료합니다.")  // 중앙 메세시 설정
                .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                .setPositiveButton("예", new DialogInterface.OnClickListener()
                {
                    // 예 버튼 클릭시 설정
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (mImageCaptureUri != null) {
                            // 임시 파일 삭제
                            File f = new File(mImageCaptureUri.getPath());
                            if (f.exists()) {
                                f.delete();

                            }
                        }

                        if(phptype.toString().equals("ADD"))
                        {
                            Intent intent = new Intent();
                            intent.putExtra("updatecode", "REFRASH");
                            setResult(3, intent);
                            // 프로세스 종료.
                        }
                        else
                        {
                            Intent intent = new Intent();
                            intent.putExtra("fix_type", "false");
                            setResult(1, intent);
                        }
                        finish();
                    }
                })
                .setNegativeButton("아니요", null).show();

        AlertDialog alert = builder.create();  //알림 객체 생성
    }


    // 프로그레스 설정
    public void InitShow()
    {
        loading.setProgress(ProgressDialog.STYLE_SPINNER);
        loading.setMessage("잠시만 기다려 주세요..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}
}
