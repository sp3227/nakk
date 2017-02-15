package com.moduse.nakk;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class YoutubeActivity extends YouTubeFailureRecoveryActivity
{
    AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        appInfo = new AppInfo();

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_player);
        youTubeView.initialize(appInfo.Get_Tab3_YoutubeKey(), this);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored)
    {
        Intent intent = getIntent();
        String movurl = intent.getExtras().getString("movurl");
        if (!wasRestored)
        {
            player.cueVideo(movurl);
        }
    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider()
    {
        return (YouTubePlayerView) findViewById(R.id.youtube_player);
    }

}
