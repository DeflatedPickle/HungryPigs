/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.hungrypigs

import net.minecraft.entity.mob.MobEntity
import net.minecraft.item.ItemStack
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

@Suppress("UNUSED")
object HungryPigs : ModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    override fun onInitialize(mod: ModContainer) {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))
    }

    fun onTickMovement(mob: MobEntity, stack: ItemStack) {
        mob.spawnItemParticles(stack, 5)
        mob.playSound(
            mob.getEatSound(stack),
            0.5f + 0.5f * mob.random.nextInt(2).toFloat(),
            (mob.random.nextFloat() - mob.random.nextFloat()) * 0.2f + 1.0f
        )
    }
}
