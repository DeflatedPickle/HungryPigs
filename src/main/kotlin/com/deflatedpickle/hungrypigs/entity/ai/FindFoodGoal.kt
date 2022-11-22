/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.hungrypigs.entity.ai

import com.deflatedpickle.hungrypigs.api.HasTarget
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.mob.MobEntity

class FindFoodGoal @JvmOverloads constructor(
    val mob: MobEntity,
    val range: Float = 12f,
) : Goal() {
    var target: ItemEntity? = null

    override fun canStart() = target == null || !target!!.isAlive

    override fun tick() {
        mob.world.getEntitiesByClass(
            ItemEntity::class.java,
            mob.boundingBox.expand(range.toDouble())
        ) { true }
            .sortedBy { it.blockPos.getSquaredDistance(mob.x, mob.y, mob.z) }
            .find { it.stack.item.isFood }?.let { item ->
                target = item.also { target ->
                    (mob as HasTarget).also {
                        it.targetPos = target.pos
                        it.targetStack = target.stack
                    }
                }
            }
    }
}
