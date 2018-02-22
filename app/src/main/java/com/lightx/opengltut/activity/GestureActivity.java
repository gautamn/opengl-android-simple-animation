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

public class GestureActivity extends Activity {

    private GLSurfaceView mGLView;
    private ViewGroup mainLayout;
    private ImageView image;

    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private int ptrID1, ptrID2;
    private float mAngle;

    // remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private PointF topLayerCenter = new PointF(0,0);

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
            ptrID1 = INVALID_POINTER_ID;
            ptrID2 = INVALID_POINTER_ID;
        }

        private final float X_SCALE_FACTOR = 1080;
        private final float Y_SCALE_FACTOR = 1920;
        private float mPreviousX = 540f;
        private float mPreviousY = 960f;
        private boolean isZoomEnabled = false;

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    ptrID1 = e.getPointerId(0);
                    sX = e.getX(e.findPointerIndex(ptrID1));
                    sY = e.getY(e.findPointerIndex(ptrID1));
                    ////////////////////////////////
                    start.set(x, y);
                    //mode = DRAG;
                    //lastEvent = null;

                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    ptrID2 = e.getPointerId(e.getActionIndex());
                    fX = e.getX(e.findPointerIndex(ptrID2));
                    fY = e.getY(e.findPointerIndex(ptrID2));
                    ///////////////////////////////
                    oldDist = spacing(e);
                    if (oldDist > 10f) {
                        midPoint(mid, e);
                       isZoomEnabled = true;
                    }
                    /////////////////////////////////
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) { //rotate case
                        float nfX, nfY, nsX, nsY;
                        nsX = e.getX(e.findPointerIndex(ptrID1));
                        nsY = e.getY(e.findPointerIndex(ptrID1));
                        nfX = e.getX(e.findPointerIndex(ptrID2));
                        nfY = e.getY(e.findPointerIndex(ptrID2));
                        mAngle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY);

                        if(isZoomEnabled){
                            float newDist = spacing(e);
                            float dx = ((x - mPreviousX)*2)/X_SCALE_FACTOR;
                            float dy = ((y - mPreviousY)*2)/Y_SCALE_FACTOR;
                            float dist = disTanceBetweenTwoPoints(x, y, mPreviousX, mPreviousY);
                            //Log.d("GestureActivity", "onTouchEvent: in zoom event. new dist="+newDist+ " oldDist="+oldDist+"  dist="+dist);
                            if (dist > 10f) {
                                float scale = (newDist / oldDist);
                                mRenderer.actionType = 3;
                                Log.d("GestureActivity", " scale="+scale);
                                float scaleFactorX = scale;
                                float scaleFactorY = scale;
                                mRenderer.scaleVertexBuffer(scaleFactorX, scaleFactorY);
                                requestRender();
                            }
                           // mRenderer.actionType = 2;
                           // mRenderer.setmAngle(mAngle);
                            // requestRender();
                        }
                    }else if(ptrID1 != INVALID_POINTER_ID){ //DRAG CASE
                        /*Coordinate of mouse touch point in OpenGL coordinate system*/
                        float x_cor = (float) (2.0 * x / X_SCALE_FACTOR - 1);
                        float y_cor = (float) (- 2.0 * y / Y_SCALE_FACTOR + 1);
                        //Log.d("GestureActivity", "onTouchEvent: in zoom event. x_cor="+x_cor+ " y_cor="+y_cor);
                        float dx = 0, dy=0;
                        float distanceBetweenCenterAndCurrentPoint = disTanceBetweenTwoPoints(topLayerCenter.x, topLayerCenter.y, x_cor, y_cor);
                       // Log.d("GestureActivity", "onTouchEvent: topLayerCenter.x=="+topLayerCenter.x+"  topLayerCenter.y="+topLayerCenter.y);
                        //Log.d("GestureActivity", "onTouchEvent: distanceBetweenCenterAndCurrentPoint=="+distanceBetweenCenterAndCurrentPoint);
                        if(distanceBetweenCenterAndCurrentPoint < 10f){

                            mRenderer.actionType = 1;
                            dx = ((x - mPreviousX)*2)/X_SCALE_FACTOR;
                            dy = ((y - mPreviousY)*2)/Y_SCALE_FACTOR;
                            Log.d("GestureActivity", "onTouchEvent: distanceBetweenCenterAndCurrentPoint=="
                                    +distanceBetweenCenterAndCurrentPoint+ " topLayerCenter.x="+topLayerCenter.x
                            +" topLayerCenter.y="+topLayerCenter.y);
                            //mRenderer.changeVertexBuffer(topLayerCenter, dx, -dy);
                            mRenderer.changeVertexBuffer(dx, -dy);
                            requestRender();
                            topLayerCenter.set(topLayerCenter.x+dx, topLayerCenter.y-dy);
                        }
                    }
                    mPreviousX = x;
                    mPreviousY = y;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    ptrID2 = INVALID_POINTER_ID;
                    break;
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

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float disTanceBetweenTwoPoints(float x, float y, float xPrev, float yPrev) {

        return (float) Math.sqrt( Math.abs(x-xPrev)*Math.abs(x-xPrev) + Math.abs(y-yPrev) * Math.abs(y-yPrev));
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * Calculate the degree to be rotated by.
     *
     * @param event
     * @return Degrees
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}