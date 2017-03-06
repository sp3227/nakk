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

    //광고 불러오기
    private String Ad_loadURL = UNITY_URL+"Ad_select.php";

    //광고 이미지 경로
    private String Ad_loadimgURL = "http://nakk20.raonnet.com/ad/";

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

    //탭2 포인트 불러오기
    private String Tab2_PointselectURL = UNITY_URL+"Point_select.php";

    //탭2 포인트 디테일 불러오기
    private String Tab2_PointdetailselectURL = UNITY_URL+"Point_detail_select.php";

    //탭2 포인트 이미지 폴더 경로
    private String Tab2_PointimgFTP_URL = "http://nakk20.raonnet.com/pointimg/";

    //탭2 포인트 추가
    private String Tab2_PointaddURL = UNITY_URL+"Point_add.php";

    //탭2 포인트 이미지 추가
    private String Tab2_PointaddimgURL = UNITY_URL+"Point_addimg.php";


    //탭2 포인트 삭제
    private String Tab2_PointdeleteURL = UNITY_URL+"Point_delete.php";

    //탭3 유튜브
    private String Tab3_YoutubeURL = UNITY_URL+"youtubelist.php";

    //탭4 프로필 (기존 프로필 불러오기)
    private String Tab4_fix_profile_load = UNITY_URL+"Profile_fix_select.php";

    //탭4 프로필 (프로필 수정하기)
    private String Tab4_fix_profile_update = UNITY_URL+"Profile_fix.php";

    //탭4 프로필 (프로필 이미지 업로드)
    private String Tab4_fix_profile_imgupdate = UNITY_URL+"Profile_fix_img.php";

    //탭4 프로필 (기존 이미지 삭제 이미지만!)
    private String Tab4_fix_profile_imgdelete = UNITY_URL+"Profile_fix_imgdelete.php";

    //탭4 스폰서 결제
    private String Tab4_sponsor_pay = UNITY_URL+"Sponsor_pay.php";

    //탭4 스폰서 리스트 불러오기
    private String Tab4_sponsorlist_select = UNITY_URL+"Sponsor_select.php";

    //탭1 이미지 저장 경로 URL
    private String Tab4_ProImgFTP_URL = "http://nakk20.raonnet.com/profileimg/";


    //유튜브 키
    private String YoutubeKey = "AIzaSyB6yor1mxzmpr3wga7c2MsNg_A9K2YYFJs";

    // 다음맵 KEY
    private String DaumKey = "2f280f5013022107ebe4fcd0382e5b02";

    // 내 위치 정보
    private double Latitude;    // 경도
    private double Longitude;   // 위도

    // 내 푸시 상태 (ON/OFF)
    public static boolean Push_state = true;

    // MY 로그인 아이디
    public static String MY_LOGINID;


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

    // 광고 보여지는 뷰
    public static String ViewAD = "";

    //지도 포인트 맵

    public static int Select_MapType = 1;


    // get
    public String Get_DaumKey(){return DaumKey;}


    public double Get_Latitude(){return Latitude;}
    public double Get_Longitude(){return Longitude;}

    public String Get_AppVer(){return AppVer;}
    public String Get_CertificationKey(){return CertificationKey;}
    public String Get_CertificationURL(){return CertificationURL;}
    public String Get_LoginURL(){return LoginURL;}
    public String Get_SignUpURL(){return SignUpURL;}
    public String Get_Ad_loadURL(){return Ad_loadURL;}
    public String Get_Ad_loadimgURLL(){return Ad_loadimgURL;}
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


    public String Get_Tab2_PointselectURL(){return Tab2_PointselectURL;}
    public String Get_Tab2_PointdetailselectURL(){return Tab2_PointdetailselectURL;}
    public String Get_Tab2_PointimgFTP_URL(){return Tab2_PointimgFTP_URL;}
    public String Get_Tab2_PointdeleteURL(){return Tab2_PointdeleteURL;}
    public String Get_Tab2_PointaddURL(){return Tab2_PointaddURL;}
    public String Get_Tab2_PointaddimgURL(){return Tab2_PointaddimgURL;}


    public String Get_Tab3_YoutubeURL(){return Tab3_YoutubeURL;}
    public String Get_Tab3_YoutubeKey(){return YoutubeKey;}

    public String Get_Tab4_fix_profile_load(){return Tab4_fix_profile_load;}
    public String Get_Tab4_fix_profile_update(){return Tab4_fix_profile_update;}
    public String Get_Tab4_fix_profile_imgupdate(){return Tab4_fix_profile_imgupdate;}
    public String Get_Tab4_fix_profile_imgdelete(){return Tab4_fix_profile_imgdelete;}
    public String Get_Tab4_ProImgFTP_URL(){return Tab4_ProImgFTP_URL;}
    public String Get_Tab4_sponsor_pay(){return Tab4_sponsor_pay;}
    public String Get_Tab4_sponsorlist_select(){return Tab4_sponsorlist_select;}



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



