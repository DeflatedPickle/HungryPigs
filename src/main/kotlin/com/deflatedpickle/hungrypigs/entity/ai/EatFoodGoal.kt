/* Copyright (c) 2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate")

package com.deflatedpickle.hungrypigs.entity.ai

import com.deflatedpickle.hungrypigs.api.HasTarget
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.sound.SoundCategory
import net.minecraft.world.event.GameEvent
import java.util.EnumSet

class EatFoodGoal @JvmOverloads constructor(
    val mob: AnimalEntity,
    val chance: Float = 0.15f,
    val target: () -> ItemEntity?
) : Goal() {
    var timer = 0

    init {
        controls = EnumSet.of(Control.MOVE, Control.LOOK, Control.JUMP)
    }

    override fun canStart() =
        if (mob.random.nextFloat() >= chance) {
            false
        } else {
            val target = target()
            target != null && mob.squaredDistanceTo(target) < 4
        }

    override fun shouldContinue(): Boolean {
        if (!target()!!.isAlive) {
            return false
        }

        return true
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
                world.playSound(
                    null as PlayerEntity?,
                    mob.x,
                    mob.y,
                    mob.z,
                    mob.getEatSound(target.stack),
                    SoundCategory.NEUTRAL,
                    1.0f,
                    1.0f + (world.random.nextFloat() - world.random.nextFloat()) * 0.4f
                )
                mob.applyFoodEffects(target.stack, world, mob)
            }

            (mob as HasTarget).also {
                it.targetPos = null
                it.targetStack = ItemStack.EMPTY
            }

            if (mob.isBreedingItem(target.stack)) {
                mob.loveTicks = 300
            }

            target.kill()
        }
    }
}
