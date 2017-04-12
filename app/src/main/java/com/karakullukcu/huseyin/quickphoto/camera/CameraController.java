package com.karakullukcu.huseyin.quickphoto.camera;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;

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
}
