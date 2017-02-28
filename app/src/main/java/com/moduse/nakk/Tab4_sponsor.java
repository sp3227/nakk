package com.moduse.nakk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sejung on 2017-02-27.
 */

public class Tab4_sponsor extends Activity
{

    ProgressDialog loading;

    // 레이아웃
    TextView infotext;
    TextView payvlue;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab4_sponsor);

        loading = new ProgressDialog(this);

        infotext = (TextView) findViewById(R.id.sponsorText);
        payvlue = (TextView) findViewById(R.id.tab4_sponsor_payvalue);

        ColorChange();

    }

    // 설명글 컬러 부분 체인지
    public void ColorChange()
    {
        SpannableStringBuilder builder = new SpannableStringBuilder(getResources().getString(R.string.sponsortext));
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#01bfd7")), 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new ForegroundColorSpan(Color.parseColor("#01bfd7")), 88,93, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        infotext.append(builder);

    }

    // 스폰 금액 설정 리스너
    public void tab4_sponsor_value(View v)
    {
        final CharSequence[] item = {"50,000원", "100,000원","취소"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("후원 금액을 선택해주세요.") // 제목 설정
                .setItems(item, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        if (item[i].toString().equals(item[0]))
                        {
                            // 금액 5만원
                            payvlue.setText(item[0]+" ");
                        }
                        else if (item[i].toString().equals(item[1]))
                        {
                            // 금액 10만원
                            payvlue.setText(item[1]+" ");
                        }
                        else if (item[i].toString().equals(item[2]))
                        {
                            // 금액 취소
                            payvlue.setText("- ");
                        }
                        else
                        {
                            dialogInterface.dismiss();
                        }
                    }
                }).show();

        AlertDialog alert = builder.create();  //알림 객체 생성
    }


    // 스폰하기 결제 클릭 리스너
    public void tab4_sponsor_submit(View v)
    {

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
