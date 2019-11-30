package com.igrus.wearlockscreen;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.opengl.GLUtils;
import android.os.Build;
import android.provider.MediaStore;

import java.io.InputStream;

public class Curtain{
    private Aether aethers;					//Hold all our star instances in this array

    private float zoom = -15.0f;			//Distance Away From Stars
    private int[] textures = new int[5];
    static public Bitmap bitmap,bitmap2;
    static public int selectimage=0;
    static public Uri imageFilePath;
    static public InputStream iss;
    public int mode=1;

    public Curtain() {

        //Initiate the stars array
        aethers = new Aether();
        //Initiate our stars with random colors and increasing distance
    }

    public void draw(GL10 gl) {
        if(mode==1)
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        else
            gl.glBindTexture(GL10.GL_TEXTURE_2D,textures[1]);
        //Recover the current star into an object
        Aether aether = aethers;

        gl.glLoadIdentity();							//Reset The Current Modelview Matrix

        gl.glTranslatef(0.0f, 0.0f, 0.0f); 				//Zoom Into The Screen (Using The Value In 'zoom')

        aether.draw(gl);

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void loadGLTexture(GL10 gl, Context context) {
        if(selectimage==0)
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.more_small);
        else if(selectimage==1)
        {
//            BitmapFactory.Options bmpFactoryOptions=new BitmapFactory.Options();
//            bmpFactoryOptions.inSampleSize=1;
//            bmpFactoryOptions.inPurgeable=true;
//            bmpFactoryOptions.inDither=true;
//            bitmap= BitmapFactory.decodeStream(iss,null,bmpFactoryOptions);
            selectimage++;

        }
        bitmap2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.button);
        Bitmap sideInversionImg = null,sideInversionImg2=null;
        Matrix m = new Matrix();
        m.setScale(-1,1);
        m.setScale(1, -1);

   //         sideInversionImg = Bitmap.createBitmap(bitmap, 0, 0,
    //                bitmap.getWidth(), bitmap.getHeight(), m, false);
        Bitmap tmpsideInversionImg=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),false);
        sideInversionImg = Bitmap.createBitmap(tmpsideInversionImg, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), m, false);
            sideInversionImg2 = Bitmap.createBitmap(bitmap2, 0, 0,
                    bitmap2.getWidth(), bitmap2.getHeight(), m, false);

        gl.glGenTextures(1, textures, 0);

        //Create Linear Filtered Texture and bind it to texture
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, sideInversionImg, 0);


            gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[1]);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, sideInversionImg2, 0);



        //Clean up
        sideInversionImg.recycle();

    }
}
