package com.karakullukcu.huseyin.quickphoto.camera;

import android.hardware.Camera;
import android.util.Log;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by pc on 12.04.2017.
 */

public class CameraController {
    private static final String TAG = CameraController.class.getName();

    private Camera mCamera;
    private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Camera.Parameters mCameraParameters;

    private static CameraController instance;

    private CameraController() {}

    public static CameraController getInstance() {
        if (instance == null) {
            instance = new CameraController();
        }
        return instance;
    }

    private Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(cameraID);
        } catch (Exception e) {
            Log.d(TAG,"Error opening camera");
            e.printStackTrace();
        }
        return c;
    }

    private Camera getCameraInstance(int cameraID) {
        this.cameraID = cameraID;
        return getCameraInstance();
    }

    public void initCamera() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            mCameraParameters = mCamera.getParameters();
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    public Camera.Parameters getCameraParameters() {
        return mCameraParameters;
    }

    public void setCameraParameters(Camera.Parameters parameters) {
        mCamera.setParameters(parameters);
        mCameraParameters = parameters;
    }

    public void setCameraOrientation(int rotation) {
        mCamera.setDisplayOrientation(rotation);
    }

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
            mCameraParameters = null;
        }
    }

    public void startPreview(SurfaceHolder displayHolder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(displayHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                Log.d(TAG,"Error starting camera preview");
                e.printStackTrace();
            }
        }
    }

    public void stopPreview() {
        if (mCamera != null)
            mCamera.stopPreview();
    }

    public Size getOptimalSize(List<Size> sizes, int width, int height, float ratio) {
        List<Size> optimalSizes = new ArrayList<>();
        for (Size size : sizes) {
            if (Math.max(size.height,size.width) == Math.min(size.height,size.width) * ratio
                    && Math.max(size.height,size.width) >= Math.max(width,height)
                    && Math.min(size.height,size.width) >= Math.min(width,height) ) {
                optimalSizes.add(size);
            }
        }

        if (optimalSizes.size() > 0) {
            /**
             * I use min optimal size for this app.
             * If you want better picture resolution and preview size, you might want get max optimal size.
             */
            return Collections.min(optimalSizes, new CompareSizesByArea());
        } else {
            return Collections.max(sizes, new CompareSizesByArea());
        }
    }

    private static class CompareSizesByArea implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            return Long.signum((long) lhs.width * lhs.height - (long) rhs.width * rhs.height);
        }
    }
}
