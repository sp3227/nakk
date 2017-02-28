package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by sejung on 2017-02-27.
 */

public class Tab4_fixbase extends Activity
{

    ProgressDialog loading;
    CheckBox push_box;
    boolean push_value;

    //푸시 저장부분 로컬
    SharedPreferences setting;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab4_fixbase);

        loading = new ProgressDialog(this);

        push_box = (CheckBox) findViewById(R.id.tab4_fixbase_push);

        PushSwitch();

    }

    // 푸시 스위치
    public void PushSwitch()
    {
        setting = getSharedPreferences("setting", 0);  // 로컬 세팅값 불러오기 & 저장하기
        editor= setting.edit();

        push_value = AppInfo.Push_state;

        push_box.setChecked(push_value);

        push_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked)
                {
                    push_value = true;
                    editor.putBoolean("PUSH_STATE", true);
                    AppInfo.Push_state = true;
                }
                else
                {
                    push_value = false;
                    editor.putBoolean("PUSH_STATE", false);
                    AppInfo.Push_state = false;
                }
                editor.commit();
            }
        });
    }

    // 탈퇴하기
    public void tab4_fixbase_signout(View v)
    {
        // 마지막에 작업하기
        // 계정, 토크, 멘트, 이미지, 포인트 삭제


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("[회원 탈퇴]") // 제목 설정
                .setMessage("저장한 모든 데이터가 삭제됩니다.\n(이후 복구 불가능)\n탈퇴 하시겠습니까?")
                .setCancelable(false)  //뒤로 버튼 클릭시 취소 설정
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    // 예 버튼 클릭시 설정
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                       // finish();
                    }
                })
                .setNegativeButton("아니요", null).show();

        AlertDialog alert = builder.create();  //알림 객체 생성
    }


    // 이용약관 보기 1
    public void tab4_fixbase_agee1(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(),Ageeinfo.class);
        intent.putExtra("ageetype","agee1");

        startActivity(intent);
    }

    // 이용약관 보기 1
    public void tab4_fixbase_agee2(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(),Ageeinfo.class);
        intent.putExtra("ageetype","agee2");

        startActivity(intent);
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
