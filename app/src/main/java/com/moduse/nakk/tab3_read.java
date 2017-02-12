package com.moduse.nakk;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by sejung on 2017-02-11.
 */

public class Tab3_read extends Activity
{

    public LinearLayout in_layout;
    public LayoutInflater Inflater;

    Tab3_read()
    {
        Inflater = ((Main) Main.MinContext).getLayoutInflater();
        Inflater = (LayoutInflater) ((Main) Main.MinContext).getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        in_layout = (LinearLayout) Inflater.inflate(R.layout.tab3, null);
    }

    public void init_tab3()
    {

    }

}
