package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Created by sejung on 2017-02-20.
 */

public class Tab_addtalk extends Activity
{

    ProgressDialog loading;

    // 데이터
    AppInfo appInfo;

    //통신 부분
    phpdown task;

    //레이아웃 설정
    ImageView layout_location;
    ImageView layout_img;
    EditText edit_data;
    TextView title;

    // 입력데이터
    String talk_data;

    // 인텐트 넘겨받는값
    String WriteType;
    String TalkIdx;  // 수정떄만 사용

    //토크 부분
    String Talk_type;
    String Talk_idx;
    String Talkwrite_id;

    // 토크 수정부분
    HashMap<String,Object> fixdata;

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


    //지도부분
    private static final int MAP_SELECT = 3;
    String map_state = "";
    Double point_latitude = null;
    Double point_longitude = null;
    String point_address = "";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1_write);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);


        InitShow();

        layout_location = (ImageView) findViewById(R.id.tab1_add_img_location);
        layout_img = (ImageView) findViewById(R.id.tab1_add_img);
        edit_data = (EditText) findViewById(R.id.tab1_add_edit_data);
        title = (TextView) findViewById(R.id.tab1_write_title);

        // 인텐트로 넘겨받기
        Intent intent = getIntent();
        WriteType = intent.getStringExtra("add_type");
        TalkIdx = intent.getStringExtra("talk_id");

        fixdata = new HashMap<String, Object>();  // 수정 데이터를 넣을 리스트 선언




        // 타입별 초기화 (작성, 수정)
        InitWrite();


    }

    //초기화
    public void InitWrite()
    {
        Talkwrite_id = AppInfo.MY_DEVICEID;  // 디바이스 ID 가져오기 (작성자 ID)

        // 새로 작성하기
        if(WriteType.toString().equals("ADD"))
        {
            Talk_type = "ADD";
            talk_data = "";
            edit_data.setText("");

            Glide.with(this).load(R.drawable.jarang_upload_location_off).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_location);
            Glide.with(this).load(R.drawable.jarang_upload_img_default).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_img);
        }
        else if(WriteType.toString().equals("FIX"))   // 수정하기
        {
            Talk_type = "FIX";
            title.setText("수정하기");

            // php 함수 시작 기존꺼 불러오기
            tab1_fix_select();
        }
    }

    ////////// 버튼 설정///////////////

    //위치 찍기 버튼
    public void tab1_add_btn_location(View v)
    {

        if(WriteType.toString().equals("ADD"))   // 일반  포인트 찍기
        {
            Intent intent = new Intent(this.getApplicationContext(), Tab1_map.class);
            intent.putExtra("type", "point_select");
            startActivityForResult(intent, MAP_SELECT);
        }
        else if(WriteType.toString().equals("FIX") && fixdata.get("talk_locationstate").toString().equals("true"))  // 기존에 위치가 있으면
        {
            Intent intent = new Intent(this.getApplicationContext(), Tab1_map.class);
            intent.putExtra("type", "point_select_fix");
            intent.putExtra("fix_latitude",point_latitude);
            intent.putExtra("fix_longitude",point_longitude);
            startActivityForResult(intent, MAP_SELECT);
        }
    }


    //사진 찍기 버튼
    public void tab1_add_btn_img(View v)
    {
        showDialog(1);
    }

    //완료 버튼(서버 통신 시작)
    public void tab1_add_btn_submit(View v)
    {

        if(WriteType.toString().equals("ADD")) {
            talk_data = edit_data.getText().toString();
            ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

            post.add(new BasicNameValuePair("talkwriteid", Talkwrite_id));
            post.add(new BasicNameValuePair("talkdata", talk_data));
            post.add(new BasicNameValuePair("talklocationstate", map_state));
            post.add(new BasicNameValuePair("talklatitude", String.format("%f", point_latitude)));
            post.add(new BasicNameValuePair("talklongitude", String.format("%f", point_longitude)));


            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                HttpPost httpPost = new HttpPost(appInfo.Get_Tab1_AddtalkuploadURL());
                httpPost.setEntity(entity);

                task = new phpdown();    // 쓰레드 시작
                task.execute(httpPost);

            } catch (Exception e) {
                Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("Exception Error", e.toString());
            }
        }
        else if(WriteType.toString().equals("FIX"))
        {
            WriteType = "FIXUPDATE";
            Talk_type = "FIX";
            talk_data = edit_data.getText().toString();

            ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

            post.add(new BasicNameValuePair("talkid", TalkIdx));
            post.add(new BasicNameValuePair("talkwriteid", Talkwrite_id));
            post.add(new BasicNameValuePair("talkdata", talk_data));
            post.add(new BasicNameValuePair("talklocationstate", map_state));
            post.add(new BasicNameValuePair("talklatitude", String.format("%f", point_latitude)));
            post.add(new BasicNameValuePair("talklongitude", String.format("%f", point_longitude)));


            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                HttpPost httpPost = new HttpPost(appInfo.Get_Tab1_AddtalkfixURL());
                httpPost.setEntity(entity);

                task = new phpdown();    // 쓰레드 시작
                task.execute(httpPost);

            } catch (Exception e) {
                Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("Exception Error", e.toString());
            }
        }
    }
        // 버튼 아님 수정하기 이전 기록 불러오기
    public void tab1_fix_select()
    {

        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("talk_idx", TalkIdx));
        post.add(new BasicNameValuePair("talk_writeid", Talkwrite_id));


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab1_AddtalkfixselectURL());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 사진, 갤러리 부분
   // 갤러리 다이얼로그 설정
   @Override
   protected Dialog onCreateDialog(int id) {
       switch (id) {
           case 1:
               final CharSequence[] item = {"카메라", "갤러리", "취소"};
               AlertDialog.Builder builder = new AlertDialog.Builder(this);

               builder.setTitle("사진 불러오기") // 제목 설정
                       .setItems(item, new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialogInterface, int i) {
                               if (item[i].toString().equals(item[0])) {
                                   doTakePhotoAction();
                               } else if (item[i].toString().equals(item[1])) {
                                   doTakeAlbumAction();
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
                            .error(R.drawable.jarang_upload_img_default).into(layout_img);


                    //                Log.i("TAG1,", "포토 :" + photo + "   갯 :"+ photo.getGenerationId());
                    Toast.makeText(getBaseContext(), "퍼온 사진인지 체크중..", Toast.LENGTH_SHORT).show();


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

            case MAP_SELECT:
            {

                map_state = data.getStringExtra("point_state");
                point_latitude =  data.getDoubleExtra("point_latitude",0);
                point_longitude = data.getDoubleExtra("point_longitude",0);
                point_address = data.getStringExtra("point_address");

                if(map_state.toString().equals("true"))
                {
                    Glide.with(this).load(R.drawable.jarang_upload_location_on).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_location);

                    Toast.makeText(getApplicationContext(),point_address,Toast.LENGTH_SHORT).show();
                }


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
            Log.i("result",result);
            if(WriteType.toString().equals("ADD"))
            {
                if (!result.toString().equals("CHARNULL"))
                {
                    if (imgbyte != null)
                    {
                        Talk_idx = result;
                        upload = new AndroidUploader();
                        upload.execute();
                    }
                    else
                    {
                        Toast.makeText((Main) Main.MinContext, "당신의 자랑질이 등록 되었습니다.", Toast.LENGTH_SHORT).show();

                        ((Main) Main.MinContext).tab1_.all_tab1();  // (토크리스트)리스트뷰 초기화

                        finish();
                    }

                } else {
                    Toast.makeText((Main) Main.MinContext, "특수문자는 사용할수 없습니다. 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            else if(WriteType.toString().equals("FIXUPDATE"))
            {
                if (!result.toString().equals("CHARNULL"))
                {
                    if (imgbyte != null)
                    {
                        Talk_idx = TalkIdx;
                        upload = new AndroidUploader();
                        upload.execute();
                    }
                    else
                    {
                        Toast.makeText((Main) Main.MinContext, "당신의 자랑질이 수정 되었습니다.", Toast.LENGTH_SHORT).show();

                        ((Main) Main.MinContext).tab1_.all_tab1();  // (토크리스트)리스트뷰 초기화

                        finish();
                    }

                } else {
                    Toast.makeText((Main) Main.MinContext, "특수문자는 사용할수 없습니다. 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            else if(WriteType.toString().equals("FIX"))
            {
                String idx_;
                String talk_idx_;
                String talk_writeid_;
                String talk_img_;
                String talk_data_;
                String talk_locationstate_;
                String talk_latitude_;
                String talk_longitude_;
                String talk_writetime_;


                try {
                    JSONObject root = new JSONObject(result);

                    JSONArray ja = root.getJSONArray("result");

                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jo = ja.getJSONObject(i);
                        idx_ = jo.getString("idx");
                        talk_idx_ = jo.getString("talk_idx");
                        talk_writeid_ = jo.getString("talk_writeid");
                        talk_img_ = jo.getString("talk_img");
                        talk_data_ = jo.getString("talk_data");
                        talk_locationstate_ = jo.getString("talk_locationstate");
                        talk_latitude_ = jo.getString("talk_latitude");
                        talk_longitude_ = jo.getString("talk_longitude");
                        talk_writetime_ = jo.getString("talk_writetime");

                        fixdata.put("idx",idx_);
                        fixdata.put("talk_idx",talk_idx_);
                        fixdata.put("talk_writeid",talk_writeid_);
                        fixdata.put("talk_img",talk_img_);
                        fixdata.put("talk_data",talk_data_);
                        fixdata.put("talk_locationstate",talk_locationstate_);
                        fixdata.put("talk_latitude",talk_latitude_);
                        fixdata.put("talk_longitude",talk_longitude_);
                        fixdata.put("talk_writetime",talk_writetime_);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                // UI 적용 함수 시작
                Fix_UiUpdate();
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////  수정하기 불러온거 UI 적용

    public void Fix_UiUpdate()
    {

        // 이미지 부분
        if(!fixdata.get("talk_img").toString().equals("none"))
        {
           // Glide.with(this).load(R.drawable.jarang_upload_location_off).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_location);
            Glide.with(this).load(appInfo.Get_Tab1_TalkImgFTP_URL()+fixdata.get("talk_img")).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_img);
        }

        // 위치 부분
        if(!fixdata.get("talk_locationstate").equals("none"))
        {
            Glide.with(this).load(R.drawable.jarang_upload_location_on).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_location);

            map_state = "true";

            point_latitude = Double.parseDouble(fixdata.get("talk_latitude").toString());
            point_longitude = Double.parseDouble(fixdata.get("talk_longitude").toString());

        }

        // 내용 부분

        if(!fixdata.get("talk_data").equals(null))
        {
            edit_data.setText(fixdata.get("talk_data").toString());
        }


    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 이미지 업로드 부분

    // 이미지 업로드
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
        HttpFileUpload(appInfo.Get_Tab1_AddtalkimguploadURL(), "", filePath);
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

            //토크 타입 올리기 (ADD, FIX)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"talktype\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Talk_type);  // 토크 식별 아이디
            dos.writeBytes(lineEnd);

            //텍스트 올리기 (talkidx)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"talkidx\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Talk_idx);  // 토크 식별 아이디
            dos.writeBytes(lineEnd);

            //텍스트 올리기 (talkwriteid)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"talkwriteid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(Talkwrite_id);  // 작성자 아이디
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
                if(WriteType.toString().equals("ADD"))
                {
                    Toast.makeText((Main) Main.MinContext, "당신의 자랑질이 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                else if(WriteType.toString().equals("FIX"))
                {
                    Toast.makeText((Main) Main.MinContext, "당신의 자랑질이 수정 되었습니다.", Toast.LENGTH_SHORT).show();
                }

                ((Main) Main.MinContext).tab1_.all_tab1();  // (토크리스트)리스트뷰 초기화

                finish();  // 창 종료
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            //하드웨어 뒤로가기 버튼에 따른 이벤트 설정
            case KeyEvent.KEYCODE_BACK:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("자랑질 종료") // 제목 설정
                        .setMessage("작성중인 글을 종료합니다.")  // 중앙 메세시 설정
                        .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            // 예 버튼 클릭시 설정
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (mImageCaptureUri != null) {
                                    // 임시 파일 삭제
                                    File f = new File(mImageCaptureUri.getPath());
                                    if (f.exists()) {
                                        f.delete();

                                    }
                                }
                                // 프로세스 종료.
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
        loading.setMessage("잠시만 기다려 주세요..");
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}
}
