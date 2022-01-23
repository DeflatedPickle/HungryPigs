/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.hungrypigs.entity.data

import com.deflatedpickle.hungrypigs.extension.asLong
import com.deflatedpickle.hungrypigs.extension.fromLong
import net.minecraft.entity.data.TrackedDataHandler
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.math.Vec3d

object TrackedDataHandlers {
    val VEC3D: TrackedDataHandler<Vec3d> = object : TrackedDataHandler<Vec3d> {
        override fun write(packetByteBuf: PacketByteBuf, blockPos: Vec3d) {
            packetByteBuf.writeLong(blockPos.asLong())
        }

        override fun read(packetByteBuf: PacketByteBuf) = fromLong(packetByteBuf.readLong())
        override fun copy(blockPos: Vec3d?) = blockPos
    }

    init {
        TrackedDataHandlerRegistry.register(VEC3D)
    }
}
