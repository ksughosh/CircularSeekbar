package com.skumar.flexibleciruclarseekbar.ktx

/**
 * Created by s.kumar on 04.06.18.
 * Copyright © 2017 LOOP. All rights reserved.
 */
data class ViewParams (var progressWidth: Int = 4,
                       var arcWidth : Int = 2,
                       var startAngle: Int = 0,
                       var sweepAngle: Int = 360,
                       var rotation: Int = 0,
                       var roundedEdges: Boolean = false,
                       var touchInside: Boolean = true,
                       var clockWise: Boolean = true,
                       var enabled: Boolean = true,
                       var hasGradientColor: Boolean = true,
                       var arcRadius: Int = 0,
                       var progressSweep: Int = 0) {
    private val angleOffset = -90
    fun getAngularStart() = startAngle + angleOffset + rotation
}