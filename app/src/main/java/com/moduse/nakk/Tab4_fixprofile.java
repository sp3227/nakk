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
 * Created by sejung on 2017-02-27.
 */

public class Tab4_fixprofile extends Activity
{

    ProgressDialog loading;

    // 앱정보
    AppInfo appInfo;

    //레이아웃
    ImageView Proimg;
    EditText edit_nickname;
    EditText edit_email;
    EditText edit_fixpass;
    EditText edit_fixpassre;

    // 통신부분
    phpdown task;

    // 통신 타입 (로드, 수정)
    String phptype;

    // 데이터를 담을 해시 맵
    HashMap<String,Object> userdata;

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
        setContentView(R.layout.tab4_fixprofile);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);

        //레이아웃 설정
        Proimg = (ImageView) findViewById(R.id.fix_setting_proimg);
        edit_nickname = (EditText) findViewById(R.id.fix_setting_nickname);
        edit_email = (EditText) findViewById(R.id.fix_setting_email);
        edit_fixpass = (EditText) findViewById(R.id.fix_setting_pass);
        edit_fixpassre = (EditText) findViewById(R.id.fix_setting_repass);

        edit_fixpass.setText(null);
        edit_fixpassre.setText(null);

        userdata = new HashMap<String, Object>();  // 기존 프로필 데이터를 넣을 리스트 선언

        InitShow(2);

        php_profile_select();

       // Glide.with(this).load(R.drawable.profile_default).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(Proimg);

    }

    // 기존 프로필 불러오기
    public void php_profile_select()
    {
        StartShow();
        phptype = "LOAD";

        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));
        post.add(new BasicNameValuePair("userID", AppInfo.MY_DEVICEID));


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab4_fix_profile_load());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }

    // 기존 프로필 불러오기
    public void php_profileimg_delete()
    {
        StartShow();
        phptype = "IMG_DELETE";

        ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

        post.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));
        post.add(new BasicNameValuePair("userID", AppInfo.MY_DEVICEID));
        post.add(new BasicNameValuePair("profileimg", ""+userdata.get("user_profileimg")));


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
            HttpPost httpPost = new HttpPost(appInfo.Get_Tab4_fix_profile_imgdelete());
            httpPost.setEntity(entity);

            task = new phpdown();    // 쓰레드 시작
            task.execute(httpPost);

        } catch (Exception e) {
            Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
            Log.e("Exception Error", e.toString());
        }
    }




    // 사진을 클릭했을때
    public void tab4_fix_proimg(View v)
    {
        showDialog(1);
    }


    // 프로필 수정 완료 버튼 리스너
    public void tab4_setting_profile_submit(View v)
    {
        InitShow(1);
        StartShow();
        phptype = "FIX";

        Boolean PassState = false;

        String fix_nickname_ = "";
        String fix_email_ = "";
        String fix_pass_ = "";

        // 검증 체크
        if(!edit_nickname.getText().toString().equals(userdata.get("user_nickname").toString()))
        {
            fix_nickname_ = edit_nickname.getText().toString();   //수정된 닉네임
        }
        else
        {
            fix_nickname_ = userdata.get("user_nickname").toString();
        }

        if(!edit_email.getText().toString().equals(userdata.get("user_email").toString()))
        {
            fix_email_ = edit_email.getText().toString();
        }
        else
        {
            fix_email_ = userdata.get("user_email").toString();
        }

        if(!edit_fixpass.getText().toString().equals(userdata.get("user_pass").toString()) &&
                !edit_fixpassre.getText().toString().equals(userdata.get("user_pass").toString())
                && !edit_fixpass.getText().toString().equals("") && !edit_fixpass.getText().toString().equals(null)
                && !edit_fixpassre.getText().toString().equals("") && !edit_fixpassre.getText().toString().equals(null))
        {
            if(edit_fixpass.getText().toString().equals(edit_fixpassre.getText().toString()))
            {
                fix_pass_ = edit_fixpass.getText().toString();
                PassState =true;
            }
            else
            {
                StopShow();
                Toast.makeText(this,"비밀번호가 다릅니다. 확인해주세요.",Toast.LENGTH_SHORT).show();
                edit_fixpass.setText(null);
                edit_fixpassre.setText(null);
            }
        }
        else
        {
            PassState =true;
            fix_pass_ = userdata.get("user_pass").toString();
            //Toast.makeText(this,"비밀번호가 공백이거나 기존 비밀번호와 같습니다.",Toast.LENGTH_SHORT).show();
        }


        if(PassState)
        {
            ArrayList<NameValuePair> post = new ArrayList<NameValuePair>();

            post.add(new BasicNameValuePair("loginID", AppInfo.MY_LOGINID));
            post.add(new BasicNameValuePair("userID", AppInfo.MY_DEVICEID));

            post.add(new BasicNameValuePair("fix_nickname", fix_nickname_));
            post.add(new BasicNameValuePair("fix_email", fix_email_));
            post.add(new BasicNameValuePair("fix_pass", fix_pass_));


            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(post, "UTF-8");
                HttpPost httpPost = new HttpPost(appInfo.Get_Tab4_fix_profile_update());
                httpPost.setEntity(entity);

                task = new phpdown();    // 쓰레드 시작
                task.execute(httpPost);

            } catch (Exception e) {
                Toast.makeText(this, "서버에 연결이 실패 하였습니다. 다시 시도 해주세요.", Toast.LENGTH_SHORT).show();
                Log.e("Exception Error", e.toString());
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// 사진, 갤러리 부분
    // 갤러리 다이얼로그 설정
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                final CharSequence[] item = {"카메라", "갤러리", "삭제", "취소"};
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
                                else if (item[i].toString().equals(item[2]))
                                {
                                    // 이미지 삭제
                                    php_profileimg_delete();
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
                            .error(R.drawable.jarang_upload_img_default).into(Proimg);


                    //                Log.i("TAG1,", "포토 :" + photo + "   갯 :"+ photo.getGenerationId());
                    Toast.makeText(getBaseContext(), "잘생겼는지 체크중..", Toast.LENGTH_SHORT).show();


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

            if(phptype.toString().equals("LOAD"))
            {
                String index_;
                String user_push_;
                String user_pass_;
                String user_device_;
                String user_nickname_;
                String user_email_;
                String user_state_;
                String user_profileimg_;
                String user_logindata_;


                try {
                    JSONObject root = new JSONObject(result);

                    JSONArray ja = root.getJSONArray("result");

                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jo = ja.getJSONObject(i);
                        index_ = jo.getString("index");
                        user_push_ = jo.getString("user_push");
                        user_pass_ = jo.getString("user_pass");
                        user_device_ = jo.getString("user_device");
                        user_nickname_ = jo.getString("user_nickname");
                        user_email_ = jo.getString("user_email");
                        user_state_ = jo.getString("user_state");
                        user_profileimg_ = jo.getString("user_profileimg");
                        user_logindata_ = jo.getString("user_logindata");

                        userdata.put("index",index_);
                        userdata.put("user_push",user_push_);
                        userdata.put("user_pass",user_pass_);
                        userdata.put("user_device",user_device_);
                        userdata.put("user_nickname",user_nickname_);
                        userdata.put("user_email",user_email_);
                        userdata.put("user_state",user_state_);
                        userdata.put("user_profileimg",user_profileimg_);
                        userdata.put("user_logindata",user_logindata_);

                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }

                // UI 적용 함수 시작
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
                        Toast.makeText(getBaseContext(), "프로필이 변경 되었습니다.", Toast.LENGTH_SHORT).show();
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
            else if(phptype.toString().equals("IMG_DELETE"))
            {
                if(result.toString().equals("SUCCESS"))
                {
                    Toast.makeText(getBaseContext(), "프로필 사진이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                    userdata.put("user_profileimg","none");
                    Load_UiUpdate();
                }
                else if(result.toString().equals("IMG_delete_failed"))
                {
                    Toast.makeText(getBaseContext(), "삭제할 이미지가 없습니다. ", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getBaseContext(), "인터넷 환경이 불안정합니다. 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
            StopShow();
        }
    }
/////////////////////////////////////////////////////////////////////////////// 통신 부분 끝 (END) ////////////////////////////////////////////////////////////////////////

    // 기존 프로필 정보 UI 적용
    public void Load_UiUpdate()
    {

        // 프로필 이미지 부분
        if(!userdata.get("user_profileimg").toString().equals("none"))
        {
            Glide.with(this).load(appInfo.Get_Tab4_ProImgFTP_URL()+userdata.get("user_profileimg")).centerCrop().diskCacheStrategy(DiskCacheStrategy.NONE).bitmapTransform(new CropCircleTransformation(this)).into(Proimg);
        }
        else
        {
            Glide.with(this).load(R.drawable.profile_default).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(Proimg);
        }

        // 닉네임 부분
        edit_nickname.setText(userdata.get("user_nickname").toString());

        // 이메일 부분
        edit_email.setText(userdata.get("user_email").toString());
        StopShow();
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
        HttpFileUpload(appInfo.Get_Tab4_fix_profile_imgupdate(), "", filePath);
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

            //텍스트 올리기 (loginID)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"loginID\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(AppInfo.MY_LOGINID);  // 로그인 아이디
            dos.writeBytes(lineEnd);

            //텍스트 올리기 (userID)
            dos.writeBytes(twoHyphens + boundary + lineEnd); //필드 구분자 시작
            dos.writeBytes("Content-Disposition: form-data; name=\"userID\"" + lineEnd);
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
                    Toast.makeText(getApplicationContext(), "프로필이 변경 되었습니다.", Toast.LENGTH_SHORT).show();
                }
                ((Main) Main.MinContext).tab1_.Remove_list();
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

                builder.setTitle("프로필 설정 종료") // 제목 설정
                        .setMessage("프로필 변경을 종료합니다.")  // 중앙 메세시 설정
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
    public void InitShow(int value)
    {
        if(value == 1)
        {
            loading.setProgress(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("프로필을 업로드 중입니다.");
        }
        else if(value == 2) {
            loading.setProgress(ProgressDialog.STYLE_SPINNER);
            loading.setMessage("정보를 불러오는 중입니다..");
        }
    }
    public void SetmsgShow(String value)
    {
        loading.setMessage(value);
    }
    public void StartShow() {loading.show();}
    public void StopShow() {loading.dismiss();}

}
