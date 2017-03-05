package com.moduse.nakk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by HOMESJ on 2017-03-05.
 */

public class Tab2_addpoint extends Activity
{
    //데이터
    AppInfo appInfo;

    //프로그레스
    ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2_write);

        appInfo = new AppInfo();
        loading = new ProgressDialog(this);

        InitShow();




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
