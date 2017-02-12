package com.moduse.nakk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by sejung on 2017-02-09.
 */

public class Ageeinfo extends AppCompatActivity
{
    TextView Agee_text;
    TextView Agee_title;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ageeinfo);

        Agee_text = (TextView) findViewById(R.id.Text_agee);
        Agee_title = (TextView) findViewById(R.id.Text_ageee_title);

        Intent intent = getIntent();

        if(intent.getExtras().getString("ageetype").toString().equals("agee1"))
        {
            Agee_title.setText("이용약관");
            Agee_text.setText(getResources().getText(R.string.useinfo_1));
        }
        else if(intent.getExtras().getString("ageetype").toString().equals("agee2"))
        {
            Agee_title.setText("위치기반 서비스 이용약관");
            Agee_text.setText(getResources().getText(R.string.useinfo_2));
        }


    }
}
