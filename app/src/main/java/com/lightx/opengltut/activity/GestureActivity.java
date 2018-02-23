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

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();

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
        private float mPreviousX = 0;
        private float mPreviousY = 0;
        /*private boolean isZoomEnabled = false;*/

        @Override
        public boolean onTouchEvent(MotionEvent e) {

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    start.set(x, y);
                    mode = DRAG;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:

                    break;

                case MotionEvent.ACTION_MOVE: {

                    if (mode == DRAG) {
                        float dx = (e.getX() - start.x) * 2 / X_SCALE_FACTOR;
                        float dy = (e.getY() - start.y) * 2 / Y_SCALE_FACTOR;
                        mRenderer.actionType = 1;
                        mRenderer.changeVertexBuffer(dx, -dy);
                        requestRender();
                        start.set(x, y);
                    }
                }
                break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
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

        return (float) Math.sqrt(Math.abs(x - xPrev) * Math.abs(x - xPrev) + Math.abs(y - yPrev) * Math.abs(y - yPrev));
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