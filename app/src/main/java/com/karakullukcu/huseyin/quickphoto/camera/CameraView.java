package com.karakullukcu.huseyin.quickphoto.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by pc on 12.04.2017.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = CameraView.class.getName();

    private SurfaceHolder mHolder;
    private CameraController mCameraController;
    private Camera.Parameters mCameraParameters;
    private DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
    private Camera.Size mPreviewSize;
    private Camera.Size mPictureSize;

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;


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
        mHolder.addCallback(this);
        mGestureDetector = new GestureDetector(context, new CameraGestureListener());
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        mCameraController = CameraController.getInstance();
        mCameraController.initCamera();
        mCameraParameters = mCameraController.getCameraParameters();
        float displayRatio = (float) Math.max(mDisplayMetrics.widthPixels,mDisplayMetrics.heightPixels) /
                (float) Math.min(mDisplayMetrics.widthPixels,mDisplayMetrics.heightPixels);

        mPreviewSize = mCameraController.getOptimalSize(mCameraParameters.getSupportedPreviewSizes(),
                this.getMeasuredWidth(), this.getMeasuredHeight(), displayRatio);

        mPictureSize = mCameraController.getOptimalSize(mCameraParameters.getSupportedPictureSizes(),
                this.getMeasuredWidth(), this.getMeasuredHeight(), displayRatio);
        mHolder.addCallback(this);
        mCameraController.startPreview(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        resizeCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCameraController.stopPreview();
        mHolder.removeCallback(this);
        mCameraController.closeCamera();
        Log.d(TAG,"Surface Destroyed");
    }

    private void resizeCamera() {
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        mCameraController.stopPreview();

        // set preview size and make any resize, rotate or
        // reformatting changes here
        mCameraController.setCameraOrientation(90);
        mCameraParameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        mCameraParameters.setPictureSize(mPictureSize.width, mPictureSize.height);
        Log.d(TAG,"preview width: "+ mPreviewSize.width+" preview height: "+ mPreviewSize.height);
        Log.d(TAG,"photo width: "+ mPictureSize.width+" photo height: "+ mPictureSize.height);
        mCameraController.setCameraParameters(mCameraParameters);
        // start preview with new settings
        try {
            mCameraController.startPreview(mHolder);

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        int difference =  (int) detector.getCurrentSpan() -  (int) detector.getPreviousSpan();
        Log.d("CameraScale","ScaleFactor: "+difference);
        int maxZoom = mCameraParameters.getMaxZoom();
        int zoom =  mCameraParameters.getZoom() + difference / (maxZoom / 5);
        if (zoom > maxZoom) {
            zoom = maxZoom;
        } else if (zoom < 0) {
            zoom = 0;
        }

        if (mCameraParameters.isZoomSupported()) {
            mCameraParameters.setZoom(zoom);

        }
        Log.d("CameraScale","Zoom: "+mCameraParameters.getZoom());
        mCameraController.setCameraParameters(mCameraParameters);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    private class CameraGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mCameraController.takePicture(getContext());
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            surfaceDestroyed(mHolder);
            mCameraController.switchCameraID();
            surfaceCreated(mHolder);
            resizeCamera();
            return true;
        }
    }
}
