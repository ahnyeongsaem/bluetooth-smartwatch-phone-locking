package com.igrus.wearlockscreen;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by temp on 2014-11-25.
 */
public class BootReceiver extends BroadcastReceiver {
    DevicePolicyManager devicePolicyManager;
    ComponentName adminComponent;
    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "helpme", Toast.LENGTH_SHORT).show();
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {

            SharedPreferences sharedPreferences = context.getSharedPreferences("SP", Context.MODE_PRIVATE);

         //   MainActivity.startcheck=1;
            Toast.makeText(context, "boot Serviddce", Toast.LENGTH_SHORT);
          //  Intent in = new Intent(context,MainActivity.class);
           // in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Intent in2 = new Intent(context,SmS_Service.class);
            context.startService(in2);
            //context.startActivity(in);
            CustomGLSurfaceView.imsicorrect = sharedPreferences.getInt("imsicorrect", 1234);
            Curtain.selectimage=sharedPreferences.getInt("selectimage",0);
            if(Curtain.selectimage==1)
            {
                try {
                    Curtain.bitmap= MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(sharedPreferences.getString("iss","")));
                } catch (IOException e) {
                    Curtain.selectimage=0;
                }

                // Curtain.iss= new ByteArrayInputStream(sharedPreferences.getString("iss","").getBytes());
            }
            devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            adminComponent = new ComponentName(context, Ex_DPM.DpmClass.class);
            if (devicePolicyManager.isAdminActive(adminComponent)){
                devicePolicyManager.lockNow();


            }else{
                Toast.makeText(context, "boot Service", Toast.LENGTH_SHORT);
            }

        }
    }
}
