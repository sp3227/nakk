package com.moduse.nakk;

/**
 * Created by sejung on 2017-02-15.
 */

public class YoutubeData
{
    private String youtubeimgURL ="http://nakk20.raonnet.com/youtubeImg/";

    private String idx;
    private String title;
    private String imgurl;
    private String data;
    private String movURL;
    private String uploadtime;


    public YoutubeData(String idx_, String title_, String imgurl_, String data_, String movURL_, String uploadtime_)
    {
        idx=idx_;
        title=title_;
        imgurl=imgurl_;
        data=data_;
        movURL=movURL_;
        uploadtime=uploadtime_;
    }

    //GET
    public String GET_idx(){return idx;}
    public String GET_title(){return title;}
    public String GET_imgurl(){return youtubeimgURL+imgurl;}
    public String GET_data(){return data;}
    public String GET_movURL(){return movURL;}
    public String GET_uploadtime(){return uploadtime;}



    //SET
    public void SET_idx(String value){idx=value;}
    public void SET_title(String value){title=value;}
    public void SET_imgurl(String value){imgurl=value;}
    public void SET_data(String value){data=value;}
    public void SET_movURL(String value){movURL=value;}
    public void SET_uploadtime(String value){uploadtime=value;}

}
