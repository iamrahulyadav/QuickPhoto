package com.karakullukcu.huseyin.quickphoto.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by pc on 12.04.2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = CameraView.class.getName();

    private SurfaceHolder mHolder;
    private CameraController cameraController;
    private Camera.Parameters cameraParameters;
    private DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;


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

        cameraController = CameraController.getInstance();
        cameraController.initCamera();
        cameraParameters = cameraController.getCameraParameters();
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG,"measure width: "+width+" measure height: "+height );
        setMeasuredDimension(width,height);

        float displayRatio = (float) Math.max(displayMetrics.widthPixels,displayMetrics.heightPixels) /
                (float) Math.min(displayMetrics.widthPixels,displayMetrics.heightPixels);

        mPreviewSize = cameraController.getOptimalSize(cameraParameters.getSupportedPreviewSizes(),
                width, height, displayRatio);

        mPictureSize = cameraController.getOptimalSize(cameraParameters.getSupportedPictureSizes(),
                width, height, displayRatio);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        cameraController.startPreview(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
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
        Log.d(TAG,"preview width: "+ mPictureSize.width+" preview height: "+ mPictureSize.height);
        cameraController.setCameraParameters(cameraParameters);
        // start preview with new settings
        try {
            cameraController.startPreview(surfaceHolder);

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        cameraController.stopPreview();
        cameraController.closeCamera();
        Log.d(TAG,"Surface Destroyed");
    }
}
