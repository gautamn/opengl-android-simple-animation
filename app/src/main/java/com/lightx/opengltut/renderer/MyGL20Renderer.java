package com.lightx.opengltut.renderer;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lightx.opengltut.shape.Sprite;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Nitin Gautam on 15/02/2018.
 */

public class MyGL20Renderer implements GLSurfaceView.Renderer
{
    private final Context mActivityContext;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mModelMatrix=new float[16];
    private Sprite sprite;

    public MyGL20Renderer(final Context activityContext){
        mActivityContext = activityContext;
    }

    public void onSurfaceCreated(GL10 unused, javax.microedition.khronos.egl.EGLConfig eglConfig){
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        sprite = new Sprite(mActivityContext);
    }

    public void onDrawFrame(GL10 unused)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -1.5f);
        //Matrix.scaleM(mModelMatrix, 0, 2, 2, 1.0f);
        //Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);


        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        sprite.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height){
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}