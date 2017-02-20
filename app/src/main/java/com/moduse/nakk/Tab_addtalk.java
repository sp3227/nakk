package com.moduse.nakk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by sejung on 2017-02-20.
 */

public class Tab_addtalk extends Activity
{

    ProgressDialog loading;

    //레이아웃 설정
    ImageView layout_location;
    ImageView layout_img;
    EditText edit_data;

    // 입력데이터
    String talk_data;

    // 인텐트 넘겨받는값
    String WriteType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1_write);

        InitShow();

        layout_location = (ImageView) findViewById(R.id.tab1_add_img_location);
        layout_img = (ImageView) findViewById(R.id.tab1_add_img);
        edit_data = (EditText) findViewById(R.id.tab1_add_edit_data);

        // 타입별 초기화 (작성, 수정)
        InitWrite();


    }

    //초기화
    public void InitWrite()
    {
        // 새로 작성하기
        if(WriteType.toString().equals(""))
        {
            talk_data = "";
            edit_data.setText("");

            Glide.with(this).load(R.drawable.jarang_upload_location_off).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_location);
            Glide.with(this).load(R.drawable.testimg3).centerCrop().bitmapTransform(new CropCircleTransformation(this)).into(layout_img);
        }
        else   // 수정하기
        {

        }
    }

    ////////// 버튼 설정///////////////

    //위치 찍기 버튼
    public void tab1_add_btn_location(View v)
    {

    }

    //사진 찍기 버튼
    public void tab1_add_btn_img(View v)
    {

    }

    //완료 버튼(서버 통신 시작)
    public void tab1_add_btn_submit(View v)
    {

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
