package com.asun.trendingtv;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

public class VideoPlayer_Activity extends AppCompatActivity implements View.OnClickListener {

    private static final DefaultBandwidthMeter BANDWIDTH_METER=new DefaultBandwidthMeter();
    private static final String TAG ="PlayerActivity";
    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private ComponentListener componentListener;
    private long playbackposition;
    private int currentWindow;
    private boolean playWhenReady=true;
    private ProgressBar progressBar;
    NativeExpressAdView adView;
    MainActivity mainActivity=new MainActivity();

    //TextViews to show details of volume and brightness
    private TextView tVBrightness,tVVolume;
    //SeekBars to set volume and brightness
    private SeekBar sbVolume,sbBrightness;
    //AudioManager object, that will get and set volume
    private AudioManager audioManager;
    //Variable to store brightness value
    private int brightness;
    //Content resolver used as a handle to the system's settings
    private ContentResolver cResolver;
    //Window object, that will store a reference to the current window
    private Window window;
    int maxVolume=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        mainActivity.showVideoAd();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initializeControls();


        String title=getIntent().getExtras().getString("title");
        TextView title_tv = (TextView) findViewById(R.id.video_title_tv);
        ImageView back_btn = (ImageView) findViewById(R.id.back_btn);
        title_tv.setText(title);
        back_btn.setOnClickListener(this);
        componentListener=new ComponentListener();
        playerView=(SimpleExoPlayerView) findViewById(R.id.player_view);
        progressBar=(ProgressBar)findViewById(R.id.progress_id) ;
        adView=(NativeExpressAdView)findViewById(R.id.player_ad_id);
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                adView.loadAd(new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();


            }
        });

        adView.loadAd(new AdRequest.Builder().addTestDevice("3004E128A9A0AFEA0736A6DD128990FF").build());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Util.SDK_INT > 23){
            initializePlayer();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if(Util.SDK_INT <= 23 || player==null){
            initializePlayer();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(Util.SDK_INT <= 23){
            releasePlayer();



        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Util.SDK_INT > 23){
            releasePlayer();

            // Toast.makeText(getApplicationContext(),"onStop",Toast.LENGTH_LONG).show();


        }
    }


    private void initializePlayer(){
        String s=getIntent().getExtras().getString("video_url");
        String s1=getIntent().getExtras().getString("subtitle_url");


        if(player==null){

            TrackSelection.Factory adaptiveTrackSelectionFactory=
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            player= ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(
                    getApplicationContext()),new DefaultTrackSelector(adaptiveTrackSelectionFactory),new DefaultLoadControl());
            player.addListener(componentListener);
            player.setVideoDebugListener(componentListener);
            player.setAudioDebugListener(componentListener);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow,playbackposition);
            playerView.setPlayer(player);
        }
        MediaSource mediaSource=buildMediaSource(Uri.parse(s));

        Format textFormat = Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP,
                null, Format.NO_VALUE, Format.NO_VALUE,null, null);

        if (s1 != null) {
            if(!s1.equals("")) {
                Uri uri = Uri.parse(s1);
                DefaultHttpDataSourceFactory factory = new DefaultHttpDataSourceFactory("exo");


                MediaSource subtitleSource = new SingleSampleMediaSource(uri, factory, textFormat, C.TIME_UNSET);
                //Plays the video with the sideloaded subtitle.
                MergingMediaSource mergedSource =
                        new MergingMediaSource(mediaSource, subtitleSource);
                player.prepare(mergedSource);
                player.setPlayWhenReady(true);


            }
            else {
                player.prepare(mediaSource);
                player.setPlayWhenReady(true);


            }
        }
    }

    private void releasePlayer(){
        if(player!=null){
            playbackposition=player.getCurrentPosition();
            currentWindow=player.getCurrentWindowIndex();
            playWhenReady=player.getPlayWhenReady();
            //  player.removeListener(componentListener);
            // player.setVideoListener(null);
            // player.setVideoDebugListener(null);
            // player.setAudioDebugListener(null);
            player.stop();
            player=null;
        }
    }

    private MediaSource buildMediaSource(Uri  uri){
       // DefaultHttpDataSourceFactory defaultHttpDataSourceFactory=new DefaultHttpDataSourceFactory("exo");
        DataSource.Factory factory=new DefaultDataSourceFactory(getApplicationContext()
                ,Util.getUserAgent(getApplicationContext(),"TestForAppVideoPlayer"),BANDWIDTH_METER);
        ExtractorsFactory extractorsFactory=new DefaultExtractorsFactory();
        //  DataSource.Factory dataSourceFactory=new DefaultHttpDataSourceFactory("ua",BANDWIDTH_METER);
        // DashChunkSource.Factory dashChunkSourceFactory=new DefaultDashChunkSource.Factory(dataSourceFactory);
        return new ExtractorMediaSource(uri,factory,extractorsFactory,null,null);
    }

    @SuppressLint("InlinedApi")
    private  void hideSystemUi(){
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                |View.SYSTEM_UI_FLAG_FULLSCREEN
                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                );
    }

    @Override
    public void onClick(View v) {
        onBackPressed();
    }

    private class ComponentListener implements ExoPlayer.EventListener,VideoRendererEventListener,AudioRendererEventListener
    {

        @Override
        public void onTimelineChanged(Timeline timeline, Object manifest) {

        }

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        }

        @Override
        public void onLoadingChanged(boolean isLoading) {

        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch ((playbackState)){

                case ExoPlayer.STATE_IDLE:
                    stateString="ExoPlayer.STATE_IDLE     _";
                    break;

                case ExoPlayer.STATE_BUFFERING:
                    stateString="ExoPlayer.STATE_BUFFERING     _";
                    progressBar.setVisibility(View.VISIBLE);

                    adView.setVisibility(View.GONE);
                    break;


                case ExoPlayer.STATE_READY:
                    stateString="ExoPlayer.STATE_READY     _";
                    progressBar.setVisibility(View.GONE);

                    if(!playWhenReady ) {
                        adView.setVisibility(View.VISIBLE);
                    }
                    else {
                        adView.setVisibility(View.GONE);

                    }
                    break;


                case ExoPlayer.STATE_ENDED:
                    stateString="ExoPlayer.STATE_ENDED     _";
                    break;

                default:
                    stateString="UNKNOWNSTATE            _";
                    break;
            }
            Log.d(TAG,"change state to" +stateString+  "playwhenReady" +playWhenReady);

        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {

        }

        @Override
        public void onPositionDiscontinuity() {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {


        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioTrackUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }

        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {


        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // mainActivity.showIntersititialAd();
        mainActivity.showVideoAd();
    }

    private void initializeControls() {
        //get reference of the UI Controls
        sbVolume = (SeekBar) findViewById(R.id.sbVolume);
        sbBrightness = (SeekBar) findViewById(R.id.sbBrightness);
        tVVolume=(TextView)findViewById(R.id.tVVolume);
        tVBrightness = (TextView) findViewById(R.id.tVBrightness);

        try {

            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            //set max progress according to volume
            sbVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            //get current volume
            sbVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            //Set the seek bar progress to 1
            sbVolume.setKeyProgressIncrement(1);
            //get max volume
            maxVolume=sbVolume.getMax();
            sbVolume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                    //Calculate the brightness percentage
                    float perc = (progress /(float)maxVolume)*100;
                    //Set the brightness percentage
                    tVVolume.setVisibility(View.VISIBLE);
                    tVVolume.setText(" "+(int)perc +"%");
                    Handler handler =new Handler();
                    handler.postDelayed(() -> tVVolume.setVisibility(View.GONE),1000);
                }
            });

        } catch (Exception e) {

        }


        //Get the content resolver
        cResolver = getContentResolver();

        //Get the current window
        window = getWindow();

        //Set the seekbar range between 0 and 255
        sbBrightness.setMax(255);
        //Set the seek bar progress to 1
        sbBrightness.setKeyProgressIncrement(1);

        try
        {
            //Get the current system brightness
            brightness = System.getInt(cResolver, System.SCREEN_BRIGHTNESS);
        }
        catch (SettingNotFoundException e)
        {
            //Throw an error case it couldn't be retrieved
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }

        //Set the progress of the seek bar based on the system's brightness
        sbBrightness.setProgress(brightness);

        //Register OnSeekBarChangeListener, so it can actually change values
        sbBrightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                //Set the system brightness using the brightness variable value
                System.putInt(cResolver, System.SCREEN_BRIGHTNESS, brightness);
                //Get the current window attributes
                LayoutParams layoutpars = window.getAttributes();
                //Set the brightness of this window
                layoutpars.screenBrightness = brightness / (float)255;
                //Apply attribute changes to this window
                window.setAttributes(layoutpars);
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
                //Nothing handled here
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                //Set the minimal brightness level
                //if seek bar is 20 or any value below
                if(progress<=20)
                {
                    //Set the brightness to 20
                    brightness=20;
                }
                else //brightness is greater than 20
                {
                    //Set brightness variable based on the progress bar
                    brightness = progress;
                }
                //Calculate the brightness percentage
                float perc = (brightness /(float)255)*100;
                //Set the brightness percentage
                tVBrightness.setVisibility(View.VISIBLE);
                tVBrightness.setText(" "+(int)perc +"%");
                Handler handler =new Handler();
                handler.postDelayed(() -> tVBrightness.setVisibility(View.GONE),2000);
            }
        });
    }
}
