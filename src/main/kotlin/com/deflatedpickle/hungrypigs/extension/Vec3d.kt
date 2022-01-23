/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.hungrypigs.extension

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

fun unpackLongX(packedPos: Long) = (packedPos shl 64 - BlockPos.BIT_SHIFT_X - BlockPos.SIZE_BITS_X shr 64 - BlockPos.SIZE_BITS_X)
fun unpackLongY(packedPos: Long) = (packedPos shl 64 - BlockPos.SIZE_BITS_Y shr 64 - BlockPos.SIZE_BITS_Y)
fun unpackLongZ(packedPos: Long) = (packedPos shl 64 - BlockPos.BIT_SHIFT_Z - BlockPos.SIZE_BITS_Z shr 64 - BlockPos.SIZE_BITS_Z)

fun Vec3d.asLong(): Long {
    var l = 0L
    l = l or (x.toLong() and BlockPos.BITS_X shl BlockPos.BIT_SHIFT_X)
    l = l or (y.toLong() and BlockPos.BITS_Y shl 0)
    l = l or (z.toLong() and BlockPos.BITS_Z shl BlockPos.BIT_SHIFT_Z)
    return l
}

fun fromLong(long: Long) = Vec3d(
    unpackLongX(long).toDouble(),
    unpackLongY(long).toDouble(),
    unpackLongZ(long).toDouble(),
)
