package com.lightx.opengltut.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lightx.opengltut.shape.Square;

import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Square mSquare;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float mAngle;
    private Square mSquare2;

    @Override
    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mSquare = new Square();
        mSquare2 = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

   /* @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        mSquare.draw(mMVPMatrix);
    }*/

   private static float x = 0.0f;
   private static boolean movingRight = false;
    private static boolean movingUp = false;
    private static float y = 0.0f;

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.translateM(mViewMatrix, 0, x, 0.5f, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        mSquare.draw(mMVPMatrix);

        x=x+getDeltaX();

        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.translateM(mViewMatrix, 0, 0.5f, y, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        mSquare2.draw(mMVPMatrix);
        y=y+getDeltaY();
    }

    private static float getDeltaX(){

        float delta = 0.0f;
        if (movingRight) // If we are moving right
            delta = 0.005f;
        else  // Otherwise
            delta = -0.005f;
        if (x <= -0.5f) // If we have gone left too far
            movingRight = true; // Reverse our direction so we are moving right
        else if (x >= 0.5f) // Else if we have gone down too far
            movingRight = false; // Reverse our direction so we are moving left

        return  delta;
    }

    private static float getDeltaY(){

        float delta = 0.0f;
        if (movingUp) // If we are moving right
            delta = 0.005f;
        else  // Otherwise
            delta = -0.005f;
        if (y <= -0.5f) // If we have gone left too far
            movingUp = true; // Reverse our direction so we are moving right
        else if (y >= 0.5f) // Else if we have gone down too far
            movingUp = false; // Reverse our direction so we are moving left

        return  delta;
    }

   /**Rotation of two squares*/

    /*private float[] mRotationMatrix = new float[16];
    public void onDrawFrame(GL10 unused) {

        float[] scratch = new float[16];
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mSquare.draw(scratch);

        // Set the camera position (View matrix)
        scratch = new float[16];
        Matrix.setRotateM(mRotationMatrix, 0, -mAngle*4, 0.0f, 0.0f, 1.0f);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        mSquare.draw(scratch);

        mAngle+=.4;

    }*/




   /* private float[] mTranslationalMatrix = new float[16];
    public void onDrawFrame(GL10 unused) {

        *//*Drawing pink square*//*
        float[] scratch = new float[16];
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
       // Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, 0.2f, 0.1f, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        mSquare.draw(scratch);
        //mAngle+=.4;

    }*/

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }
}