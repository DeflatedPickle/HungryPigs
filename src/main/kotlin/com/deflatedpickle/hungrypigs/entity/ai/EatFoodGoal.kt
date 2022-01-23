/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.hungrypigs.entity.ai

import com.deflatedpickle.hungrypigs.api.HasTarget
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.item.ItemStack
import net.minecraft.world.event.GameEvent
import java.util.EnumSet

class EatFoodGoal(
    val mob: AnimalEntity,
    val target: () -> ItemEntity?
) : Goal() {
    var timer = 0

    init {
        controls = EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP)
    }

    override fun canStart(): Boolean {
        val target = target() ?: return false

        if (!target.isAlive) {
            return false
        }

        return mob.squaredDistanceTo(target.pos) < 4
    }

    override fun start() {
        timer = getTickCount(40)
        mob.world.sendEntityStatus(mob, 10.toByte())
        mob.navigation.stop()
    }

    override fun stop() {
        (mob as HasTarget).also {
            it.targetPos = null
            it.targetStack = ItemStack.EMPTY
        }
    }

    override fun tick() {
        timer = 0.coerceAtLeast(timer - 1)
        if (timer != getTickCount(4)) {
            return
        }

        target()?.let { target ->
            mob.world.let { world ->
                world.emitGameEvent(mob, GameEvent.EAT, mob.cameraBlockPos)
                mob.applyFoodEffects(target.stack, world, mob)
            }

            if (mob.isBreedingItem(target.stack)) {
                mob.loveTicks = 300
            }

            if (target.stack.count - 1 > 0) {
                start()
                target.stack.decrement(1)
            } else {
                (mob as HasTarget).also {
                    it.targetPos = null
                    it.targetStack = ItemStack.EMPTY
                }

                target.kill()
            }
        }
    }
}
