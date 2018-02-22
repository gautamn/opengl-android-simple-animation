package com.lightx.opengltut.activity;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lightx.opengltut.renderer.SlidingLayerMultiTouchRenderer;
import com.lightx.opengltut.renderer.SlidingLayerRenderer;

public class SlidingLayersMultiTouchActivity extends Activity {

    private GLSurfaceView mGLView;
    private ViewGroup mainLayout;
    private ImageView image;

    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private int ptrID1, ptrID2;
    private float mAngle;

    //private OnRotationGestureListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    class MyGLSurfaceView extends GLSurfaceView {

        private final SlidingLayerMultiTouchRenderer mRenderer;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            mRenderer = new SlidingLayerMultiTouchRenderer(context);
            setRenderer(mRenderer);
            ptrID1 = INVALID_POINTER_ID;
            ptrID2 = INVALID_POINTER_ID;
        }

        private final float X_SCALE_FACTOR = 1080;
        private final float Y_SCALE_FACTOR = 1920;
        private float mPreviousX = 540f;
        private float mPreviousY = 960f;

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    Log.d("SMULTITACTILITY", "action down event clicked");
                    ptrID1 = e.getPointerId(0);
                    sX = e.getX(e.findPointerIndex(ptrID1));
                    sY = e.getY(e.findPointerIndex(ptrID1));

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    Log.d("SMULTITACTILITY", "action pointer down event clicked");
                    ptrID2 = e.getPointerId(e.getActionIndex());
                    Log.d("SMULTITACTILITY", "setting ptrID2=" + ptrID2);
                    fX = e.getX(e.findPointerIndex(ptrID2));
                    fY = e.getY(e.findPointerIndex(ptrID2));
                    break;

                case MotionEvent.ACTION_MOVE:
                    Log.d("SMULTITACTILITY", "action move event clicked with ptrID1=" + ptrID1 + " ,ptrID2=" + ptrID2);
                    if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                        float nfX, nfY, nsX, nsY;
                        nsX = e.getX(e.findPointerIndex(ptrID1));
                        nsY = e.getY(e.findPointerIndex(ptrID1));
                        nfX = e.getX(e.findPointerIndex(ptrID2));
                        nfY = e.getY(e.findPointerIndex(ptrID2));
                        mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);
                        mRenderer.setmAngle(mAngle);
                        requestRender();
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    ptrID2 = INVALID_POINTER_ID;
                    break;
               /* case MotionEvent.ACTION_UP:
                    //Log.d("SMULTITACTILITY", "action up event clicked");
                    ptrID1 = INVALID_POINTER_ID;
                    break;

                case MotionEvent.ACTION_CANCEL:
                   // Log.d("SMULTITACTILITY", "action cancel event clicked");
                    ptrID1 = INVALID_POINTER_ID;
                    ptrID2 = INVALID_POINTER_ID;
                    break;*/
            }

            return true;
        }

        private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {
            float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
            float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

            float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
            if (angle < -180.f) angle += 360.0f;
            if (angle > 180.f) angle -= 360.0f;
            return angle;
        }
    }
}