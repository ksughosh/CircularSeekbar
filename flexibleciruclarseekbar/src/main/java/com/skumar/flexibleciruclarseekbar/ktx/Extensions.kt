package com.skumar.flexibleciruclarseekbar.ktx

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.math.round

/**
 * Created by s.kumar on 04.06.18.
 * Copyright © 2017 LOOP. All rights reserved.
 */
val Int.dp : Int
    get() = (this.toFloat() / Resources.getSystem().displayMetrics.density).toInt()

val Int.px : Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun ViewGroup.inflate(@LayoutRes resource: Int, attachToRoot: Boolean) =
        context?.let { LayoutInflater.from(it).inflate(resource, this, attachToRoot) }

fun Context.getColorFromResource(@ColorRes resource: Int) =
        ContextCompat.getColor(this, resource)

val Double.f : Float
    get() = toFloat()

val Int.f : Float
    get() = toFloat()

val Float.i : Int
    get() = toInt()

val Int.d : Double
    get() = toDouble()

val Double.i: Int
    get() = toInt()

fun Int.squared() : Int = this * this
fun Float.squared() : Float = this * this

fun Double.roundToFraction(value: Double) : Double = round(this * value) / value