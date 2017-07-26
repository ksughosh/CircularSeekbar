package com.skumar.flexibleciruclarseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * The MIT License (MIT)
 * Copyright (c) <2016> <Sughosh Krishna Kumar >
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/**
 * Circular seek bar view that will draw an arc seekbar anywhere
 * between 90 degrees to 360 degrees.
 */

@SuppressWarnings("UnusedAssignment")
public class CircularSeekBar extends View {

    private static final String TAG = CircularSeekBar.class.getSimpleName();
    private static int INVALID_PROGRESS_VALUE = -1;
    // The initial rotational offset -90 means we start at 12 o'clock
    @SuppressWarnings("FieldCanBeLocal")
    private final int mAngleOffset = -90;

    /**
     * The Drawable for the seek arc thumbnail
     */
    private Drawable mThumb;

    /**
     * The Maximum value that this CircularSeekBar can be set to
     */
    private int mMax = 50;

    /**
     * The Current value that the CircularSeekBar is set to
     */
    private float mProgress = 0;

    /**
     * The width of the progress line for this CircularSeekBar
     */
    private int mProgressWidth = 4;

    /**
     * The Width of the background arc for the CircularSeekBar
     */
    private int mArcWidth = 2;

    /**
     * The Angle to start drawing this Arc from
     */
    private int mStartAngle = 0;

    /**
     * The Angle through which to draw the arc (Max is 360)
     */
    private int mSweepAngle = 360;

    /**
     * The rotation of the CircularSeekBar- 0 is twelve o'clock
     */
    private int mRotation = 0;

    /**
     * Give the CircularSeekBar rounded edges
     */
    private boolean mRoundedEdges = false;

    /**
     * Enable touch inside the CircularSeekBar
     */
    private boolean mTouchInside = true;

    /**
     * Will the progress increase clockwise or anti-clockwise
     */
    private boolean mClockwise = true;


    private int mArcDiameter;
    /**
     * is the control enabled/touchable
     */
    private boolean mEnabled = true;

    /**
     * Minimum value to be set for the slider arc
     */
    private int mMin;

    /**
     * Switch between single and gradient color
     */
    private boolean hasGradientColor;

    /**
     * Switches for raising the pop-up box
     */
    private boolean hasPopup, hasPopupIn;

    /**
     * Value for determining the scale for the progress
     * eg: 2 for 0.5 and 100 for 0.25 and 1 for 1 scale
     */
    private int mFraction;


    private float mProgressIncrement;

    /**
     * Switch to draw the needle scale markings
     */
    private boolean drawMarkings;

    /**
     * Popup box with the value
     */
    private PopupBox mPopup;

    // Internal variables
    private int mArcRadius = 0;
    private float mProgressSweep = 0;
    private RectF mArcRect = new RectF();
    private Paint mArcPaint;
    private Paint mProgressPaint;
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;
    private double mTouchAngle;
    private float mTouchIgnoreRadius;
    private OnCircularSeekBarChangeListener mOnCircularSeekBarChangeListener;

    // Variables concerning with drawing of the needle scale
    private Paint mNeedleScalePaint;
    private float mNeedleThickness;
    private int mNeedleDistance;
    private int mNeedleDP;
    private boolean isIncreaseCenter;
    private int mIncreaseCenterNeedle;
    private boolean hasDotMarkers;
    private int mDotSize;
    private float mMinimumNeedleScale;
    private float mMaximumNeedleScale;
    private int mHeightForPopup;
    private boolean mDrawNeedleScaleUp;

    public CircularSeekBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.circularSeekBarStyle);
    }

    public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    // Initialize all required attributes for the slider arc.
    private void init(Context context, AttributeSet attrs, int defStyle) {

        Log.d(TAG, "Initialising CircularSeekBar ...");
        float density = context.getResources().getDisplayMetrics().density;

        // Defaults, may need to link this into theme settings
        int arcColor = Color.GREEN;
        int progressColor = Color.BLUE;
        int needleColor = Color.BLACK;
        int thumbHalfHeight = 0;
        int thumbHalfWidth = 0;
        mThumb = ContextCompat.getDrawable(getContext(), R.drawable.circular_slider_drawable);
        // Convert progress width to pixels for current density
        mProgressWidth = (int) (mProgressWidth * density);


        if (attrs != null) {
            // Attribute initialization
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.CircularSeekBar, defStyle, 0);

            Drawable thumb = a.getDrawable(R.styleable.CircularSeekBar_thumb);
            if (thumb != null) {
                mThumb = thumb;
            }


            thumbHalfHeight = mThumb.getIntrinsicHeight() / 2;
            thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
            mThumb.setBounds(-thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth,
                    thumbHalfHeight);

            mMax = a.getInteger(R.styleable.CircularSeekBar_max, mMax);
            mProgress = a.getFloat(R.styleable.CircularSeekBar_progress, mProgress);
            mProgressWidth = (int) a.getDimension(
                    R.styleable.CircularSeekBar_progressWidth, mProgressWidth);
            mArcWidth = (int) a.getDimension(R.styleable.CircularSeekBar_arcWidth,
                    mArcWidth);
            mStartAngle = a.getInt(R.styleable.CircularSeekBar_startAngle, mStartAngle);
            mSweepAngle = a.getInt(R.styleable.CircularSeekBar_sweepAngle, mSweepAngle);
            mRotation = a.getInt(R.styleable.CircularSeekBar_rotation, mRotation);
            mRoundedEdges = a.getBoolean(R.styleable.CircularSeekBar_roundEdges,
                    mRoundedEdges);
            mTouchInside = a.getBoolean(R.styleable.CircularSeekBar_touchInside,
                    mTouchInside);
            mClockwise = a.getBoolean(R.styleable.CircularSeekBar_clockwise,
                    mClockwise);
            mEnabled = a.getBoolean(R.styleable.CircularSeekBar_enabled, mEnabled);

            arcColor = a.getColor(R.styleable.CircularSeekBar_arcColor, arcColor);
            progressColor = a.getColor(R.styleable.CircularSeekBar_progressColor,
                    progressColor);

            a.recycle();
        }

        mProgress = (mProgress > mMax) ? mMax : mProgress;
        mProgress = (mProgress < 0) ? 0 : mProgress;

        mSweepAngle = (mSweepAngle > 360) ? 360 : mSweepAngle;
        mSweepAngle = (mSweepAngle < 0) ? 0 : mSweepAngle;

        mProgressSweep = mProgress / mMax * mSweepAngle;

        mStartAngle = (mStartAngle > 360) ? 0 : mStartAngle;
        mStartAngle = (mStartAngle < 0) ? 0 : mStartAngle;

        mArcPaint = new Paint();
        mArcPaint.setColor(arcColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(mProgressWidth);

        mNeedleScalePaint = new Paint();
        mNeedleScalePaint.setColor(needleColor);
        mNeedleScalePaint.setAntiAlias(true);
        mNeedleScalePaint.setStrokeWidth(mNeedleThickness);
        mNeedleDistance = 30;
        mNeedleDP = 10;
        mMinimumNeedleScale = 0;
        isIncreaseCenter = false;
        mIncreaseCenterNeedle = 0;
        hasDotMarkers = false;
        mDotSize = 2;
        mHeightForPopup = 0;
        mDrawNeedleScaleUp = false;

        mProgressIncrement = 1;
        hasGradientColor = true;
        mMin = 0;
        mFraction = 1;
        drawMarkings = false;
        hasPopup = false;
        hasPopupIn = false;
        mPopup = new PopupBox(context);

        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mClockwise) {
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
        }

        // Set for the gradient color
        if (hasGradientColor)
            setShader();

        // Draw the arcs
        final int arcStart = mStartAngle + mAngleOffset + mRotation;
        final int arcSweep = mSweepAngle;
        canvas.drawArc(mArcRect, arcStart, arcSweep, false, mArcPaint);

        // Draw the needle scale
        if (drawMarkings) {
            drawNeedleMarkings(canvas);

            // Draw dot markers
            if (hasDotMarkers)
                drawDotMarker(canvas);
        }


        if (mEnabled) {
            // Draw the thumb nail
            canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
            mThumb.draw(canvas);
            if (hasPopupIn) {
                // Draw the popup
                canvas.translate(-mThumb.getIntrinsicWidth() / 2, -mThumb.getIntrinsicHeight() - mHeightForPopup);
                mPopup.draw(canvas);
            }
        }
    }

    /**
     * Method to drawing the gradient color for the arc
     */
    public void setShader() {
        SweepGradient sweepgradient = new SweepGradient(mArcRadius, mArcRadius,
                Color.parseColor("#2f8bca"), Color.parseColor("#c91200"));
        Matrix matrix = new Matrix();
        matrix.reset();
        sweepgradient.getLocalMatrix(matrix);
        matrix.postRotate(90, mArcRadius, mArcRadius);
        sweepgradient.setLocalMatrix(matrix);
        mArcPaint.setShader(sweepgradient);
        mNeedleScalePaint.setShader(sweepgradient);
    }

    /**
     * Method to draw the needle scale
     *
     * @param canvas drawable element
     */
    private void drawNeedleMarkings(Canvas canvas) {
        float cx = canvas.getWidth() / 2;
        float cy = canvas.getHeight() / 2;
        float scaleMarkSize = getResources().getDisplayMetrics().density * mNeedleDP;
        int radius = mArcRadius + mNeedleDistance;

        for (float progress = mProgressIncrement; progress < mMax; progress += mProgressIncrement) {
            float progressSweep = progress / mMax * mSweepAngle;
            int thumbAngle = (int) (mStartAngle + progressSweep + mRotation);
            float startX = (float) (cx + radius * Math.sin(Math.toRadians(thumbAngle)));
            float startY = (float) (cy - radius * Math.cos(Math.toRadians(thumbAngle)));

            float stopX = (float) (cx + (radius + scaleMarkSize) * Math.sin(Math.toRadians(thumbAngle)));
            float stopY = (float) (cy - (radius + scaleMarkSize) * Math.cos(Math.toRadians(thumbAngle)));

            if (progress == mMax / 2 && isIncreaseCenter) {
                stopX = (float) (cx + (radius + scaleMarkSize + mIncreaseCenterNeedle) * Math.sin(Math.toRadians(thumbAngle)));
                stopY = (float) (cy - (radius + scaleMarkSize + mIncreaseCenterNeedle) * Math.cos(Math.toRadians(thumbAngle)));
            }
            if (progress >= mMinimumNeedleScale && progress <= mMaximumNeedleScale && mDrawNeedleScaleUp) {
                stopX = (float) (cx + (radius + scaleMarkSize + mIncreaseCenterNeedle) * Math.sin(Math.toRadians(thumbAngle)));
                stopY = (float) (cy - (radius + scaleMarkSize + mIncreaseCenterNeedle) * Math.cos(Math.toRadians(thumbAngle)));
            }
            canvas.drawLine(startX, startY, stopX, stopY, mNeedleScalePaint);
        }
    }

    /**
     * Method to draw the dots over needle scale
     *
     * @param canvas drawable element
     */
    private void drawDotMarker(Canvas canvas) {
        float cx = canvas.getWidth() / 2;
        float cy = canvas.getHeight() / 2;
        float scaleMarkSize = getResources().getDisplayMetrics().density * 10;
        int radius = mArcRadius + mNeedleDistance + mIncreaseCenterNeedle + 15;

        for (float progress = 0; progress <= mMax; progress += 0.2f) {
            float progressSweep = progress / mMax * mSweepAngle;
            int thumbAngle = (int) (mStartAngle + progressSweep + mRotation);

            float stopX = (float) (cx + (radius + scaleMarkSize) * Math.sin(Math.toRadians(thumbAngle)));
            float stopY = (float) (cy - (radius + scaleMarkSize) * Math.cos(Math.toRadians(thumbAngle)));

            canvas.drawCircle(stopX, stopY, mDotSize, mNeedleScalePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int min = Math.min(width, height);
        float top = 0;
        float left = 0;

        mTranslateX = (int) (width * 0.5f);
        mTranslateY = (int) (height * 0.5f);

        mArcDiameter = min - getPaddingLeft() - mNeedleDistance - mIncreaseCenterNeedle - 5;
        mArcRadius = mArcDiameter / 2;
        top = height / 2 - (mArcDiameter / 2);
        left = width / 2 - (mArcDiameter / 2);
        mArcRect.set(left, top, left + mArcDiameter, top + mArcDiameter);

        int arcStart = (int) mProgressSweep + mStartAngle + mRotation + 90;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));

        setTouchInSide(mTouchInside);

        mPopup.measure(-2, -2);
        mPopup.layout(0, 0, mPopup.getMeasuredHeight(), mPopup.getMeasuredWidth());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    hasPopupIn = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    if (hasPopup)
                        hasPopupIn = true;
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful()) {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }

    private void onStartTrackingTouch() {
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }

    /**
     * Update progress value
     *
     * @param event generated from touch
     */
    private void updateOnTouch(MotionEvent event) {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        if (ignoreTouch) {
            return;
        }
        setPressed(true);
        mTouchAngle = getTouchDegrees(event.getX(), event.getY());
        float progress = getProgressForAngle(mTouchAngle);
        onProgressRefresh(progress, true);
    }

    /**
     * Get the point of contact
     *
     * @param xPos current x point
     * @param yPos current y point
     * @return if Thumb is touched or not
     */
    private boolean ignoreTouch(float xPos, float yPos) {
        boolean ignore = true;
        float x = xPos - (mTranslateX - mThumbXPos);
        float y = yPos - (mTranslateY - mThumbYPos);

        // Check if the touch event is on the Thumb
        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < mThumb.getIntrinsicHeight()) {
            ignore = false;
        }
        return ignore;
    }

    /**
     * Convert from cartesian to polar
     *
     * @param xPos current x point
     * @param yPos current y point
     * @return angle of the Thumb w.r.t the arc.
     */
    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        //invert the x-coord if we are rotating anti-clockwise
        x = (mClockwise) ? x : -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2)
                - Math.toRadians(mRotation));
        if (angle < 0) {
            angle = 360 + angle;
        }
        angle -= mStartAngle;
        return angle;
    }

    /**
     * Get value for current angle
     *
     * @param angle current angle
     * @return progress for angle
     */
    private float getProgressForAngle(double angle) {
        double touchProgress = roundToFraction(valuePerDegree() * angle);
        touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        return (float) touchProgress;
    }

    /**
     * Round the values to set fraction
     *
     * @param x value of progress
     * @return rounded to the nearest precision
     */
    private double roundToFraction(double x) {
        return (double) Math.round(x * mFraction) / mFraction;
    }

    /**
     * Getter
     *
     * @return value per degree
     */
    private float valuePerDegree() {
        return (float) mMax / mSweepAngle;
    }

    /**
     * Upon progress refresh from listener
     *
     * @param progress progress value
     * @param fromUser is from user
     */
    private void onProgressRefresh(float progress, boolean fromUser) {
        updateProgress(progress, fromUser);
    }

    /**
     * Update thumb positon
     */
    private void updateThumbPosition() {
        int thumbAngle = (int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    /**
     * Progress value updater
     *
     * @param progress progress value
     * @param fromUser is from user
     */
    private void updateProgress(float progress, boolean fromUser) {

        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }

        progress = (progress > mMax) ? mMax : progress;
        progress = (progress < 0) ? 0 : progress;
        mProgress = progress;

        // Set the popup value
        mPopup.setText(String.valueOf(progress + mMin) + "°");

        if (mOnCircularSeekBarChangeListener != null) {
            mOnCircularSeekBarChangeListener
                    .onProgressChanged(this, progress + mMin, fromUser);
        }

        mProgressSweep = progress / mMax * mSweepAngle;

        updateThumbPosition();

        invalidate();
    }

    /**
     * Sets a listener to receive notifications of changes to the CircularSeekBar's
     * progress level. Also provides notifications of when the user starts and
     * stops a touch gesture within the CircularSeekBar.
     *
     * @param l The seek bar notification listener
     * @see OnCircularSeekBarChangeListener
     */
    public void setOnCircularSeekBarChangeListener(OnCircularSeekBarChangeListener l) {
        mOnCircularSeekBarChangeListener = l;
    }

    /**
     * Get progress
     *
     * @return progress
     */
    public float getProgress() {
        return mProgress + mMin;
    }

    /**
     * Set progress value
     *
     * @param progress progress value
     */
    public void setProgress(float progress) {
        float progressMinusMin = progress - mMin;
        updateProgress(getProgressForAngle(progressMinusMin / valuePerDegree()), false);
    }

    /**
     * Getter
     *
     * @return Progress bar thickness (width)
     */
    public int getProgressBarThickness() {
        return mProgressWidth;
    }

    /**
     * Set progress bar drawable width
     *
     * @param progressBarThickness thickness in dp
     */
    public void setProgressBarThickness(int progressBarThickness) {
        this.mProgressWidth = progressBarThickness;
        mProgressPaint.setStrokeWidth(mProgressWidth);
    }

    /**
     * Getter
     *
     * @return Arc thickness (width)
     */
    public int getArcThickness() {
        return mArcWidth;
    }

    /**
     * Set arc drawable width
     *
     * @param mArcThickness thickness in dp
     */
    public void setArcThickness(int mArcThickness) {
        this.mArcWidth = mArcThickness;
        mArcPaint.setStrokeWidth(mArcWidth);
    }

    /**
     * Get rotation
     *
     * @return rotation value
     */
    public int getArcRotation() {
        return mRotation;
    }

    /**
     * Set rotation value
     *
     * @param mRotation rotation value
     */
    public void setArcRotation(int mRotation) {
        this.mRotation = mRotation;
        updateThumbPosition();
    }

    /**
     * Getter
     *
     * @return start angle of the arc
     */
    public int getStartAngle() {
        return mStartAngle;
    }

    /**
     * Setting the starting angle
     *
     * @param mStartAngle initial angle value
     */
    public void setStartAngle(int mStartAngle) {
        this.mStartAngle = mStartAngle;
        updateThumbPosition();
    }

    /**
     * Getter
     *
     * @return sweep angle of the arc
     */
    public int getSweepAngle() {
        return mSweepAngle;
    }

    /**
     * Setter
     *
     * @param mSweepAngle sweep angle of the arc
     */
    public void setSweepAngle(int mSweepAngle) {
        this.mSweepAngle = mSweepAngle;
        updateThumbPosition();
    }

    /**
     * Set rounded edges
     *
     * @param isEnabled if true then have stroke with cap else not
     */
    public void setRoundedEdges(boolean isEnabled) {
        mRoundedEdges = isEnabled;
        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            mProgressPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
    }

    /**
     * @param isEnabled touch anywhere for progress
     * @deprecated This has been changed to reflect only on Thumb click
     * Set the touch inside value
     */
    public void setTouchInSide(boolean isEnabled) {
        int thumbHalfHeight = mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
        mTouchInside = isEnabled;
        if (mTouchInside) {
            mTouchIgnoreRadius = mArcRadius / 4;
        } else {
            // Don't use the exact radius makes interaction too tricky
            mTouchIgnoreRadius = mArcRadius - Math.min(thumbHalfHeight, thumbHalfWidth);
        }
    }

    /**
     * Getter
     *
     * @return is slider is clockwise
     */
    public boolean isClockwise() {
        return mClockwise;
    }

    /**
     * Set clockwise movement of the slider
     *
     * @param isClockwise if true clockwise movement
     */
    public void setClockwise(boolean isClockwise) {
        mClockwise = isClockwise;
    }

    /**
     * Check Thumb drawable
     *
     * @return Is Thumb enabled
     */
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * Setter
     *
     * @param enabled draw Thumb
     */
    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    /**
     * Get the progress value color
     *
     * @return color of progress
     */
    public int getProgressColor() {
        return mProgressPaint.getColor();
    }

    /**
     * Set progress value color
     *
     * @param color color for progess
     */
    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        invalidate();
    }

    /**
     * Getter
     *
     * @return Slider arc color
     */
    public int getArcColor() {
        return mArcPaint.getColor();
    }

    /**
     * Setter
     *
     * @param color set slider arc color
     */
    public void setArcColor(int color) {
        mArcPaint.setColor(color);
        invalidate();
    }

    /**
     * Getter
     *
     * @return get maximum slider value
     */
    public int getMax() {
        return mMax;
    }

    /**
     * Setter
     *
     * @param mMax set maximum slider value
     */
    public void setMax(int mMax) {
        this.mMax = mMax - mMin;
    }

    /**
     * Setter
     *
     * @param hasGradient true for gradient paint
     */
    public void setIsGradient(boolean hasGradient) {
        hasGradientColor = hasGradient;
    }

    /**
     * Getter
     *
     * @return get minimum value
     */
    public int getMin() {
        return mMin;
    }

    /**
     * Setter
     *
     * @param min set minimum slider value
     */
    public void setMin(int min) {
        mMin = min;

    }

    /**
     * Check has is gradient arc is selected
     *
     * @return is arc gradient
     */
    public boolean hasGradientColor() {
        return hasGradientColor;
    }

    /**
     * Getter
     *
     * @return scale fraction value
     */
    public int getValueStep() {
        return mFraction;
    }

    /**
     * Setter
     *
     * @param value scale fraction value
     *              eg: 2 for 0.5 scale or 100 for 0.25 scale.
     */
    public void setValueStep(int value) {
        mFraction = value;
    }

    /**
     * Number of needles in one scale
     *
     * @param distanceBetweenNeedles number of needles per fraction scale
     */
    public void setNeedleFrequency(float distanceBetweenNeedles) {
        mProgressIncrement = distanceBetweenNeedles;
    }

    /**
     * Getter
     *
     * @return touch angle
     */
    public double getTouchAngle() {
        return mTouchAngle;
    }

    /**
     * Setter
     *
     * @param mDrawMarkings is true draws the needle scale
     */
    public void setDrawMarkings(boolean mDrawMarkings) {
        drawMarkings = mDrawMarkings;
    }

    /**
     * Getter
     *
     * @return needle scale thickness
     */
    public float getNeedleThickness() {
        return mNeedleThickness;
    }

    /**
     * Setter
     *
     * @param thickness set needle scale thickness
     */
    public void setNeedleThickness(float thickness) {
        mNeedleThickness = thickness;
        mNeedleScalePaint.setStrokeWidth(thickness);
    }

    /**
     * Getter
     *
     * @return needle distance from center
     */
    public int getNeedleDistanceFromCenter() {
        return mNeedleDistance;
    }

    /**
     * Setter
     *
     * @param radius scale's distance from center of the arc
     */
    public void setNeedleDistanceFromCenter(int radius) {
        if (radius < 30)
            mNeedleDistance = 30;
        else
            mNeedleDistance = radius;
    }

    /**
     * Setter
     *
     * @param dp length of each needle
     */
    public void setNeedleLengthInDP(int dp) {
        mNeedleDP = dp;
    }

    /**
     * Getter
     *
     * @return length of each needle
     */
    public int getNeedleDP() {
        return mNeedleDP;
    }

    /**
     * Setter
     *
     * @param hasPopup true to draw popup
     */
    public void setPopup(boolean hasPopup) {
        this.hasPopup = hasPopup;
    }

    /**
     * Getter
     *
     * @return increase in needle length for half-way
     */
    public boolean isIncreaseCenterNeedle() {
        return isIncreaseCenter;
    }

    /**
     * Setter
     *
     * @param increaseCenterNeedle half-way point; taller needle.
     */
    public void setIncreaseCenterNeedle(int increaseCenterNeedle) {
        isIncreaseCenter = true;
        mIncreaseCenterNeedle = increaseCenterNeedle;
    }

    /**
     * Getter
     *
     * @return if dots to be drawn
     */
    public boolean getDotMarkers() {
        return hasDotMarkers;
    }

    /**
     * Setter
     *
     * @param isDots to draw dots after needle scale
     */
    public void setDotMarkers(boolean isDots) {
        hasDotMarkers = isDots;
    }

    /**
     * Setter
     *
     * @param dotSize size of each dots
     */
    public void setDotSize(int dotSize) {
        mDotSize = dotSize;
    }

    /**
     * Setter for increasing needle length between certain values
     *
     * @param minimumNeedleScale minimum progress value
     * @param maximumNeedleScale maximum progress value
     */
    public void setMinimumAndMaximumNeedleScale(float minimumNeedleScale, float maximumNeedleScale) {
        mMinimumNeedleScale = minimumNeedleScale - mMin;
        mMaximumNeedleScale = maximumNeedleScale - mMin;
        mDrawNeedleScaleUp = true;
    }

    /**
     * Setter for height of the popup
     *
     * @param height height above the Thumb
     */
    public void setHeightForPopupFromThumb(int height) {
        mHeightForPopup = height;
    }

    /**
     * Getter
     *
     * @return real maximum value
     */
    public int getRealMax() {
        return (mMax + mMin);
    }

    public interface OnCircularSeekBarChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param CircularSeekBar The CircularSeekBar whose progress has changed
         * @param progress        The current progress level. This will be in the range
         *                        0..max where max was set by
         *                        max is 100.)
         * @param fromUser        True if the progress change was initiated by the user.
         */
        void onProgressChanged(CircularSeekBar CircularSeekBar, float progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         *
         * @param CircularSeekBar The CircularSeekBar in which the touch gesture began
         */
        void onStartTrackingTouch(CircularSeekBar CircularSeekBar);

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the CircularSeekBar.
         *
         * @param CircularSeekBar The CircularSeekBar in which the touch gesture began
         */
        void onStopTrackingTouch(CircularSeekBar CircularSeekBar);
    }
}