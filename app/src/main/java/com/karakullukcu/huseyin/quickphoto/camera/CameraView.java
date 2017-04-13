package com.karakullukcu.huseyin.quickphoto.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import static android.R.attr.width;

/**
 * Created by pc on 12.04.2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraView.class.getName();

    private SurfaceHolder mHolder;
    private CameraController cameraController;
    private Camera.Parameters cameraParameters;
    private DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;

    private GestureDetector mGestureDetector;


    public CameraView(Context context) {
        super(context);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mGestureDetector = new GestureDetector(context, new CameraGestureListener());

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        cameraController = CameraController.getInstance();
        cameraController.initCamera();
        cameraParameters = cameraController.getCameraParameters();
        float displayRatio = (float) Math.max(displayMetrics.widthPixels,displayMetrics.heightPixels) /
                (float) Math.min(displayMetrics.widthPixels,displayMetrics.heightPixels);

        mPreviewSize = cameraController.getOptimalSize(cameraParameters.getSupportedPreviewSizes(),
                this.getMeasuredWidth(), this.getMeasuredHeight(), displayRatio);

        mPictureSize = cameraController.getOptimalSize(cameraParameters.getSupportedPictureSizes(),
                this.getMeasuredWidth(), this.getMeasuredHeight(), displayRatio);
        mHolder.addCallback(this);
        cameraController.startPreview(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        resizeCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        cameraController.stopPreview();
        mHolder.removeCallback(this);
        cameraController.closeCamera();
        Log.d(TAG,"Surface Destroyed");
    }

    private void resizeCamera() {
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        cameraController.stopPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here
        cameraController.setCameraOrientation(90);
        cameraParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        cameraParameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        Log.d(TAG,"preview width: "+ mPreviewSize.width+" preview height: "+ mPreviewSize.height);
        Log.d(TAG,"photo width: "+ mPictureSize.width+" photo height: "+ mPictureSize.height);
        cameraController.setCameraParameters(cameraParameters);
        // start preview with new settings
        try {
            cameraController.startPreview(mHolder);

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private class CameraGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            surfaceDestroyed(mHolder);
            cameraController.switchCameraID();
            surfaceCreated(mHolder);
            resizeCamera();
            return true;
        }
    }
}
