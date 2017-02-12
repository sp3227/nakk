package com.moduse.nakk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bumptech.glide.Glide;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by HOMESJ on 2017-02-12.
 */

public class View_img extends Activity
{

    ProgressDialog loading;
    String ImgUrl ="";
    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);

        Intent intent = getIntent();
        ImgUrl = intent.getExtras().getString("ImuUrl");

        loading = new ProgressDialog(View_img.this);
        InitShow();
        StartShow();

        // 레이아웃 세팅
        photoView = (PhotoView) findViewById(R.id.widget_photoview);
        Glide.with(this.getApplicationContext()).load(ImgUrl).into(photoView);
        // Glide.with(this.getApplicationContext()).load(ImgUrl).into(img);

        StopShow();
    }

    public void View_close(View v)
    {
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

}

