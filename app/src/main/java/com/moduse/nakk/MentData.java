package com.moduse.nakk;

/**
 * Created by HOMESJ on 2017-02-18.
 */

public class MentData
{
    private String proURL ="http://nakk20.raonnet.com/profileimg/";

    private String idx;                       //인덱스
    private String talk_idx;                 //게시글 idx
    private String talk_writeid;            //작성자 id
    private String push_target;             //댓글작성자 pushid
    private String menter_id;               //작성자 id
    private String menter_nickname;        //작성자 닉네임
    private String menter_img;              //작성자 프로필URL
    private String ment_addtime;            //멘트작성 시간
    private String ment_data;                //멘트 내용

    public MentData(String idx_, String talk_idx_, String talk_writeid_, String push_target_, String menter_id_, String menter_nickname_, String menter_img_, String ment_addtime_, String ment_data_)
    {
        idx = idx_;
        talk_idx = talk_idx_;
        talk_writeid = talk_writeid_;
        push_target = push_target_;
        menter_id = menter_id_;
        menter_nickname = menter_nickname_;
        menter_img = menter_img_;
        ment_addtime = ment_addtime_;
        ment_data = ment_data_;
    }

    //GET
    public String GET_idx(){return idx;}
    public String GET_talk_idx(){return talk_idx;}
    public String GET_talk_writeid(){return talk_writeid;}
    public String GET_push_target(){return push_target;}
    public String GET_menter_id(){return menter_id;}
    public String GET_menter_nickname(){return menter_nickname;}
    public String GET_menter_img(){return proURL+menter_img;}
    public String GET_ment_addtime(){return ment_addtime;}
    public String GET_ment_data(){return ment_data;}




    //SET
    public void SET_idx(String value){idx = value;}
    public void SET_talk_idx(String value){talk_idx = value;}
    public void SET_talk_writeid(String value){talk_writeid = value;}
    public void SET_push_target(String value){push_target = value;}
    public void SET_menter_id(String value){menter_id = value;}
    public void SET_menter_nickname(String value){menter_nickname = value;}
    public void SET_menter_img(String value){menter_img = value;}
    public void SET_ment_addtime(String value){ment_addtime = value;}
    public void SET_ment_data(String value){ment_data = value;}
}
