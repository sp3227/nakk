package com.moduse.nakk;

/**
 * Created by sejung on 2017-03-07.
 */

public class Tab2_mentdata
{

    private String proURL ="http://nakk20.raonnet.com/profileimg/";

    private String 	idx;
    private String 	point_idx;
    private String 	write_loginID;
    private String 	write_deviceID;
    private String 	menter_loginID;
    private String 	menter_deviceID;
    private String 	user_nickname;
    private String 	user_proimg;
    private String 	ment_addtime;
    private String 	ment_data;
    private String 	user_sponsor;


    public Tab2_mentdata(String idx_, String point_idx_, String write_loginID_, String write_deviceID_, String menter_loginID_, String menter_deviceID_, String user_nickname_, String user_proimg_, String ment_addtime_, String ment_data_, String user_sponsor_)
    {
        idx=idx_;
        point_idx=point_idx_;
        write_loginID=write_loginID_;
        write_deviceID=write_deviceID_;
        menter_loginID=menter_loginID_;
        menter_deviceID=menter_deviceID_;
        user_nickname=user_nickname_;
        user_proimg=user_proimg_;
        ment_addtime=ment_addtime_;
        ment_data=ment_data_;
        user_sponsor = user_sponsor_;
    }

    //GET
    public String GET_idx(){return idx;}
    public String GET_point_idx(){return point_idx;}
    public String GET_write_loginID(){return write_loginID;}
    public String GET_write_deviceID(){return write_deviceID;}
    public String GET_menter_loginID(){return menter_loginID;}
    public String GET_menter_deviceID(){return menter_deviceID;}
    public String GET_user_nickname(){return user_nickname;}
    public String GET_user_proimg()
    {
        if(user_proimg.toString().equals("none"))
        {
            return user_proimg;
        }
        else {
            return proURL + user_proimg;
        }
    }
    public String GET_ment_addtime(){return ment_addtime;}
    public String GET_ment_data(){return ment_data;}
    public String GET_user_sponsor(){return user_sponsor;}



    //SET
    public void SET_idx(String value){value = idx;}
    public void SET_point_idx(String value){value = point_idx;}
    public void SET_write_loginID(String value){value = write_loginID;}
    public void SET_write_deviceID(String value){value = write_deviceID;}
    public void SET_menter_loginID(String value){value = menter_loginID;}
    public void SET_menter_deviceID(String value){value = menter_deviceID;}
    public void SET_user_nickname(String value){value = user_nickname;}
    public void SET_user_proimg(String value){value = proURL+user_proimg;}
    public void SET_ment_addtime(String value){value = ment_addtime;}
    public void SET_ment_data(String value){value = ment_data;}
    public void SET_user_sponsor(String value){value = user_sponsor;}
}

