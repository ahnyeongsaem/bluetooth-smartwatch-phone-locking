package com.igrus.wearlockscreen;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.provider.Settings.SettingNotFoundException;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends PreferenceActivity implements OnPreferenceClickListener  {

    public DevicePolicyManager devicePolicyManager;
    public ComponentName adminComponent;
    LayoutInflater inflater;
    final static int Dialog_1 = 0;
    static int startcheck=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, SmS_Service.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,1,intent,0);
        Notification noti = new Notification(R.drawable.ic_launcher,"test",System.currentTimeMillis());
        noti.setLatestEventInfo(this,"title","test",pendingIntent);
        noti.flags=noti.flags|Notification.FLAG_ONGOING_EVENT;

        inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);

//        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
//        CustomGLSurfaceView.imsicorrect = sharedPreferences.getInt("imsicorrect", 1234);


        if(startcheck==1)
        {
            finish();
            startcheck =0;
        }



            addPreferencesFromResource(R.xml.pref);
            Preference Reen_Service = (Preference) findPreference("Reen_service");
            Preference Disable_Service = (Preference) findPreference("Disable_service");
            Preference Reen_DPM = (Preference) findPreference("Reen_device_policy");
            Preference Disable_DPM = (Preference) findPreference("Disable_device_policy");
            Preference Re_LockNow = (Preference) findPreference("Request_LockNow");                        //잠금화면 카테고리
            Preference Re_PassScreen = (Preference) findPreference("Request_PasswordScreen");            //원격잠금 카테고리
            Preference isPass = (Preference) findPreference("Request_isPass");                    //원격잠금 카테고리
            Preference Re_image = (Preference) findPreference("Request_ImageSelect");
            Preference ImsiImage = (Preference) findPreference("Request_ImsiPassword");


            Reen_Service.setOnPreferenceClickListener(this);
            Disable_Service.setOnPreferenceClickListener(this);
            Reen_DPM.setOnPreferenceClickListener(this);
            Disable_DPM.setOnPreferenceClickListener(this);
            Re_LockNow.setOnPreferenceClickListener(this);
            Re_PassScreen.setOnPreferenceClickListener(this);
            isPass.setOnPreferenceClickListener(this);
            Re_image.setOnPreferenceClickListener(this);
            ImsiImage.setOnPreferenceClickListener(this);

    }
    protected void onActivityResult(int requestCode,int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode,resultCode,intent);
        Toast.makeText(this, ""+requestCode, Toast.LENGTH_SHORT).show();
        if(resultCode==RESULT_OK && requestCode==0)
        {
            try {
                //Curtain.iss = getContentResolver().openInputStream(intent.getData());
                Curtain.bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),intent.getData());

                Curtain.selectimage=1;


                SharedPreferences sharedPreferences = getSharedPreferences("SP",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("iss", intent.getData().toString());
                editor.putInt("selectimage",Curtain.selectimage);
                editor.commit();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean onPreferenceClick(Preference preference) {
        adminComponent = new ComponentName(this, Ex_DPM.DpmClass.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        boolean is_pass=false;

        if(preference.getKey().equals("Reen_device_policy"))
        {
            if (!devicePolicyManager.isAdminActive(adminComponent)){
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Additional text explaining why this needs to be added.");
                startActivityForResult(intent, 1);
            }
            else
                Toast.makeText(this, "이미 기기권한이 사용 중 입니다.", Toast.LENGTH_SHORT).show();
        }
        else if(preference.getKey().equals("Disable_device_policy"))
        {
            if (devicePolicyManager.isAdminActive(adminComponent)){
                Toast.makeText(this, "기기권한이 해제되었습니다.", Toast.LENGTH_SHORT).show();
                devicePolicyManager.removeActiveAdmin(adminComponent);
            }
            else
                Toast.makeText(this, "이미 기기권한이 해제되어 있습니다.", Toast.LENGTH_SHORT).show();
        }
        else if(preference.getKey().equals("Reen_service"))
        {
            startService(new Intent("sms_service"));
        }
        else if(preference.getKey().equals("Disable_service"))
        {
            stopService(new Intent("sms_service"));
        }
        else if(preference.getKey().equals("Request_ImsiPassword"))
        {
            showDialog(Dialog_1);
        }
        else if(preference.getKey().equals("Request_ImageSelect"))
        {
            Intent choosePictureIntent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(choosePictureIntent,0);
        }
        else if(preference.getKey().equals("Request_LockNow"))
        {
            if (devicePolicyManager.isAdminActive(adminComponent))
                devicePolicyManager.lockNow();

            else
                Toast.makeText(this, "기기권한을 설정하셔야 합니다.", Toast.LENGTH_SHORT).show();
        }
        else if(preference.getKey().equals("Request_PasswordScreen"))
        {
            Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
            startActivity(intent);
        }
        else if(preference.getKey().equals("Request_isPass"))
        {
            String PASSWORD_TYPE_KEY = "lockscreen.password_type";
            try {
                final boolean isPattern = 1 ==
                        android.provider.Settings.System.getLong(getContentResolver(),
                                android.provider.Settings.System.LOCK_PATTERN_ENABLED);

                long mode = android.provider.Settings.Secure.getLong(getContentResolver(),
                        PASSWORD_TYPE_KEY);
                final boolean isPassword =
                        DevicePolicyManager.PASSWORD_QUALITY_NUMERIC == mode
                                || DevicePolicyManager.PASSWORD_QUALITY_ALPHABETIC == mode
                                || DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC == mode;
                is_pass = isPassword || isPattern;

            } catch (SettingNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if(is_pass)
                Toast.makeText(this, "비밀번호 사용", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "비밀번호 미사용", Toast.LENGTH_SHORT).show();
        }

        // TODO Auto-generated method stub
        return false;

    }


    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case Dialog_1:
                final LinearLayout linear = (LinearLayout)inflater.inflate(R.layout.dialog_layout, null);

                return new AlertDialog.Builder(MainActivity.this)
                        .setTitle("임시 비밀번호 설정")
                        .setIcon(R.drawable.ic_launcher)
                        .setView(linear)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText editname = (EditText) linear.findViewById(R.id.edit);
                                CustomGLSurfaceView.imsicorrect = Integer.parseInt(editname.getText().toString());

                                SharedPreferences sharedPreferences = getSharedPreferences("SP",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("imsicorrect", CustomGLSurfaceView.imsicorrect);
                                editor.commit();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create();
        }
        return null;
    }

}
