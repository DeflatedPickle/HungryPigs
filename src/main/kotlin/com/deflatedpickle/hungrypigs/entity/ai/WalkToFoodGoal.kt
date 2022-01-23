/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.hungrypigs.entity.ai

import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.mob.MobEntity
import java.util.EnumSet

class WalkToFoodGoal @JvmOverloads constructor(
    val mob: MobEntity,
    val range: Float = 12f,
    val target: () -> ItemEntity?
) : Goal() {
    init {
        controls = EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP)
    }

    override fun canStart(): Boolean {
        val target = target() ?: return false

        if (!target.isAlive) {
            return false
        } else if (mob.squaredDistanceTo(target) > (this.range * this.range).toDouble() || mob.squaredDistanceTo(target.pos) < 4) {
            return false
        }

        return true
    }

    override fun tick() {
        target()?.let { target ->
            mob.navigation.startMovingTo(target, 1.0)
        }
    }
}
