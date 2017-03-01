package com.moduse.nakk;

/**
 * Created by HOMESJ on 2017-03-01.
 */

public class SponsorData
{
    private String idx;
    private String loginID;
    private String deviceID;
    private String user_nickname;
    private String money;
    private String token;
    private String sponsortime;

    public SponsorData(String idx_, String loginID_, String deviceID_, String user_nickname_, String money_, String token_, String sponsortime_)
    {
        idx=idx_;
        loginID=loginID_;
        deviceID=deviceID_;
        user_nickname=user_nickname_;
        money=money_;
        token=token_;
        sponsortime=sponsortime_;
    }

    //get
    public String GET_idx(){return idx;}
    public String GET_loginID(){return loginID;}
    public String GET_deviceID(){return deviceID;}
    public String GET_user_nickname(){return user_nickname;}
    public String GET_money(){return money;}
    public String GET_token(){return token;}
    public String GET_sponsortime(){return sponsortime;}

    //set
    public void SET_idx(String value){idx = value;}
    public void SET_loginID(String value){loginID = value;}
    public void SET_deviceID(String value){deviceID = value;}
    public void SET_user_nickname(String value){user_nickname = value;}
    public void SET_money(String value){money = value;}
    public void SET_token(String value){token = value;}
    public void SET_sponsortime(String value){sponsortime = value;}
}
