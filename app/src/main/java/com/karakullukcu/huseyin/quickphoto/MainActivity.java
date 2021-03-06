package com.karakullukcu.huseyin.quickphoto;

import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private PhotoPreviewFragment previewFragment;

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            getSupportFragmentManager().beginTransaction().remove(previewFragment).commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hideSystemUI();

        // Transparent system ui after api 19.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        final Handler handler = new Handler();
        final Runnable hideUiTask = new Runnable() {
            @Override
            public void run() {
                hideSystemUI();
            }
        };

        // System ui disappear 3 seconds after showed up.
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    handler.postDelayed(hideUiTask,3000);
                } else {
                    handler.removeCallbacks(hideUiTask);
                }
            }
        });

        Window window = getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.rotationAnimation = WindowManager.LayoutParams.ROTATION_ANIMATION_CROSSFADE;
        window.setAttributes(windowParams);

        FragmentManager manager = getSupportFragmentManager();
        previewFragment = (PhotoPreviewFragment) manager.findFragmentByTag(getString(R.string.photo_preview_fragment_key));
        FragmentTransaction transaction = manager.beginTransaction();
        if (previewFragment == null) {
            transaction.replace(R.id.fragmentContainerLayout, new CameraPreviewFragment());
        } else {
            transaction.replace(R.id.fragmentContainerLayout, previewFragment);
        }

        transaction.commit();
    }


    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
