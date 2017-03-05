package com.moduse.nakk;

/**
 * Created by HOMESJ on 2017-03-05.
 */

public class PointData
{
    private String idx;
    private String point_idx;
    private String point_state;
    private String loginID;
    private String deviceID;
    private String latitude;
    private String longtitude;
    private String addtime;
    private String nickname;

    public PointData(String idx_, String point_idx_, String point_state_, String loginID_, String deviceID_, String latitude_, String longtitude_, String addtime_, String nickname_)
    {
        idx=idx_;
        point_idx=point_idx_;
        point_state=point_state_;
        loginID=loginID_;
        deviceID=deviceID_;
        latitude=latitude_;
        longtitude=longtitude_;
        addtime=addtime_;
        nickname=nickname_;
    }

    //GET
    public String GET_idx(){return idx;}
    public String GET_point_idx(){return point_idx;}
    public String GET_point_state(){return point_state;}
    public String GET_loginID(){return loginID;}
    public String GET_deviceID(){return deviceID;}
    public String GET_latitude(){return latitude;}
    public String GET_longtitude(){return longtitude;}
    public String GET_addtime(){return addtime;}
    public String GET_nickname(){return nickname;}


    //SET
    public void SET_idx(String value){idx = value;}
    public void SET_point_idx(String value){point_idx = value;}
    public void SET_point_state(String value){point_state = value;}
    public void SET_loginID(String value){loginID = value;}
    public void SET_deviceID(String value){deviceID = value;}
    public void SET_latitude(String value){latitude = value;}
    public void SET_longtitude(String value){longtitude = value;}
    public void SET_addtime(String value){addtime = value;}
    public void SET_nickname(String value){nickname = value;}

}
