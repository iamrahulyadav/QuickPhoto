package com.karakullukcu.huseyin.quickphoto.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import com.karakullukcu.huseyin.quickphoto.PhotoPreviewFragment;
import com.karakullukcu.huseyin.quickphoto.R;

import java.io.FileOutputStream;
import java.io.IOException;
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
    private boolean isFrontCamera = false;
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
            if (isFrontCamera) {
                c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        } catch (Exception e) {
            Log.d(TAG,"Error opening camera");
            e.printStackTrace();
        }
        return c;
    }


    public void initCamera() {
        mCamera = getCameraInstance();
        if (mCamera != null) {
            mCameraParameters = mCamera.getParameters();
        }
    }

    public void switchCameraID() {
        isFrontCamera = !isFrontCamera;
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

    public void takePicture(final Context context) {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    Matrix matrix = new Matrix();
                    if (isFrontCamera) {
                        matrix.postRotate(-90);
                        matrix.postScale(-1,1,imageBitmap.getWidth()/2f,imageBitmap.getHeight()/2f);
                    } else {
                        matrix.postRotate(90);
                    }

                    Bitmap correctedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(),
                            imageBitmap.getHeight(), matrix, true);
                    imageBitmap.recycle();
                    FileOutputStream fos = null;

                    try {
                        fos = context.openFileOutput(context.getString(R.string.image_name_for_storage)
                                ,Context.MODE_PRIVATE);
                        correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    PhotoPreviewFragment photoPreviewFragment = new PhotoPreviewFragment();
                    AppCompatActivity activity = (AppCompatActivity) context;
                    FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainerLayout,photoPreviewFragment,context.getString(R.string.photo_preview_fragment_key));
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
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
