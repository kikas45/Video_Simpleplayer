package com.example.videosimpleplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.controls.ControlsProviderService;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.DefaultHlsDataSourceFactory;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;


public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {


    ArrayList<MediaFiles> mVideoFiles = new ArrayList<>();
    PlayerView playerView;
    SimpleExoPlayer player;
    private ControlsProviderService controlsMode;
    public  enum  ControlsProviderService{
        LOCK, FULLSCREEN;
    }
    int position;
    String videoTitle;
    TextView title;
    ImageView videoback, lock, unlcok, scalling;
    ProgressBar progressBar;
    RelativeLayout root;
    ConcatenatingMediaSource concatenatingMediaSource;

   private ImageView nextButton, previousButton;

   ///// horizontal recycler Variables

    private   ArrayList<IconModel> iconModelsArray = new ArrayList<>();
    PlayBackIconAdapter playBackIconAdapter;
    RecyclerView recyclerViewicons;

   ///// horizontal recycler Variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullscreen();
        setContentView(R.layout.activity_video_player);
        getSupportActionBar().hide();
        playerView = findViewById(R.id.exoplayer_view);
         progressBar = findViewById(R.id.progress_bar);

        position = getIntent().getIntExtra("position", 1);
        videoTitle = getIntent().getStringExtra("video_title");
        mVideoFiles = getIntent().getExtras().getParcelableArrayList("video_Arraylist");

        title = findViewById(R.id.video_title);
        title.setText(videoTitle);
        videoback = findViewById(R.id.video_back);
        lock = findViewById(R.id.lock);
        unlcok = findViewById(R.id.un_lock);
        root = findViewById(R.id.root_layout);

        ///// horizontal recycler Variables

        recyclerViewicons = findViewById(R.id.recyclerView_icon);

        ///// horizontal recycler Variables

        nextButton = findViewById(R.id.exo_next);
        previousButton = findViewById(R.id.exo_prev);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        videoback.setOnClickListener(this);
        lock.setOnClickListener(this);
        unlcok.setOnClickListener(this);
        scalling = findViewById(R.id.scalling_CK);
        scalling.setOnClickListener(firstListner);


        playVideo();

        //// implement the horizontal recycler view

       // iconModelsArray.add(new IconModel(R.drawable))

        //// implement the horizontal recycler view

        evenlisterner();
    }

    //// personal addition
    private void evenlisterner() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if(playbackState== Player.STATE_BUFFERING){

                   progressBar.setVisibility(View.VISIBLE);

                }else if(playbackState==Player.STATE_READY){

                    progressBar.setVisibility(View.GONE);


                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
            int currentPosition = 0;
            @Override
            public void onPositionDiscontinuity(int reason) {


              /*  if (player.getCurrentWindowIndex() != currentPosition) {
                    currentPosition = player.getCurrentWindowIndex();
                    player.setPlayWhenReady(false);
                    player.stop();
                }*/

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }


    private void playVideo() {
        String path = mVideoFiles.get(position).getPath();
        Uri uri = Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "app"));
        concatenatingMediaSource = new ConcatenatingMediaSource();
        for ( int i = 0 ; i<mVideoFiles.size(); i++){
            new File(String.valueOf(mVideoFiles.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(String.valueOf(uri)));
            concatenatingMediaSource.addMediaSource(mediaSource);

        }

        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);

        playerError();
    }

    private void playerError() {
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(getApplicationContext(), "video is playing error", Toast.LENGTH_SHORT).show();

            }
        });

        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (player.isPlaying()){
            player.stop();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();

    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }
    private void setFullscreen(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.video_back:

                if (player!=null){
                    player.release();
                }
                finish();

                break;
            case R.id.lock:
                controlsMode = controlsMode.FULLSCREEN;
                root.setVisibility(View.VISIBLE);
                lock.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Unlocked", Toast.LENGTH_SHORT).show();

                break;
            case R.id.un_lock:
                controlsMode = ControlsProviderService.FULLSCREEN;
                root.setVisibility(View.INVISIBLE);
                lock.setVisibility(View.VISIBLE);
                Toast.makeText(this, "locked", Toast.LENGTH_SHORT).show();

                break;


            ///
            case R.id.exo_next:
                try {
                    player.stop();
                    position++;
                    playVideo();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No next Video", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.exo_prev:
                try {
                    player.stop();
                    position--;
                    playVideo();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "No Previous Video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }


    }

    View.OnClickListener firstListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.ic_round_lock);
            Toast.makeText(VideoPlayerActivity.this, "FullScreen", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(seconListner);
        }
    };
    View.OnClickListener seconListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.ic_round_fit);
            Toast.makeText(VideoPlayerActivity.this, "zoom", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(thirdListner);
        }
    };
    View.OnClickListener thirdListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scalling.setImageResource(R.drawable.ic_round_fit);
            Toast.makeText(VideoPlayerActivity.this, "Fit", Toast.LENGTH_SHORT).show();
            scalling.setOnClickListener(firstListner);
        }
    };
}