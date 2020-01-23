package com.example.michael.strategygame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


// View that can be panned or zoomed

public class InteractiveView extends View {

    // Min and max hardcoded scaling size
    private float minScale = 0.5f;
    private float maxScale = 1.5f;
    // Width and height of the view's current display
    private int width, height;
    // Boundary of the view for panning or zooming
    private RectF boundsRect;
    private float translateX = 0;
    private float translateY = 0;
    private float scale = 1;
    private GestureDetectorCompat gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    // Used to set scaling to factor in width and height of view or device
    private float idealScale = scale, idealMinScale = minScale, idealMaxScale = maxScale;

    private boolean scrolling = false;

    public InteractiveView(Context context) {
        super(context);
        setupGestureDetector();
    }

    public InteractiveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupGestureDetector();
    }

    public void setBounds(RectF bounds) {
        this.boundsRect = bounds;
    }

    public float getTranslateX() {
        return translateX;
    }

    public float getTranslateY() {
        return translateY;
    }

    public float getScaleFactor() {
        return scale;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public int getViewWidth() {
        return width;
    }

    public int getViewHeight() {
        return height;
    }

    public void setScaling(float scale, float minScale, float maxScale) {
        this.idealScale = scale;
        this.idealMinScale = minScale;
        this.idealMaxScale = maxScale;

        adjustScaling();
    }

    private void setTranslateValues(float distanceX, float distanceY) {
        // Make sure it's not translated out of bounds
        float minX = boundsRect.left * scale;
        float minY = boundsRect.top * scale;
        float maxX = boundsRect.right * scale;
        float maxY = boundsRect.bottom * scale;

        // How much the canvas will be moved from the origin (positive = panning left)
        translateX = Math.max(Math.min(minX, translateX - distanceX), width - maxX);
        translateY = Math.max(Math.min(minY, translateY - distanceY), height - maxY);
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrolling = true;
                // Change the amount the canvas is translated by in onDraw
                setTranslateValues(distanceX, distanceY);
                // Redraw the view
                invalidate();
                return true;
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float newScale = scale * detector.getScaleFactor();
                // Find the minimum size, either the smallest size that fits or the hardcoded value
                float tempMinScale = Math.max(width / boundsRect.right, height / boundsRect.bottom);
                tempMinScale = Math.max(tempMinScale, minScale);
                // Make sure scale is between minimum and maximum
                scale = Math.min(maxScale, Math.max(tempMinScale, newScale));

                // Stay in bounds by altering how much the canvas will be translated
                setTranslateValues(0, 0);

                // Redraw the view
                invalidate();
                return true;
            }
        });
    }

    // Factors in width and height of view to make it scale like it's on a device with 720 pixels width
    private void adjustScaling() {
        float scaleCheck = Math.min(width, height) / 720.0f;

        scale = idealScale * scaleCheck;
        minScale = idealMinScale * scaleCheck;
        maxScale = idealMaxScale * scaleCheck;
    }

    // Get current view measurements
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = View.MeasureSpec.getSize(widthMeasureSpec);
        height = View.MeasureSpec.getSize(heightMeasureSpec);

        adjustScaling();

        // Have to call this or it crashes
        setMeasuredDimension(width, height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP)
            scrolling = false;
        boolean retVal = scaleGestureDetector.onTouchEvent(event);
        retVal = gestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(translateX, translateY);
        canvas.scale(scale, scale);
    }

}
