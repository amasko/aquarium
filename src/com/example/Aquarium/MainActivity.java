package com.example.Aquarium;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    MainView view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        view = new MainView(this);
        setContentView(view);
        Log.d(TAG, "View added");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        view.threadsStop();
        this.finish();
        Log.w(TAG, "seems to be finished ");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
