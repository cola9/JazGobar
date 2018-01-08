package com.example.nikolajcolic.jazgobar;

import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by nikolajcolic on 06/01/2018.
 */



public class IntroVideoView extends SurfaceView implements
        SurfaceHolder.Callback {
    private static final String TAG = "INTRO_VIDEO_CALLBACK";
    private MediaPlayer mp;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IntroVideoView(Context context, AttributeSet attrs,
                          int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public IntroVideoView(Context context, AttributeSet attrs,
                          int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IntroVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IntroVideoView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mp = new MediaPlayer();
        getHolder().addCallback(this);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Intent intent = new Intent(getContext(), ActivityWelcome.class);
                getContext().startActivity(intent);
                ((Activity) getContext()).finish();
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.intro); // your intro video file placed in raw folder named as intro.mp4
        try {
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(),
                    afd.getDeclaredLength());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        android.view.ViewGroup.LayoutParams lp = getLayoutParams();

        int screenHeight = getHeight();
        int screenWidth = getWidth();

        // this plays in full screen video
        lp.height = screenHeight;
        lp.width = screenWidth;

        setLayoutParams(lp);
        mp.setDisplay(getHolder());
        mp.setLooping(false);
        mp.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mp.stop();
    }
}