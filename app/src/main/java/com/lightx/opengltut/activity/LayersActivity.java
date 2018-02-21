package com.lightx.opengltut.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lightx.opengltut.R;
import com.lightx.opengltut.renderer.LayerRenderer;
import com.lightx.opengltut.renderer.MyGL20Renderer;

public class LayersActivity extends Activity {

    private GLSurfaceView mGLView;

    private int xDelta;
    private int yDelta;

    private ViewGroup mainLayout;
    private ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    class MyGLSurfaceView extends GLSurfaceView {

        private final LayerRenderer mRenderer;
        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private float mPreviousX;
        private float mPreviousY;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            mRenderer = new LayerRenderer(context);
            setRenderer(mRenderer);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            ///x, y, mPreviousX, mPreviousY are phone pixel coordinates
            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;

                    // reverse direction of rotation above the mid-line
                    if (y > getHeight() / 2) {
                        dx = dx * -1 ;
                    }

                    // reverse direction of rotation to left of the mid-line
                    if (x < getWidth() / 2) {
                        dy = dy * -1 ;
                    }

                    mRenderer.setAngle(
                            mRenderer.getAngle() +
                                    ((dx + dy) * TOUCH_SCALE_FACTOR));  // = 180.0f / 320
                    requestRender();
            }

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    }
}