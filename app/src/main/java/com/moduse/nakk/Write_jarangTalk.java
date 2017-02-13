package com.moduse.nakk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by HOMESJ on 2017-02-12.
 */

public class Write_jarangTalk extends Activity
{

    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_image);

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

