package com.igrus.wearlockscreen;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Ex_DPM extends Activity {



    public static class DpmClass extends DeviceAdminReceiver{
        static SharedPreferences getSamplePreferences(Context context) {
            return context.getSharedPreferences(DeviceAdminReceiver.class.getName(), 0);
        };

        void showToast(Context context, CharSequence msg) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnabled(Context context, Intent intent) {

            showToast(context, "Device Admin enabled");
            context.stopService(new Intent("sms_service"));
            context.startService(new Intent("sms_service"));
        }
        @Override
        public CharSequence onDisableRequested(Context context, Intent intent) {
            return "This is an optional message to warn the user about disabling.";
        }
        @Override
        public void onDisabled(Context context, Intent intent) {

            showToast(context, "Device Admin Disabled");
        }
        @Override
        public void onPasswordChanged(Context context, Intent intent) {

            showToast(context, "Password Change");
            context.stopService(new Intent("sms_service"));
            context.startService(new Intent("sms_service"));
        }
        @Override
        public void onPasswordFailed(Context context, Intent intent) {

            showToast(context, "PasswordFailed");
        }
        @Override
        public void onPasswordSucceeded(Context context, Intent intent) {

            showToast(context, "PasswordSucceeded");
        }
    }

}
