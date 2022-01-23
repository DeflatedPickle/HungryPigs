/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.hungrypigs.api

interface EatGrass {
    var eatFoodTimer: Int

    fun getNeckAngle(delta: Float): Float
    fun getHeadAngle(delta: Float): Float
}
