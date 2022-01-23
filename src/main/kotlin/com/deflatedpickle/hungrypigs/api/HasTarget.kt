/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.hungrypigs.api

import net.minecraft.item.ItemStack
import net.minecraft.util.math.Vec3d

interface HasTarget {
    var targetPos: Vec3d?
    var targetStack: ItemStack?
}
