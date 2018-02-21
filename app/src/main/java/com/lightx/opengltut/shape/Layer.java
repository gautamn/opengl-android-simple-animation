package com.lightx.opengltut.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.lightx.opengltut.R;
import com.lightx.opengltut.renderer.MyGLRenderer;
import com.lightx.opengltut.util.ShaderUtil;
import com.lightx.opengltut.util.TextureUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Layer {
    private final Context mActivityContext;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    private final int mTextureCoordinateDataSize = 2;
    private int mTextureDataHandle;

    private final int shaderProgram;
    private FloatBuffer vertexBuffer=null;
    private ShortBuffer indexBuffer=null;
    private FloatBuffer texCoordBuffer=null;

    private int mPositionHandle;
    private int mMVPMatrixHandle;

    private static final int COORDS_PER_VERTEX = 3;
    private final int vertexStride = COORDS_PER_VERTEX * 4; //Bytes per vertex
    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = {0.63671875f, 0.76953125f, 0.22265625f, 1.0f};

    private	float textureCoords[] ={
            0.0f, 0.0f,  // top left
            0.0f, 1.0f,  // bottom left
            1.0f, 1.0f,  // bottom right
            1.0f, 0.0f // top right
    };

    public Layer(final Context activityContext, float[] coordinates, int resourceId, String fragmentShaderCode) {

        mActivityContext = activityContext;
        vertexBuffer = initFloatBuffer(coordinates);
        texCoordBuffer = initFloatBuffer(textureCoords);

        ByteBuffer dlb = ByteBuffer.allocateDirect(coordinates.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        indexBuffer = dlb.asShortBuffer();
        indexBuffer.put(drawOrder);
        indexBuffer.position(0);

        String vertexShaderCode = ShaderUtil.readTextFileFromRawResource(activityContext, R.raw.texture_vertex_shader);
        //Log.d("Sprite", "vertex shader source code="+vertexShaderCode);
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        Log.i("Sprite", "Sprite: vertexShader="+vertexShader);

        //fragmentShaderCode = ShaderUtil.readTextFileFromRawResource(activityContext, R.raw.texture_fragment_shader);
        //Log.d("Sprite", "fragment shader source code="+fragmentShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        Log.i("Sprite", "Sprite: fragmentShader="+fragmentShader);

        shaderProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(shaderProgram, vertexShader);
        GLES20.glAttachShader(shaderProgram, fragmentShader);
        GLES20.glLinkProgram(shaderProgram);
        mTextureDataHandle = TextureUtil.loadTexture(mActivityContext, resourceId);

    }

    private FloatBuffer initFloatBuffer(float[] buffer)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(buffer.length * 4);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer aFloatBuffer = bb.asFloatBuffer();
        aFloatBuffer.put(buffer);
        aFloatBuffer.position(0);
        return aFloatBuffer;
    }

    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(shaderProgram);
        mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram, "u_Texture");

        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        texCoordBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,texCoordBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void draw(float[] mvpMatrix, float[] vertexCoord) {

        GLES20.glUseProgram(shaderProgram);
        mPositionHandle = GLES20.glGetAttribLocation(shaderProgram, "a_Position");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(shaderProgram, "a_TexCoordinate");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(shaderProgram, "u_MVPMatrix");
        mTextureUniformHandle = GLES20.glGetUniformLocation(shaderProgram, "u_Texture");

        vertexBuffer = initFloatBuffer(vertexCoord);
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        texCoordBuffer.position(0);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 0,texCoordBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}



