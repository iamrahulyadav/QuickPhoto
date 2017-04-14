package com.karakullukcu.huseyin.quickphoto;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

    public PhotoPreviewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mGestureDetector = new GestureDetector(getContext(),this);

        final View rootView = inflater.inflate(R.layout.fragment_photo_preview, container, false);
        rootView.setOnTouchListener(this);
        final ImageView imagePreviewView = (ImageView) rootView.findViewById(R.id.mainImageView);
        final Bitmap imageBitmap = getArguments().getParcelable(getString(R.string.taken_picture_bitmap));
        imagePreviewView.setImageBitmap(imageBitmap);


        mDescriptionEditText = (DescriptionEditText) rootView.findViewById(R.id.descriptionEditText);



        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            Rect rect = new Rect();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mDescriptionEditText.getLayoutParams();
            @Override
            public void onGlobalLayout() {
                rootView.getWindowVisibleDisplayFrame(rect);
                if(rootView.getHeight() - (rect.bottom - rect.top) > 200) {
                    isKeyBoardVisible = true;
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    lp.bottomMargin = rootView.getHeight() - (rect.bottom - rect.top);
                    mDescriptionEditText.setLayoutParams(lp);
                } else {
                    isKeyBoardVisible = false;
                    lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
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
