package com.sughoshkumar.circularseekbar;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sughoshkumar.sliderarc.CircularSeekBar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = "MAIN";
    private FloatingActionButton floatingButtonLeft;

    private FloatingActionButton floatingButtonRight;

    private TextView mSeekBarValue;


    private CircularSeekBar mCircularSeekBar;

    private float progressValue = 10;

    private boolean mStartClicked, mMoreClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            progressValue = savedInstanceState.getFloat("value");
            Log.d(TAG, "Progress value " + progressValue);
        }

        // initialize circular seek bar
        mCircularSeekBar = (CircularSeekBar) findViewById(R.id.circular_seek);

        floatingButtonLeft = (FloatingActionButton) findViewById(R.id.floating_button_left);

        floatingButtonRight = (FloatingActionButton) findViewById(R.id.floating_button_right);

        // initialize the button press switches
        mStartClicked = mMoreClicked = false;

        // Init textview for seek bar value
        mSeekBarValue = (TextView) findViewById(R.id.textView);

        // Setting the circular menu parameters
        mCircularSeekBar.setDrawMarkings(true);
        mCircularSeekBar.setDotMarkers(true);
        mCircularSeekBar.setRoundedEdges(true);
        mCircularSeekBar.setIsGradient(true);
        mCircularSeekBar.setPopup(true);
        mCircularSeekBar.setSweepAngle(270);
        mCircularSeekBar.setArcRotation(225);
        mCircularSeekBar.setArcThickness(30);
        mCircularSeekBar.setMin(10);
        mCircularSeekBar.setMax(30);
        mCircularSeekBar.setProgress(progressValue);
        mCircularSeekBar.setIncreaseCenterNeedle(20);
        mCircularSeekBar.setValueStep(2);
        mCircularSeekBar.setNeedleFrequency(0.5f);
        mCircularSeekBar.setNeedleDistanceFromCenter(32);
        mCircularSeekBar.setNeedleLengthInDP(12);
        mCircularSeekBar.setIncreaseCenterNeedle(24);
        mCircularSeekBar.setNeedleThickness(1);
        mCircularSeekBar.setHeightForPopupFromThumb(10);

        // Setting textview with the seek bar value
        mSeekBarValue.setText(String.valueOf(progressValue) + "\u00B0");


        // On progress changed listener
        // Obtain progress and manipulate accordingly
        mCircularSeekBar.setOnCircularSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar CircularSeekBar, float progress, boolean fromUser) {
                mCircularSeekBar.setMinimumAndMaximumNeedleScale(progress - 2.5f, progress + 2.5f);
                mSeekBarValue.setText(String.valueOf(progress) + "\u00B0");
                if (progress < (mCircularSeekBar.getRealMax() / 2)) {
                    floatingButtonLeft.setImageResource(R.drawable.eco_pressed);
                    floatingButtonRight.setImageResource(R.drawable.eco_normal);
                } else if (progress > (mCircularSeekBar.getRealMax() / 2) + 5) {
                    floatingButtonRight.setImageResource(R.drawable.comfort_pressed);
                    floatingButtonLeft.setImageResource(R.drawable.eco_normal);
                } else {
                    floatingButtonLeft.setImageResource(R.drawable.eco_normal);
                    floatingButtonRight.setImageResource(R.drawable.eco_normal);
                }
                progressValue = progress;
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar CircularSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar CircularSeekBar) {

            }
        });

        floatingButtonRight.setOnClickListener(this);
        floatingButtonLeft.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_button_left:
                if (!mStartClicked || mMoreClicked) {
                    floatingButtonLeft.setImageResource(R.drawable.eco_pressed);
                    floatingButtonRight.setImageResource(R.drawable.eco_normal);
                    mStartClicked = true;
                } else {
                    floatingButtonLeft.setImageResource(R.drawable.eco_normal);
                    mStartClicked = false;
                }
                mCircularSeekBar.setProgress(mCircularSeekBar.getMin());
                break;
            case R.id.floating_button_right:
                if (!mMoreClicked || mStartClicked) {
                    floatingButtonRight.setImageResource(R.drawable.comfort_pressed);
                    floatingButtonLeft.setImageResource(R.drawable.eco_normal);
                    mCircularSeekBar.setProgress(23);
                    mMoreClicked = true;
                } else {
                    floatingButtonRight.setImageResource(R.drawable.eco_normal);
                    mMoreClicked = false;
                    mCircularSeekBar.setProgress(15);
                }
                break;
            default:
                Log.d(TAG, "ERROR WITH ID");
        }
    }
}
