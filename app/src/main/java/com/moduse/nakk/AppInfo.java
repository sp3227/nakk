package com.moduse.nakk;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;


/**
 * Created by sejung on 2017-02-03.
 */

public class AppInfo extends Application
{
    // 앱 인증 키 URl
    private String AppVer = "1";
    private String CertificationKey = "dnfsg9sdf7g9sdf6g6sd8fg6sd78fg093840850df8g";
    private String CertificationURL = "http://nakk20.raonnet.com/php_data/Certification_App.php";

    //통합 URL
    private String UNITY_URL = "http://nakk20.raonnet.com/php_data/";

    // 로그인 URL
    private String LoginURL = UNITY_URL+"User_login.php";

    //회원가입 URL
    private String SignUpURL = UNITY_URL+"User_signup.php";

    //탭1 자랑하기 불러오기
    private String Tab1_TalkInselectURL = UNITY_URL+"Jaraing_talk_select.php";

    //탭1 자랑하기 업로드(글쓰기)
    private  String Tab1_AddtalkuploadURL = UNITY_URL+"Jaraing_talk_add.php";

    //탭1 자랑하기 이미지 업로드
    private String Tab1_AddtalkimguploadURL = UNITY_URL+ "Jaraing_talk_imgadd.php";

    //탭1 자랑하기 수정
    private String Tab1_AddtalkfixURL = UNITY_URL+"Jaraing_talk_fix.php";

    //탭1 자랑하기 수정 기존글 불러오기
    private String Tab1_AddtalkfixselectURL = UNITY_URL+"Jaraing_talk_fix_select.php";

    //탭1 이미지 저장 경로 URL
    private String Tab1_TalkImgFTP_URL = "http://nakk20.raonnet.com/talkimg/";

    //탭1 자랑하기 삭제
    private String Tab1_TalkdeleteURL = UNITY_URL+"Jaraing_talk_delete.php";

    //탭1 자랑하기 좋아요
    private String Tab1_TalklikeURL = UNITY_URL+"Jaraing_like.php";

    //탭1 자랑하기 멘트 불러오기
    private String Tab1_MentloadURL = UNITY_URL+"Jaraing_ment_select.php";

    //탭1 자랑하기 멘트 추가
    private String Tab1_MentaddURL = UNITY_URL+"Jaraing_ment_add.php";

    //탭1 자랑하기 멘트 삭제
    private String Tab1_MentdeleteURL = UNITY_URL+"Jaraing_ment_delete.php";

    //탭3 유튜브
    private String Tab3_YoutubeURL = UNITY_URL+"youtubelist.php";

    //유튜브 키
    private String YoutubeKey = "AIzaSyB6yor1mxzmpr3wga7c2MsNg_A9K2YYFJs";

    // 다음맵 KEY
    private String DaumKey = "2f280f5013022107ebe4fcd0382e5b02";

    // 내 위치 정보
    private double Latitude;    // 경도
    private double Longitude;   // 위도


    //디바이스 아이디 체크
    public static Context TargetContext;

    //MY GCM
    public static String MY_PUSHID;

    //MY DEVICEID
    public static String MY_DEVICEID;

    //앱상태
    public static boolean StateApp = false;             // 앱  실행상태
    public static boolean onGcmUdate = false;

    //저장 인덱스 값  텝1
    public static boolean SaveIndex = false;
    public static int SaveIndexNum = 0;


    // get
    public String Get_DaumKey(){return DaumKey;}


    public double Get_Latitude(){return Latitude;}
    public double Get_Longitude(){return Longitude;}

    public String Get_AppVer(){return AppVer;}
    public String Get_CertificationKey(){return CertificationKey;}
    public String Get_CertificationURL(){return CertificationURL;}
    public String Get_LoginURL(){return LoginURL;}
    public String Get_SignUpURL(){return SignUpURL;}
    public String Get_Tab1_TalkInselectURL(){return Tab1_TalkInselectURL;}
    public String Get_Tab1_TalkdeleteURL(){return Tab1_TalkdeleteURL;}
    public String Get_Tab1_TalklikeURL(){return Tab1_TalklikeURL;}
    public String Get_Tab1_AddtalkuploadURL(){return Tab1_AddtalkuploadURL;}
    public String Get_Tab1_AddtalkimguploadURL(){return Tab1_AddtalkimguploadURL;}
    public String Get_Tab1_AddtalkfixURL(){return Tab1_AddtalkfixURL;}
    public String Get_Tab1_TalkImgFTP_URL(){return Tab1_TalkImgFTP_URL;}
    public String Get_Tab1_AddtalkfixselectURL(){return Tab1_AddtalkfixselectURL;}
    public String Get_Tab1_MentloadURL(){return Tab1_MentloadURL;}
    public String Get_Tab1_MentaddURL(){return Tab1_MentaddURL;}
    public String Get_Tab1_MentdeleteURL(){return Tab1_MentdeleteURL;}


    public String Get_Tab3_YoutubeURL(){return Tab3_YoutubeURL;}
    public String Get_Tab3_YoutubeKey(){return YoutubeKey;}



    //set
    public void Set_Latitude(double value){Latitude = value;}
    public void Set_Longitude(double value){Longitude = value;}


    // 디바이스 ID 가져오기
    public String Get_DeviceID()
    {
        // 디바이스 ID 검사
        TelephonyManager manager = (TelephonyManager) TargetContext.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceid;

            if(manager.getDeviceId() != null)
            {
                deviceid = manager.getDeviceId();
            }
            else
            {
                deviceid = android.provider.Settings.Secure.getString(TargetContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            }

            return deviceid;

    }

    // 클래스 이름 가져오기
    public String getClassName(){
        try{
            ActivityManager am = (ActivityManager) TargetContext.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName topActivity=taskInfo.get(0).topActivity;
            Log.i("StateApp2",topActivity.getClassName());
            return topActivity.getClassName();
        }catch(Exception e){
            e.printStackTrace();
            return "";
        }
    }


}



