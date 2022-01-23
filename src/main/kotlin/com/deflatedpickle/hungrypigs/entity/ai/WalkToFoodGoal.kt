/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.hungrypigs.entity.ai

import com.deflatedpickle.hungrypigs.api.HasTarget
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.mob.MobEntity
import java.util.EnumSet

class WalkToFoodGoal @JvmOverloads constructor(
    val mob: MobEntity,
    val chance: Float = 0.15f,
    val range: Float = 12f,
) : Goal() {
    var target: ItemEntity? = null

    init {
        controls = EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP)
    }

    override fun canStart() =
        if (mob.random.nextFloat() >= chance) {
            false
        } else {
            if (target == null) {
                target = mob.world.getEntitiesByClass(
                    ItemEntity::class.java,
                    mob.boundingBox.expand(range.toDouble())
                ) { true }
                    .sortedBy { it.blockPos.getSquaredDistance(mob.x, mob.y, mob.z, false) }
                    .firstOrNull { it.stack.item.isFood }

                target?.let { target ->
                    (mob as HasTarget).also {
                        it.targetPos = target.pos
                        it.targetStack = target.stack
                    }
                }
            }

            target != null
        }

    override fun shouldContinue(): Boolean {
        if (!target!!.isAlive) {
            target = null
            return false
        } else if (mob.squaredDistanceTo(target) > (this.range * this.range).toDouble() || mob.squaredDistanceTo(target!!.pos) < 4
        ) {
            return false
        }

        return true
    }

    override fun tick() {
        target?.let { target ->
            mob.navigation.startMovingTo(target, 1.0)
        }
    }
}
