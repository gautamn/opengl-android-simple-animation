package com.lightx.opengltut.activity;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lightx.opengltut.renderer.SlidingLayerRenderer;

public class SlidingLayersActivity extends Activity {

    private GLSurfaceView mGLView;
    private ViewGroup mainLayout;
    private ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    class MyGLSurfaceView extends GLSurfaceView {

        private final SlidingLayerRenderer mRenderer;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            mRenderer = new SlidingLayerRenderer(context);
            setRenderer(mRenderer);
        }

        private final float X_SCALE_FACTOR = 1080;
        private final float Y_SCALE_FACTOR = 1920;
        private float mPreviousX=540f;
        private float mPreviousY=960f;

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            float x = e.getX();
            float y = e.getY();
            Log.d("SLIDING_LAYERS_ACTIVITY", "onTouchEvent: x="+x+" ,y="+y);

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = ((x - mPreviousX)*2)/X_SCALE_FACTOR;
                    float dy = ((y - mPreviousY)*2)/Y_SCALE_FACTOR;
                    Log.d("SLIDING_LAYERS_ACTIVITY", "onTouchEvent: Setting dx="+dx+" ,dy="+dy);
                    mRenderer.changeVertexBuffer(dx, -dy);
                    requestRender();
                    mPreviousX = x;
                    mPreviousY = y;
                    break;

                case MotionEvent.ACTION_DOWN:
                    mPreviousX = x;
                    mPreviousY = y;
                    break;
            }

            return true;
        }
    }
}