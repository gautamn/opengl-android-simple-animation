package com.lightx.opengltut.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.lightx.opengltut.renderer.GestureMultiTouchRenderer;
import com.lightx.opengltut.util.MotionUtil;
import com.lightx.opengltut.util.RotationGestureDetector;

public class MultiTouchGestureActivity extends Activity {

    private GLSurfaceView mGLView;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float fX, fY, sX, sY;
    private ScaleGestureDetector mScaleDetector;
    private RotationGestureDetector mRotationDetector;
    private float mScaleFactor = 1.f;
    private final float X_SCALE_FACTOR = 1080;
    private final float Y_SCALE_FACTOR = 1920;
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    class MyGLSurfaceView extends GLSurfaceView implements RotationGestureDetector.OnRotationGestureListener {

        private GestureMultiTouchRenderer mRenderer;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            mRenderer = new GestureMultiTouchRenderer(context);
            setRenderer(mRenderer);
            mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
            mRotationDetector = new RotationGestureDetector(this, this);
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            mScaleDetector.onTouchEvent(e);
            mRotationDetector.onTouchEvent(e);

            switch (e.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    Log.d("Gesture Activity", "On action down event");
                    sX = e.getX();
                    sY = e.getY();
                    start.set(sX, sY);
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    fX = e.getX();
                    fY = e.getY();
                    Log.d("Gesture Activity", "On action pointer down event");
                    oldDist = MotionUtil.spacingBetweenFingers(e);
                    if (oldDist > 10f) {
                        MotionUtil.midPoint(mid, e);
                        mode = ZOOM;
                    }
                    break;

                case MotionEvent.ACTION_MOVE: {
                    float dx = (e.getX() - start.x) * 2 / X_SCALE_FACTOR;
                    float dy = (e.getY() - start.y) * 2 / Y_SCALE_FACTOR;
                    Log.d("Touch", "mRotationDetector.getAngle()=" + mRotationDetector.getAngle());
                    if (mode == DRAG && MotionUtil.disTanceBetweenTwoSides(dx, dy) > 0) {
                        mRenderer.actionType = 1;
                        mRenderer.changeVertexBuffer(dx, -dy);
                        Log.d("GestureActivity", "Rendering drag...");
                        requestRender();
                        start.set(e.getX(), e.getY());
                    } else if (mode == ZOOM) {
                        mRenderer.actionType = 3;
                        mRenderer.changeVertexBufferBasedOnScaleFactor(mScaleFactor);
                        requestRender();
                        if (e.getPointerCount() == 2) {
                            float nfX, nfY, nsX, nsY;
                            nfX = e.getX(e.getPointerId(0));
                            nfY = e.getY(e.getPointerId(0));
                            nsX = e.getX(e.getPointerId(1));
                            nsY = e.getY(e.getPointerId(1));

                            if (MotionUtil.disTanceBetweenTwoPoints(nfX, nfY, fX, fY) > 0 && MotionUtil.disTanceBetweenTwoPoints(nsX, nsY, sX, sY) > 0) {
                                mRenderer.actionType = 2;
                                mRenderer.changeVertexBuffer(mRotationDetector.getAngle() % 360);
                                requestRender();
                            }
                            fX = nfX;
                            fY = nfY;
                            sX = nfX;
                            sY = nfY;
                        }
                    }
                }
                break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_UP:
                    mode = NONE;
                    break;
            }

            return true;
        }

        @Override
        public void onRotation(RotationGestureDetector rotationDetector) {

            float angle = rotationDetector.getAngle();
            Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
        }
    }

    private class ScaleListener
            extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.99f, Math.min(mScaleFactor, 1.01f));

            mGLView.invalidate();
            return true;
        }
    }/*class*/

}