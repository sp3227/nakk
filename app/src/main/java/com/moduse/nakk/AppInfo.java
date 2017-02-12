package com.moduse.nakk;

/**
 * Created by sejung on 2017-02-03.
 */

public class AppInfo
{
    // 앱 인증 키 URl
    private String AppVer = "1";
    private String CertificationKey = "dnfsg9sdf7g9sdf6g6sd8fg6sd78fg093840850df8g";
    private String CertificationURL = "http://nakk20.raonnet.com/php_data/Certification_App.php";

    // 로그인 URL
    private String LoginURL = "http://nakk20.raonnet.com/php_data/User_login.php";

    //회원가입 URL
    private String SignUpURL = "http://nakk20.raonnet.com/php_data/User_signup.php";

    //탭1 글 불러오기
    private String Tab1_TalkInselectURL ="http://nakk20.raonnet.com/php_data/jaraing_talk_select.php";

    // 다음맵 KEY
    private String DaumKey = "2f280f5013022107ebe4fcd0382e5b02";

    // 내 위치 정보
    private double Latitude;    // 경도
    private double Longitude;   // 위도

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



    //set
    public void Set_Latitude(double value){Latitude = value;}
    public void Set_Longitude(double value){Longitude = value;}
}



