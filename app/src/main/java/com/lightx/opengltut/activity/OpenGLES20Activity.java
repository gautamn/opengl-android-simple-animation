package com.lightx.opengltut.activity;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;

import com.lightx.opengltut.renderer.MyGL20Renderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nitin Gautam on 09/02/2018.
 */

public class OpenGLES20Activity extends Activity {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    class MyGLSurfaceView extends GLSurfaceView {

        private final MyGL20Renderer mRenderer;
        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private float mPreviousX;
        private float mPreviousY;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            mRenderer = new MyGL20Renderer(context);
            setRenderer(mRenderer);
        }
    }
}