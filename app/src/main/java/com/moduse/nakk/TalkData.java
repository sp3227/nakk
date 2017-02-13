package com.moduse.nakk;

/**
 * Created by sejung on 2017-02-11.
 */

public class TalkData
{
    private String proURL ="http://nakk20.raonnet.com/profileimg/";
    private String talkimgURL ="http://nakk20.raonnet.com/talkimg/";

    private String idx;
    private String talk_idx;
    private String talk_writeid;
    private String talk_img;
    private String talk_data;
    private String talk_likecount;
    private String talk_mentcount;
    private String talk_locationstate;
    private String talk_latitude;
    private String talk_longitude;
    private String talk_writetime;

    private String user_id;
    private String user_nickname;
    private String user_profile;

    public TalkData(String idx_, String talk_idx_, String writeid_, String img_, String data_, String likecount_, String mentcount_,
                    String locationstate_, String latitude_, String longitude_, String writetime_, String user_id_, String user_nickname_, String user_profile_)
    {
        idx = idx_;
        talk_idx = talk_idx_;
        talk_writeid = writeid_;
        talk_img = img_;
        talk_data = data_;
        talk_likecount = likecount_;
        talk_mentcount = mentcount_;
        talk_locationstate = locationstate_;
        talk_latitude = latitude_;
        talk_longitude = longitude_;
        talk_writetime = writetime_;

        user_id = user_id_;
        user_nickname = user_nickname_;
        user_profile = user_profile_;

    }

    //GET
    public String GET_idx(){ return idx; }
    public String GET_talk_writeid(){ return talk_writeid; }
    public String GET_talk_idx(){ return talk_idx; }
    public String GET_talk_img(){ return talkimgURL + talk_img; }
    public String GET_talk_data(){ return talk_data; }
    public String GET_talk_likecount(){ return talk_likecount; }
    public String GET_talk_mentcount(){ return talk_mentcount; }
    public String GET_talk_locationstate(){ return talk_locationstate; }
    public String GET_talk_latitude(){ return talk_latitude; }
    public String GET_talk_longitude(){ return talk_longitude; }
    public String GET_talk_writetime(){ return talk_writetime; }

    public String GET_user_id(){ return user_id; }
    public String GET_user_nickname(){ return user_nickname; }
    public String GET_user_profile(){ return proURL + user_profile; }


    //SET
    public void SET_idx(String value){ idx = value; }
    public void SET_talk_idx(String value){ talk_idx = value; }
    public void SET_talk_writeid(String value){ talk_writeid = value; }
    public void SET_talk_img(String value){ talk_img = value; }
    public void SET_talk_data(String value){ talk_data = value; }
    public void SET_talk_likecount(String value){ talk_likecount = value; }
    public void SET_talk_mentcount(String value){ talk_mentcount = value; }
    public void SET_talk_locationstate(String value){ talk_locationstate = value; }
    public void SET_talk_latitude(String value){ talk_latitude = value; }
    public void SET_talk_longitude(String value){ talk_longitude = value; }
    public void SET_talk_writetime(String value){ talk_writetime = value; }

    public void SET_user_id(String value){ user_id = value; }
    public void SET_user_nickname(String value){ user_nickname = value; }
    public void SET_user_profile(String value){ user_profile = value; }
}
