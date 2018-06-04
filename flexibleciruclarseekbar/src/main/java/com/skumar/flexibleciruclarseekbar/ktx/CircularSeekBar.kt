@file:Suppress("MemberVisibilityCanBePrivate")

package com.skumar.flexibleciruclarseekbar.ktx

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.skumar.flexibleciruclarseekbar.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Created by s.kumar on 04.06.18.
 * Copyright © 2017 LOOP. All rights reserved.
 */
open class CircularSeekBar : View {
    val viewParams = ViewParams()
    val needleParams = NeedleParams()
    val dotParams = NeedleParams.Dots()
    val popupParams = PopupParams()
    val gradientParams = GradientParams()

    var max: Int = 50
    var min: Int = 0
    var progress: Float = 0f
    var progressIncrement = 1

    var popUp : View? = null

    protected var onSeekbarChange : ((Float) -> Unit)? = null
    protected var onSeekbarStopped : ((Float) -> Unit)? = null

    protected open val arcPaint = Paint()
    protected open val progressPaint = Paint()
    protected open val needlePaint = Paint()
    protected open val arcRect = RectF()

    protected open var thumb: Drawable? = null

    protected var translateX = 0f
    protected var translateY = 0f
    protected var thumbXPos = 0f
    protected var thumbYPos = 0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) {
        val density = context?.resources?.displayMetrics?.density ?: 0.0f

        var arcColor = Color.GREEN
        var progressColor = Color.BLUE
        var needleColor = Color.BLACK

        if (context == null) return

        thumb = context.getDrawableCompat(R.drawable.circular_slider_drawable)

        viewParams.progressWidth *= density.toInt()

        attrs?.apply {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyleAttr, 0)
            typedArray?.apply {
                thumb = getDrawable(R.styleable.CircularSeekBar_thumb) ?: thumb
                val halfThumbDimen = thumb?.intrinsicHeight ?: 0 / 2
                thumb?.setBounds(-halfThumbDimen, -halfThumbDimen, halfThumbDimen, halfThumbDimen)

                max = getInt(R.styleable.CircularSeekBar_thumb, max)
                progress = getFloat(R.styleable.CircularSeekBar_progress, progress)

                viewParams.apply {
                    progressWidth = getDimension(R.styleable.CircularSeekBar_progressWidth, progressWidth.toFloat()).toInt()
                    arcWidth = getDimension(R.styleable.CircularSeekBar_arcWidth, arcWidth.toFloat()).toInt()
                    startAngle = getInt(R.styleable.CircularSeekBar_startAngle, startAngle)
                    sweepAngle = getInt(R.styleable.CircularSeekBar_sweepAngle, sweepAngle)
                    rotation = getInt(R.styleable.CircularSeekBar_rotation, rotation)
                    roundedEdges = getBoolean(R.styleable.CircularSeekBar_roundEdges, roundedEdges)
                    touchInside = getBoolean(R.styleable.CircularSeekBar_touchInside, touchInside)
                    clockWise = getBoolean(R.styleable.CircularSeekBar_clockwise, clockWise)
                    enabled = getBoolean(R.styleable.CircularSeekBar_enabled, enabled)
                    hasGradientColor = getBoolean(R.styleable.CircularSeekBar_gradientEnabled, hasGradientColor)
                }
                popupParams.hasPopup = getBoolean(R.styleable.CircularSeekBar_hasPopup, popupParams.hasPopup)

                needleParams.apply {
                    isIsNeedleEnabled = getBoolean(R.styleable.CircularSeekBar_needleEnabled, isIsNeedleEnabled)
                    needleThickness = getDimensionPixelOffset(R.styleable.CircularSeekBar_needleThickness, needleThickness)
                    needleDistance = getInt(R.styleable.CircularSeekBar_needleDistance, needleDistance)
                    needleScaleUp = getBoolean(R.styleable.CircularSeekBar_needleScaleEnabled, needleScaleUp)
                }
                dotParams.apply {
                    hasDots = getBoolean(R.styleable.CircularSeekBar_hasDots, hasDots)
                    dotSize = getDimensionPixelSize(R.styleable.CircularSeekBar_dotSize, dotSize)
                }
                gradientParams.apply {
                    startColor = getColor(R.styleable.CircularSeekBar_startGradientColor, startColor)
                    endColor = getColor(R.styleable.CircularSeekBar_endGradientColor, endColor)
                }

                arcColor = getColor(R.styleable.CircularSeekBar_arcColor, arcColor)
                progressColor = getColor(R.styleable.CircularSeekBar_progressColor, progressColor)
                needleColor = getColor(R.styleable.CircularSeekBar_needleColor, needleColor)
                recycle()
            }
        }
        assignDefaults()
        assignColorTheme(arcColor)
        assignProgressTheme(progressColor)
        if (needleParams.isIsNeedleEnabled) {
            assignNeedleParams(needleColor)
        }

    }

    private fun assignProgressTheme(progressColor: Int) {
        progressPaint.color = progressColor
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = viewParams.progressWidth.toFloat()
        if (viewParams.roundedEdges) {
            progressPaint.strokeCap = Paint.Cap.ROUND
        }
    }

    private fun assignColorTheme(arcColor: Int) {
        arcPaint.color = arcColor
        arcPaint.isAntiAlias = true
        arcPaint.style = Paint.Style.STROKE
        arcPaint.strokeWidth = viewParams.arcWidth.toFloat()
        if (viewParams.roundedEdges) {
            arcPaint.strokeCap = Paint.Cap.ROUND
        }
    }

    private fun assignDefaults() {
        if (popupParams.hasPopup) {
            popUp = PopupBox(context)
        }

        progress = progress defaultDownTo 0
        progress = progress defaultUpTo max

        viewParams.sweepAngle = viewParams.sweepAngle defaultDownTo 0
        viewParams.sweepAngle = viewParams.sweepAngle defaultUpTo 360

        viewParams.startAngle defaultDownTo 0
        viewParams.startAngle = if (viewParams.startAngle > 360) 0 else viewParams.startAngle

        viewParams.progressSweep = (progress / max * viewParams.sweepAngle).i
    }

    private fun assignNeedleParams(needleColor: Int) {
        needlePaint.color = needleColor
        needlePaint.isAntiAlias = true
        needlePaint.strokeWidth = needleParams.needleThickness.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            if (!viewParams.clockWise) {
                scale(-1f, -1f, arcRect.centerX(), arcRect.centerY())
            }

            if (viewParams.hasGradientColor)
                setShader()

            val arcStart = viewParams.getAngularStart().toFloat()
            val arcSweep = viewParams.sweepAngle.toFloat()

            drawArc(arcRect, arcStart, arcSweep, false, arcPaint)

            if (needleParams.drawMarkings) {
                drawNeedleMarkings(this)

                if (dotParams.hasDots) {
                    drawDotsMarker(this)
                }
            }

            if (viewParams.enabled) {
                translate(translateX - thumbXPos, translateY - thumbYPos)
                thumb?.apply {
                    draw(this@run)
                    if (popupParams.hasPopupIn) {
                        translate((-intrinsicWidth/2).toFloat(), (intrinsicHeight - popupParams.height).toFloat())
                        popUp?.draw(this@run)
                    }
                }
            }
        }
    }

    private fun drawDotsMarker(canvas: Canvas) {
        val cx = canvas.width/2
        val cy = canvas.height/2

        val scaleMarkSize = needleParams.needleLength
        val radius = viewParams.arcRadius + needleParams.needleDistance + needleParams.increaseCenter + 15

        var progress = 0f
        while (progress < max) {
            val sweep = progress/max * viewParams.sweepAngle
            val thumbAngle = (viewParams.startAngle + sweep + viewParams.rotation).toDouble()

            val stopX = (cx + (radius + scaleMarkSize) * sin(Math.toRadians(thumbAngle))).f
            val stopY = (cy - (radius + scaleMarkSize) * cos(Math.toRadians(thumbAngle))).f

            canvas.drawCircle(stopX, stopY, dotParams.dotSize.toFloat(), needlePaint)
            progress+=0.2f
        }

    }

    private fun drawNeedleMarkings(canvas: Canvas) {
        val cx = canvas.width/2
        val cy = canvas.height/2
        val scaleMarkSize = needleParams.needleLength
        val radius = viewParams.arcRadius + needleParams.needleDistance
        var progress = progressIncrement
        while(progress < max) {
            val progressSweep = progress/ max * viewParams.sweepAngle
            val thumbAngle = (viewParams.startAngle + progressSweep + viewParams.rotation).toDouble()
            val startX = (cx + radius * sin(Math.toRadians(thumbAngle)))
            val startY = (cy - radius * cos(Math.toRadians(thumbAngle)))

            var stopX = (cx + (radius + scaleMarkSize) * sin(Math.toRadians(thumbAngle)))
            var stopY = (cy - (radius + scaleMarkSize) * cos(Math.toRadians(thumbAngle)))

            val condition1 = progress == max / 2 && needleParams.isCenterIncrease
            val condition2 = progress >= needleParams.minimumNeedleScale && progress <= needleParams.maximumNeedleScale

            if (condition1 || condition2) {
                stopX = cx + (radius + scaleMarkSize + needleParams.increaseCenter) * sin(Math.toRadians(thumbAngle))
                stopY = cy - (radius + scaleMarkSize + needleParams.increaseCenter) * cos(Math.toRadians(thumbAngle))
            }
            canvas.drawLine(startX.f, startY.f, stopX.f, stopY.f, needlePaint)
            progress += progressIncrement
        }
    }

    private fun setShader() {
        val arcRadius = viewParams.arcRadius.toFloat()
        val sweepGradient = SweepGradient(arcRadius, arcRadius,
                gradientParams.startColor, gradientParams.endColor)

        val matrix = Matrix()
        matrix.postRotate(90f, arcRadius, arcRadius)
        sweepGradient.setLocalMatrix(matrix)
        arcPaint.shader = sweepGradient
        if (needleParams.drawMarkings) {
            needlePaint.shader = sweepGradient
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        val width = getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        val min = min(width, height)
        translateX = width.f * 0.5f
        translateY = height.f * 0.5f

        val arcDiameter = min - paddingLeft - needleParams.needleDistance - needleParams.increaseCenter - 5
        val arcRadius = arcDiameter/2
        viewParams.arcRadius = arcRadius
        val top = height/2 - arcRadius
        val left = width/2 - arcRadius

        arcRect.set(left.f, top.f, left+arcDiameter.f, top+arcDiameter.f)

        val arcStart = viewParams.let { it.progressSweep + it.startAngle + it.rotation + 90 }.d
        thumbXPos = (arcRadius * cos(Math.toRadians(arcStart))).f
        thumbYPos = (arcRadius * sin(Math.toRadians(arcStart))).f

        popUp?.measure(-2, -2)
        popUp?.apply { layout(0, 0, measuredHeight, measuredWidth) }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        return super.onTouchEvent(event)
        if (!viewParams.enabled || event == null) return false
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                onStartTrackingTouch()
                updateTouch(event)
                popupParams.hasPopupIn = false
            }

            MotionEvent.ACTION_MOVE -> updateTouch(event)

            MotionEvent.ACTION_UP -> {
                onStopTrackingTouch()
                if (popupParams.hasPopup) {
                    popupParams.hasPopupIn = true
                }
                isPressed = true
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
        return true
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (thumb != null && thumb?.isStateful == true) {
            thumb?.state = drawableState
        }
        invalidate()
    }

    protected open fun onStartTrackingTouch() {
        if (onSeekbarChange != null) {
            onSeekbarChange?.invoke(progress)
        }
    }

    protected open fun onStopTrackingTouch() {
        if (onSeekbarStopped != null) {
            onSeekbarStopped?.invoke(progress)
        }
    }

    open fun setOnProgressChange(func: (Float) -> Unit) {
        this.onSeekbarChange = func
    }

    open fun onStoppedProgress(func: (Float) -> Unit) {
        this.onSeekbarStopped = func
    }

    protected open fun updateTouch(event: MotionEvent) {
        if (thumb?.ignoreTouch(event.x, event.y) == true) return
        isPressed = true
        val touchAngle = getTouchDegrees(event.x, event.y)
        val progress = getProgressForAngle(touchAngle)
        onProgressRefresh(progress, false)
    }

    private fun onProgressRefresh(progress: Float, fromUser: Boolean) {
        updateProgress(progress, fromUser)
    }

    open fun updateProgress(progress: Float, fromUser: Boolean = false) {
        if (progress.isInvalid()) return

        var progressee = progress defaultDownTo 0
        progressee = progress defaultUpTo max

        this.progress = progressee
        val popUp = this.popUp
        if (popUp is PopupBox) {
            popUp.text = (progress + min).toString()
        }
        onSeekbarChange?.invoke(progress + min)

        viewParams.progressSweep = (progress / max * viewParams.sweepAngle).i

        updateThumbPosition()

        invalidate()
    }

    private fun updateThumbPosition() {
        val angle = viewParams.run { startAngle + progressSweep + rotation + 90 }.d
        thumbXPos = (viewParams.arcRadius * cos(Math.toRadians(angle))).f
        thumbYPos = (viewParams.arcRadius * sin(Math.toRadians(angle))).f
    }

    protected open fun getProgressForAngle(angle: Double) : Float =
            angle.roundToFraction(needleParams.fraction.d).f

    private fun Float.isInvalid() : Boolean = (this < 0 || this > max)

    private val valuePerDegree : Float
        get() = (max/viewParams.sweepAngle).f

    private fun Drawable.ignoreTouch(xPos: Float, yPos: Float): Boolean {
        val x = xPos - (translateX - thumbXPos)
        val y = yPos - (translateY - thumbYPos)

        val touchRadius = sqrt((x.squared() + y.squared()))
        if (touchRadius < thumb?.intrinsicHeight?.f ?: touchRadius) {
            return false
        }
        return true
    }

    private fun getTouchDegrees(xPos: Float, yPos: Float): Double {
        var x = xPos - translateX
        val y = yPos - translateY
        //invert the x-coord if we are rotating anti-clockwise
        x = if (viewParams.clockWise) x else -x
        // convert to arc Angle
        var angle = Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble()) + Math.PI / 2 - Math.toRadians(viewParams.rotation.toDouble()))
        if (angle < 0) angle += 360
        angle -= viewParams.startAngle.toDouble()
        return angle
    }

    private infix fun Int.defaultUpTo(value: Int) : Int = if (this > value) value else this
    private infix fun Float.defaultUpTo(value: Int) : Float = if (this > value) value.toFloat() else this

    private infix fun Float.defaultDownTo(value: Int) : Float = if (this < value) value.toFloat() else this
    private infix fun Int.defaultDownTo(value: Int) : Int = if (this < value) value else this

    private fun Context.getDrawableCompat(@DrawableRes resource: Int) =
            ContextCompat.getDrawable(this, resource)

    companion object {
        const val INVALID_PROGRESS_VALUE = -1f
    }

}