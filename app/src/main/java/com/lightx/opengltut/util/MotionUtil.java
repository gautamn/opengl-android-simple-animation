package com.lightx.opengltut.util;

import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Created by Nitin Gautam on 23/02/2018.
 */

public class MotionUtil {

    private static final float X_SCALE_FACTOR = 1080;
    private static final float Y_SCALE_FACTOR = 1920;

    public static float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX, float nfY, float nsX, float nsY) {

        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));
        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return angle;
    }

    /**
     * Determine the space between the first two fingers
     */
    public static float spacing(MotionEvent event) {
        float x = (event.getX(0) - event.getX(1))*2/X_SCALE_FACTOR;
        float y = (event.getY(0) - event.getY(1))*2/Y_SCALE_FACTOR;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Determine the space between the first two fingers
     */
    public static float spacingBetweenFingers(MotionEvent event) {
        float x = (event.getX(0) - event.getX(1));
        float y = (event.getY(0) - event.getY(1));
        return (float) Math.sqrt(x * x + y * y);
    }

    private float disTanceBetweenTwoPoints(float x, float y, float xPrev, float yPrev) {

        return (float) Math.sqrt(Math.abs(x - xPrev) * Math.abs(x - xPrev) + Math.abs(y - yPrev) * Math.abs(y - yPrev));
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    public static void midPoint(PointF point, MotionEvent event) {
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
    public static float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    public static float disTanceBetweenTwoSides(float dx, float dy) {

        return (float) Math.sqrt(dx*dx + dy*dy);
    }
}
