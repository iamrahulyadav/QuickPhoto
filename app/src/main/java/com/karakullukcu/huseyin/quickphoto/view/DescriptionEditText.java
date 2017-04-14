package com.karakullukcu.huseyin.quickphoto.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;


/**
 * Created by pc on 14.04.2017.
 */

public class DescriptionEditText extends AppCompatEditText implements GestureDetector.OnGestureListener,
        ScaleGestureDetector.OnScaleGestureListener {
    private GestureDetector mGestureDetectorCompat;
    private ScaleGestureDetector mScaleGestureDetector;
    private DisplayMetrics mDisplayMetrics = getResources().getDisplayMetrics();
    private InputMethodManager imm;
    private int xDelta;
    private int yDelta;
    private boolean isMoving = false;
    private boolean isScaling = false;

    public int borderDistanceFromScreenSidesAsDp = 8;


    public DescriptionEditText(Context context) {
        super(context);
        init(context);
    }

    public DescriptionEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DescriptionEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mGestureDetectorCompat = new GestureDetector(context,this);
        mScaleGestureDetector = new ScaleGestureDetector(context,this);
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetectorCompat.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);

        if (!isScaling) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) this.getLayoutParams();
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:

                    xDelta = X - lp.leftMargin;
                    yDelta = Y + lp.bottomMargin;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    isMoving = false;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:

                    int newLeftMargin = X - xDelta;
                    int newBottomMargin = yDelta - Y;

                    if (newLeftMargin < dpToPixels(borderDistanceFromScreenSidesAsDp)) {
                        newLeftMargin = dpToPixels(borderDistanceFromScreenSidesAsDp);
                    }
                    if (Math.abs(lp.leftMargin - newLeftMargin) > dpToPixels(4)) {
                        disableWriting();
                        isMoving = true;
                    }
                    lp.leftMargin = newLeftMargin;


                    if (newBottomMargin < dpToPixels(borderDistanceFromScreenSidesAsDp)) {
                        newBottomMargin = dpToPixels(borderDistanceFromScreenSidesAsDp);
                    } else if (newBottomMargin > mDisplayMetrics.heightPixels - this.getHeight() - dpToPixels(borderDistanceFromScreenSidesAsDp)) {
                        newBottomMargin = mDisplayMetrics.heightPixels - this.getHeight() - dpToPixels(borderDistanceFromScreenSidesAsDp);
                    }
                    if (Math.abs(lp.bottomMargin - newBottomMargin) > dpToPixels(4)) {
                        disableWriting();
                        isMoving = true;
                    }
                    lp.bottomMargin = newBottomMargin;


                    this.setLayoutParams(lp);

                    break;
            }
            this.invalidate();
        }
        return true;
    }



    /*
     * Almost same method as disable writing but don't close keyboard.
     * Because its default behavior of back button.
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.clearFocus();
            this.setCursorVisible(false);
            if(this.getText().toString().trim().isEmpty()) {
                this.setVisibility(View.GONE);
            }
            return false;
        }
        return super.onKeyPreIme(keyCode,event);
    }

    public void enableWriting() {
        this.setVisibility(View.VISIBLE);
        if (!this.isFocused()) {
            this.requestFocus();
        }
        this.setCursorVisible(true);
        imm.showSoftInput(this, 0);

    }

    public void disableWriting() {
        this.clearFocus();
        this.setCursorVisible(false);
        imm.hideSoftInputFromWindow(this.getWindowToken(),0);
        if(this.getText().toString().trim().isEmpty()) {
            this.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float size = this.getTextSize();
        float factor = detector.getScaleFactor();
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, size*factor);
        return true;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        isScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        isScaling = false;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;

    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        if (!isMoving) {
            this.setCursorVisible(true);
            super.onTouchEvent(motionEvent);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    private int dpToPixels(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mDisplayMetrics);
    }


}
