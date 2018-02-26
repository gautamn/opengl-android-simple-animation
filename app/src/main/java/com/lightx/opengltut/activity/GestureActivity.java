package com.lightx.opengltut.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lightx.opengltut.renderer.GestureMultiTouchRenderer;
import com.lightx.opengltut.renderer.SlidingLayerMultiTouchRenderer;
import com.lightx.opengltut.util.MotionUtil;

public class GestureActivity extends Activity {

    private GLSurfaceView mGLView;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;
    private float lastScale=0;
    private float lastTheta=0;//------------------>

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    class MyGLSurfaceView extends GLSurfaceView {

        private final GestureMultiTouchRenderer mRenderer;

        public MyGLSurfaceView(Context context) {
            super(context);
            setEGLContextClientVersion(2);
            mRenderer = new GestureMultiTouchRenderer(context);
            setRenderer(mRenderer);
        }

        private final float X_SCALE_FACTOR = 1080;
        private final float Y_SCALE_FACTOR = 1920;
        private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
        private float mPreviousX = 0;
        private float mPreviousY = 0;
        /*private boolean isZoomEnabled = false;*/

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    Log.d("Gesture Activity", "On action down event");
                    start.set(x, y);
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:

                    Log.d("Gesture Activity", "On action pointer down event");
                    oldDist = MotionUtil.spacingBetweenFingers(e);
                    if (oldDist > 10f) {
                        MotionUtil.midPoint(mid, e);
                        mode = ZOOM;
                    }
                    lastEvent = new float[4];
                    lastEvent[0] = e.getX(0);
                    lastEvent[1] = e.getX(1);
                    lastEvent[2] = e.getY(0);
                    lastEvent[3] = e.getY(1);
                    d = MotionUtil.rotation(e);
                    break;

                case MotionEvent.ACTION_MOVE: {

                    float scale = 0;
                    float dx = (e.getX() - start.x) * 2 / X_SCALE_FACTOR;
                    float dy = (e.getY() - start.y) * 2 / Y_SCALE_FACTOR;
                    //Log.d("Gesture Activity", "On action move event"+" dx="+dx+" dy="+dy);
                    if (mode == DRAG && MotionUtil.disTanceBetweenTwoSides(dx, dy)>0) {
                        mRenderer.actionType = 1;
                        mRenderer.changeVertexBuffer(dx, -dy);
                        Log.d("GestureActivity", "Rendering drag...");
                        requestRender();
                        start.set(x, y);
                    }else if (mode == ZOOM){
                        float newDist = MotionUtil.spacingBetweenFingers(e);
                        if (newDist > 10f) {
                           /* scale = (float)(newDist / oldDist);
                            if(dy<0 || dx<0) scale = -scale;
                            Log.d("Gesture Activity", "onTouchEvent: action pointer down event called with mode Zoom with newDist="+newDist+ " pointers="+e.getPointerCount()+" scale="+scale+" lastscale="+lastScale);
                            //mRenderer.setScaleFactor(scale);
                            if(Math.abs(scale-lastScale) > 0.05)
                                mRenderer.changeVertexBufferBasedOnScaleFactor(scale);
                            mRenderer.actionType = 3;
                            lastScale = scale;*/

                        }
                        if (lastEvent != null && e.getPointerCount() == 2) {
                            //Log.d("Gesture Activity", "On zoom mode"+" point count="+e.getPointerCount());

                            newRot = MotionUtil.rotation(e);
                            float r = newRot - d;
                            mRenderer.actionType = 2;

                            // reverse direction of rotation above the mid-line
                            if (y > getHeight() / 2) {
                                dx = dx * -1 ;
                            }
                            // reverse direction of rotation to left of the mid-line
                            if (x < getWidth() / 2) {
                                dy = dy * -1 ;
                            }
                            float angle = (dx + dy) * TOUCH_SCALE_FACTOR;
                            //mRenderer.setmAngle(-r);
                            Log.d("GestureActivity", "Rendering rotation... angle="+angle+" lastTheta="+lastTheta+" diff="+(r-lastTheta));
                            /*if((r-lastTheta)>1){
                                mRenderer.changeVertexBuffer(angle);
                                requestRender();
                            }*/

                            mRenderer.changeVertexBuffer(-angle);
                            requestRender();

                            lastTheta = -r;
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

            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    }
}