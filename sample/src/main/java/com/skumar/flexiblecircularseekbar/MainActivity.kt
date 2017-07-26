package com.skumar.flexiblecircularseekbar

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.skumar.flexibleciruclarseekbar.CircularSeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    val TAG = javaClass.simpleName ?: "TAG"
    private var mStartClicked: Boolean = false; private var mMoreClicked:Boolean = false

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.floatingButtonLeft -> {
                if (!mStartClicked || mMoreClicked) {
                    floatingButtonLeft.setImageResource(R.drawable.eco_pressed)
                    floatingButtonRight.setImageResource(R.drawable.eco_normal)
                    mStartClicked = true
                } else {
                    floatingButtonLeft.setImageResource(R.drawable.eco_normal)
                    mStartClicked = false
                }
                mCircularSeekBar.progress = mCircularSeekBar.min.toFloat()
            }
            R.id.floatingButtonRight -> if (!mMoreClicked || mStartClicked) {
                floatingButtonRight.setImageResource(R.drawable.comfort_pressed)
                floatingButtonLeft.setImageResource(R.drawable.eco_normal)
                mCircularSeekBar.progress = 23.toFloat()
                mMoreClicked = true
            } else {
                floatingButtonRight.setImageResource(R.drawable.eco_normal)
                mMoreClicked = false
                mCircularSeekBar.progress = 15.toFloat()
            }
            else -> Log.d(TAG, "ERROR WITH ID")
        }
    }

    private var progressValue = 10f

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            progressValue = savedInstanceState.getFloat("value")
            Log.d(TAG, "Progress value " + progressValue)
        }
        mMoreClicked = false
        mStartClicked = mMoreClicked

        // Setting the circular menu parameters
        mCircularSeekBar.setDrawMarkings(true)
        mCircularSeekBar.dotMarkers = true
        mCircularSeekBar.setRoundedEdges(true)
        mCircularSeekBar.setIsGradient(true)
        mCircularSeekBar.setPopup(true)
        mCircularSeekBar.sweepAngle = 270
        mCircularSeekBar.arcRotation = 225
        mCircularSeekBar.arcThickness = 30
        mCircularSeekBar.min = 10
        mCircularSeekBar.max = 30
        mCircularSeekBar.progress = progressValue
        mCircularSeekBar.setIncreaseCenterNeedle(20)
        mCircularSeekBar.valueStep = 2
        mCircularSeekBar.setNeedleFrequency(0.5f)
        mCircularSeekBar.needleDistanceFromCenter = 32
        mCircularSeekBar.setNeedleLengthInDP(12)
        mCircularSeekBar.setIncreaseCenterNeedle(24)
        mCircularSeekBar.needleThickness = 1.toFloat()
        mCircularSeekBar.setHeightForPopupFromThumb(10)

        // Setting textview with the seek bar value
        mSeekBarValue.text = progressValue.toString() + "\u00B0"

        fun setCirularSeekbarListener() {
            mCircularSeekBar.setOnCircularSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
                override fun onProgressChanged(CircularSeekBar: CircularSeekBar, progress: Float, fromUser: Boolean) {
                    mCircularSeekBar.setMinimumAndMaximumNeedleScale(progress - 2.5f, progress + 2.5f)
                    mSeekBarValue.text = progress.toString() + "\u00B0"
                    if (progress < mCircularSeekBar.realMax / 2) {
                        floatingButtonLeft.setImageResource(R.drawable.eco_pressed)
                        floatingButtonRight.setImageResource(R.drawable.eco_normal)
                    } else if (progress > mCircularSeekBar.realMax / 2 + 5) {
                        floatingButtonRight.setImageResource(R.drawable.comfort_pressed)
                        floatingButtonLeft.setImageResource(R.drawable.eco_normal)
                    } else {
                        floatingButtonLeft.setImageResource(R.drawable.eco_normal)
                        floatingButtonRight.setImageResource(R.drawable.eco_normal)
                    }
                    progressValue = progress
                }

                override fun onStartTrackingTouch(CircularSeekBar: CircularSeekBar) {

                }

                override fun onStopTrackingTouch(CircularSeekBar: CircularSeekBar) {

                }
            })

            floatingButtonRight.setOnClickListener(this)
            floatingButtonLeft.setOnClickListener(this)
        }

    }
}
