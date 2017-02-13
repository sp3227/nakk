package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class Main extends Activity
{
    public static Context MinContext;  // context 스테틱

    AppInfo appInfo;   // 데이터
    ProgressDialog loading;   // 프로그레스 설정

    // 탭 JAVA 선언
    public Tab1_read tab1_;
    public Tab2_read tab2_;
    public Tab3_read tab3_;
    public Tab4_read tab4_;

    LinearLayout add_Linear;  // 내부 삽입 레이아웃
    LayoutInflater Inflater;
    LinearLayout.LayoutParams layoutParams;

    //디바이스 ID  GET
    public TelephonyManager manager;

    //레이아웃 선언
    ImageView adView;      // 배너

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MinContext = this;

        loading = new ProgressDialog(Main.this);  // 프로그래스
        appInfo = new AppInfo();  // 데이터
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        init_Layout();

    }

    public void init_Layout()        // 초기 세팅
    {
        add_Linear = (LinearLayout) findViewById(R.id.inLayout);

        Inflater = getLayoutInflater();

        Inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        tab1_ = new Tab1_read();
        tab2_ = new Tab2_read();
        tab3_ = new Tab3_read();
        tab4_ = new Tab4_read();


        // 초기 1번탭 처음 실행
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        add_Linear.removeAllViews();
        add_Linear.addView(tab1_.in_layout,layoutParams);

        tab1_.init_tab1();

        String adimg = "http://nakk20.raonnet.com/ad/default_banner.gif";
        adView = (ImageView) findViewById(R.id.adView);  // 애드 뷰 (광고 배너)
        Glide.with(this.getApplicationContext()).load(adimg).asGif().diskCacheStrategy(DiskCacheStrategy.NONE).into(adView);
    }

    // TAB 버튼 세팅

    public void Btn_Tab_1(View v)   // 탭1 자랑하기
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab1_.in_layout,layoutParams);

        tab1_.all_tab1();
    }

    public void Btn_Tab_2(View v)   // 탭2  포인트
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab2_.in_layout);

        tab2_.init_tab2();
    }

    public void Btn_Tab_3(View v)   // 탭3  낚중일기
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab3_.in_layout);

        tab3_.init_tab3();
    }

    public void Btn_Tab_4(View v)   // 탭4  설정
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        add_Linear.removeAllViews();
        add_Linear.addView(tab4_.in_layout);

        tab4_.init_tab4();
    }


/*

///////////////// 탭 1  부분 //////////////////////
 */

    public void View_ZoomImage(String imgurl)
    {
        Intent intent = new Intent(Main.this,View_img.class);
        intent.putExtra("ImuUrl",imgurl);
        startActivity(intent);

    }

    public String Get_DeviceID()
    {
        try
        {
            // 디바이스 ID 검사
            manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getDeviceId();
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }

    public void tab1_btn_category(View v)
    {
        showDialog(1);
    }

    // 타입별 불러오기  전체, 내글 (선택형 다이얼로그) 탭 1
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                final CharSequence[] item = {"전체", "내글"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("자랑 유형을 선택하세요.") // 제목 설정
                        .setItems(item, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (item[i].toString().equals(item[0])) {
                                    tab1_.all_tab1();   // 전체글
                                } else if (item[i].toString().equals(item[1])) {
                                    tab1_.my_tab1();  // 내글
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

    // 자랑하기 글쓰기
    public void tab1_btn_write(View v)
    {
        Intent intent = new Intent(this.getApplicationContext(), Write_jarangTalk.class);
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
