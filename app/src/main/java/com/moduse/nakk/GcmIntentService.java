package com.moduse.nakk;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONObject;


/**
 * Created by sejung on 2017-02-22.
 */

public class GcmIntentService extends IntentService
{
    public GcmIntentService()
    {
//        Used to name the worker thread, important only for debugging.
        super("GcmIntentService");
    }


    // 푸시가 날라오면 여기를 탄다
    @Override
    protected void onHandleIntent(Intent intent)
    {

        Bundle extras = intent.getExtras();

        String JsonData = "{" + "'title':'" + intent.getStringExtra("title") + "','message':'"+intent.getStringExtra("message")+ "'}";

        if(!extras.isEmpty())
        {
            try
            {
                if(AppInfo.Push_state)
                {
                    sendNotification(JsonData);
                }
            }
            catch (Exception e)
            {
                Log.d("Exception", e.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
    //푸시 메세지 받기
    private void sendNotification(String msg)
    {

        String title = "";
        String message = "";

        try
        {
            JSONObject obj = new JSONObject(msg);

            title = obj.getString("title");
            message = obj.getString("message");
        }
        catch (Exception e)
        {
            Log.d("PushError", e.toString());
        }

        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setColor(getResources().getColor(R.color.push))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        if(AppInfo.StateApp)
        {
            Log.i("StateApp0","true");
            if (((Main) Main.MinContext).appInfo.getClassName().toString().equals("com.moduse.nakk.Main"))
            {
                //푸시 왔을때 바껴야 하는것들 쓰셈


            }
        }

    }


}
