package com.igrus.wearlockscreen;

import android.app.Activity;
import android.os.Bundle;

public class Lockscreen extends Activity {
    private CustomGLSurfaceView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (CustomGLSurfaceView) findViewById(R.id.surface_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mView.onResume();
    }

    /**
     * Also pause our Lesson
     */
    @Override
    protected void onPause() {
        super.onPause();
        mView.onPause();
    }
}
