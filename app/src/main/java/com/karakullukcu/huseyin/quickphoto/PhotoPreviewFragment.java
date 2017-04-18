package com.karakullukcu.huseyin.quickphoto;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.karakullukcu.huseyin.quickphoto.view.DescriptionEditText;


public class PhotoPreviewFragment extends Fragment implements GestureDetector.OnGestureListener,
    View.OnTouchListener {
    private DescriptionEditText mDescriptionEditText;
    private GestureDetector mGestureDetector;
    private boolean isKeyBoardVisible = false;
    private RelativeLayout.LayoutParams mDescriptionLayoutParams;
    private DisplayMetrics mDisplayMetrics;
    private Bundle savedInstance = null;
    private Bitmap imageBitmap;

    public PhotoPreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mDescriptionLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (mDescriptionEditText.getText().toString().trim().isEmpty()) {
            mDescriptionEditText.setVisibility(View.GONE);
        } else {
            if (savedInstance != null) {
                mDescriptionLayoutParams.bottomMargin = (int) (mDisplayMetrics.heightPixels /
                        savedInstance.getFloat(getString(R.string.saved_bottom_margin)));
                mDescriptionLayoutParams.leftMargin = (int) (mDisplayMetrics.widthPixels /
                        savedInstance.getFloat(getString(R.string.saved_left_margin)));
                mDescriptionEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, savedInstance.getFloat(getString(R.string.saved_text_size)));
            }
        }
        mDescriptionEditText.disableWriting();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat(getString(R.string.saved_bottom_margin), mDisplayMetrics.widthPixels /
                (float) (mDescriptionLayoutParams.bottomMargin));
        outState.putFloat(getString(R.string.saved_left_margin), mDisplayMetrics.heightPixels /
                (float) (mDescriptionLayoutParams.leftMargin));
        outState.putFloat(getString(R.string.saved_text_size), mDescriptionEditText.getTextSize());
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mGestureDetector = new GestureDetector(getContext(),this);

        final View rootView = inflater.inflate(R.layout.fragment_photo_preview, container, false);
        rootView.setOnTouchListener(this);
        final ImageView imagePreviewView = (ImageView) rootView.findViewById(R.id.mainImageView);
        if (imageBitmap != null) {
            imageBitmap.recycle();
            imageBitmap = null;
        }
        imageBitmap = getArguments().getParcelable(getString(R.string.taken_picture_bitmap));


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Matrix matrix = new Matrix();
        switch (display.getRotation()) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                break;
            case Surface.ROTATION_90:
                matrix.postRotate(-90);
                break;
            case Surface.ROTATION_270:
                matrix.postRotate(90);
                break;
        }
        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(),
                imageBitmap.getHeight(), matrix, true);
        imagePreviewView.setImageBitmap(imageBitmap);

        mDescriptionEditText = (DescriptionEditText) rootView.findViewById(R.id.descriptionEditText);
        mDescriptionLayoutParams = (RelativeLayout.LayoutParams) mDescriptionEditText.getLayoutParams();
        savedInstance = savedInstanceState;
        mDisplayMetrics = getResources().getDisplayMetrics();

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            Rect rect = new Rect();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mDescriptionEditText.getLayoutParams();
            @Override
            public void onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect);
                if(rootView.getHeight() - (rect.bottom - rect.top) > 200) {
                    isKeyBoardVisible = true;
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    //lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp.bottomMargin = rootView.getHeight() - (rect.bottom - rect.top);
                    mDescriptionEditText.setLayoutParams(lp);
                } else {
                    isKeyBoardVisible = false;
                    //lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                }
            }
        });



        return rootView;
    }

    private void toggleWriting() {
        if (!isKeyBoardVisible) {
            mDescriptionEditText.enableWriting();
        } else {
            mDescriptionEditText.disableWriting();
        }

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        toggleWriting();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }


}
