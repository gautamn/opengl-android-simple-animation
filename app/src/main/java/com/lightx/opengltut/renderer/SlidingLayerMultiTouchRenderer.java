package com.lightx.opengltut.renderer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.lightx.opengltut.R;
import com.lightx.opengltut.shape.Layer;
import com.lightx.opengltut.util.ShaderUtil;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SlidingLayerMultiTouchRenderer implements GLSurfaceView.Renderer {

    private Layer baseLayer;
    private Layer topLayer;
    private Context mActivityContext;

    private static float square_1_coords[] = {
            -1f,  1f, 0.0f,   // top left
            -1f, -1f, 0.0f,   // bottom left
            1f, -1f, 0.0f,   // bottom right
            1f,  1f, 0.0f  // top right
    };

    private static float square_2_coords[] = {
            -0.25f,  0.25f, 0.0f,   // top left
            -0.25f, -0.25f, 0.0f,   // bottom left
            0.25f, -0.25f, 0.0f,   // bottom right
            0.25f,  0.25f, 0.0f  // top right
    };

    private float[] mProjectionMatrix = new float[16];
    private float[] mViewMatrix = new float[16];

    public float getmAngle() {
        return mAngle;
    }

    public void setmAngle(float mAngle) {
        this.mAngle = mAngle;
    }

    private float mAngle;

    public SlidingLayerMultiTouchRenderer(final Context activityContext){
        mActivityContext = activityContext;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        String fragmentShaderCode = ShaderUtil.readTextFileFromRawResource(mActivityContext, R.raw.texture_fragment_shader);
        baseLayer = new Layer(mActivityContext, square_1_coords, R.drawable.sam, fragmentShaderCode);
        fragmentShaderCode = ShaderUtil.readTextFileFromRawResource(mActivityContext, R.raw.texture_fragment_shader_edge_detection);
        topLayer = new Layer(mActivityContext, square_2_coords, R.drawable.vd, fragmentShaderCode);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    private float[] mRotationMatrix = new float[16];

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    private static float x;
    private static float y;
    private static boolean movingRight = false;
    private static boolean movingUp = false;

    @Override
    public void onDrawFrame(GL10 gl10) {
        float[] mMVPMatrix = new float[16];
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        baseLayer.draw(mMVPMatrix);

        mMVPMatrix = new float[16];
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.setRotateM(mRotationMatrix, 0, -mAngle, 0, 0, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        topLayer.draw(mMVPMatrix, square_2_coords);
    }

    public void changeVertexBuffer(float x, float y) {

        for(int i=0; i< square_2_coords.length; ){
            square_2_coords[i] = square_2_coords[i] + x;
            i = i + 3;
        }

        for(int i=1; i< square_2_coords.length; ){
            square_2_coords[i] = square_2_coords[i] + y;
            i = i + 3;
        }
    }


}
