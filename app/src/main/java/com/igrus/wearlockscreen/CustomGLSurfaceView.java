package com.igrus.wearlockscreen;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Toast;
//import android.widget.Toast;

import java.util.Set;

public class CustomGLSurfaceView extends GLSurfaceView implements Renderer {
    private Curtain curtain;					//Our Stars class, managing all stars
    private float START_X, START_Y;
    private Context context;
    private int att=1;
    static public int ctct=1;
    private BluetoothAdapter mBlueToothAdapter=null;

    private long starttime;
    private long endtime;
    private SmS_Service sms;

    private int mode=1; //평소에 1 임시비번모드 2
    private int imsipass=0;
    static public int imsicorrect=1234;


    public CustomGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Set this as Renderer
        this.setRenderer(this);
        //Request focus
        this.requestFocus();
        this.setFocusableInTouchMode(true);

        //
        this.context = context;


    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //Settings
        gl.glEnable(GL10.GL_TEXTURE_2D);					//Enable Texture Mapping
        gl.glShadeModel(GL10.GL_SMOOTH); 					//Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 			//Black Background
        gl.glClearDepthf(1.0f); 							//Depth Buffer Setup

        //Really Nice Perspective Calculations
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        gl.glEnable(GL10.GL_BLEND);							//Enable blending
        gl.glDisable(GL10.GL_DEPTH_TEST);					//Disable depth test
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);		//Set The Blending Function For Translucency

        int height=0, width = 0;
        GLSurfaceView.getDefaultSize(height, getHeight());
        GLSurfaceView.getDefaultSize(width, getWidth());

        curtain = new Curtain();
        curtain.loadGLTexture(gl, this.context);
        mBlueToothAdapter= BluetoothAdapter.getDefaultAdapter();

        /*
        if(mBlueToothAdapter.isEnabled()==true)
            mBlueToothAdapter.disable();
            */
        context.registerReceiver(BTReceiver,
                new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
        context.registerReceiver(BTReceiver,
                new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
    }


    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        curtain.draw(gl);



        if(mBlueToothAdapter.isEnabled()==true && ctct==1 && System.currentTimeMillis()-starttime>5000 )
        {
            mBlueToothAdapter.disable();
        }

    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if(height == 0) { 						//Prevent A Divide By Zero By
            height = 1; 						//Making Height Equal One
        }

        gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
        gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
        gl.glLoadIdentity(); 					//Reset The Projection Matrix
        //GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

        gl.glOrthof(0, 768, 1024, 0, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
        gl.glLoadIdentity(); 					//Reset The Modelview Matrix
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
        if(mBlueToothAdapter == null){
            // 만약 블루투스 adapter가 없으면, 블루투스를 지원하지 않는 기기이거나 블루투스 기능을 끈 기기이다.
        }else{
            // 블루투스 adapter가 있으면, 블루투스 adater에서 페어링된 장치 목록을 불러올 수 있다.
            Set<BluetoothDevice> pairDevices = mBlueToothAdapter.getBondedDevices();
            //페어링된 장치가 있으면
            if(pairDevices.size()>0){
                Toast.makeText(context, "device111", Toast.LENGTH_SHORT).show();

                for(BluetoothDevice device : pairDevices ){
                    //페어링된 장치 이름과, MAC주소를 가져올 수 있다.


                    Toast.makeText(context, device.getName().toString() +" Device Is Connected!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(context, device.getAddress().toString() +" Device Is Connected!", Toast.LENGTH_SHORT).show();
                    if(device.getName().indexOf("Watch")!=-1
                            || device.getName().indexOf("Gear Live")!=-1
                            || device.getName().indexOf("Moto")!=-1
                            )
                    {

                       // att=2;
                        //Toast.makeText(context, "device"+device.getName().indexOf("Watch"), Toast.LENGTH_SHORT).show();
                    }
                }
            }else{
                Toast.makeText(context, "no Device", Toast.LENGTH_SHORT).show();
            }
        }
*/

        sms = new SmS_Service();
        DisplayMetrics dis = new DisplayMetrics();
        Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getMetrics(dis);
        int screenWidth = dis.widthPixels/2;
        int screenHeight = dis.heightPixels/3;

        int passwordcheckwidth = dis.widthPixels/3;
        int passwordcheckheight = dis.heightPixels/4;

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
//            Toast.makeText(context, "touch"+att, Toast.LENGTH_SHORT).show();
            START_X = event.getRawX();
            START_Y = event.getRawY();
            ctct = 1;           att = 1;

            if(mode==1)
            {
                if((double)passwordcheckheight*3.5<START_Y)
                {
                    Toast.makeText(context, "imsipassmode", Toast.LENGTH_SHORT).show();
                    mode=2;
                    curtain.mode=2;
                }
                else {
                    if (mBlueToothAdapter != null) {
                        if(mBlueToothAdapter.isEnabled()==false)
                            mBlueToothAdapter.enable();
                        else
                            mBlueToothAdapter.disable();
                    }
                    starttime = System.currentTimeMillis();
                }
            }
            else if(mode==2)
            {
                if(passwordcheckheight*3<START_Y && (passwordcheckwidth>START_X || passwordcheckwidth*2<START_X))
                {
                    Toast.makeText(context, "touchpassmode", Toast.LENGTH_SHORT).show();
                    mode=1;
                    curtain.mode=1;
                }
                else
                {
                    imsipass*=10;
                    imsipass+=(((int)START_X/passwordcheckwidth+1)+(((int)START_Y/passwordcheckheight)*3))%11;
                    Toast.makeText(context, "password :"+imsipass, Toast.LENGTH_SHORT).show();


                    if(imsipass>=1000)
                    {
                        if(imsipass==imsicorrect)
                            sms.removeview();
                        else
                            imsipass=0;
                    }
                    else
                    {
                        if(imsipass==imsicorrect)
                            sms.removeview();
                    }
                }
            }

            ///imsi
        }
        else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mBlueToothAdapter != null)
                mBlueToothAdapter.enable();
        }

        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            int x = (int)(event.getRawX() - START_X);	//�̵��� �Ÿ�
            int y = (int)(event.getRawY() - START_Y);	//�̵��� �Ÿ�
/*
            if((x > screenWidth || x < -screenWidth || y > screenHeight || y < -screenHeight)  && att==2   ){
//                Toast.makeText(context, "up"+att, Toast.LENGTH_LONG).show();
                sms.removeview();

            }
            */
            ctct=2;

        }

        return true;
    }
    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //Do something if connected
                ctct=2;

                Toast.makeText(context, "BT Connected"+device.getName(), Toast.LENGTH_SHORT).show();
                if(System.currentTimeMillis()-starttime<5000 && System.currentTimeMillis()-starttime>-1000) {
                    sms.removeview();
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Do something if disconnected
                att=2;
                Toast.makeText(context, "BT Disconnected"+device.getName(), Toast.LENGTH_SHORT).show();
                if(System.currentTimeMillis()-starttime<5000 && System.currentTimeMillis()-starttime>-1000) {
                    mBlueToothAdapter.enable();
                    sms.removeview();
                }


            }
            //else if...
        }
    };
}
