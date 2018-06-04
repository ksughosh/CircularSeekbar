package com.skumar.flexibleciruclarseekbar.ktx

/**
 * Created by s.kumar on 04.06.18.
 * Copyright © 2017 LOOP. All rights reserved.
 */
data class NeedleParams(var needleThickness: Int = 2.dp,
                        var isIsNeedleEnabled: Boolean = true,
                        var needleDistance: Int = 30,
                        var minimumNeedleScale: Int = 0,
                        var maximumNeedleScale: Int = 0,
                        var needleLength: Int = 10.dp,
                        var needleScaleUp: Boolean = true,
                        var drawMarkings: Boolean = false,
                        var fraction: Int = 1,
                        var isCenterIncrease: Boolean = true,
                        var increaseCenter: Int = 0) {

    data class Dots(var hasDots: Boolean = false,
                    var dotSize: Int = 2.dp)
}