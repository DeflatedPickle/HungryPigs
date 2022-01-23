/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.hungrypigs.mixin;

import com.deflatedpickle.hungrypigs.HungryPigs;
import com.deflatedpickle.hungrypigs.api.EatFood;
import com.deflatedpickle.hungrypigs.api.HasTarget;
import com.deflatedpickle.hungrypigs.entity.ai.EatFoodGoal;
import com.deflatedpickle.hungrypigs.entity.ai.FindFoodGoal;
import com.deflatedpickle.hungrypigs.entity.ai.WalkToFoodGoal;
import com.deflatedpickle.hungrypigs.entity.data.TrackedDataHandlers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("UnusedMixin")
@Mixin(PigEntity.class)
public abstract class MixinPigEntity extends AnimalEntity implements HasTarget, EatFood {
  private static final TrackedData<Vec3d> target =
      DataTracker.registerData(PigEntity.class, TrackedDataHandlers.INSTANCE.getVEC3D());
  private static final TrackedData<ItemStack> stack =
      DataTracker.registerData(PigEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

  protected MixinPigEntity(EntityType<? extends AnimalEntity> entityType, World world) {
    super(entityType, world);
  }

  @Nullable
  @Override
  public Vec3d getTargetPos() {
    return dataTracker.get(target);
  }

  @Override
  public void setTargetPos(Vec3d value) {
    dataTracker.set(target, value);
  }

  @Nullable
  @Override
  public ItemStack getTargetStack() {
    return dataTracker.get(stack);
  }

  @Override
  public void setTargetStack(@Nullable ItemStack targetStack) {
    dataTracker.set(stack, targetStack);
  }

  @Inject(method = "initDataTracker", at = @At("TAIL"))
  public void onInitDataTracker(CallbackInfo ci) {
    dataTracker.startTracking(target, null);
    dataTracker.startTracking(stack, ItemStack.EMPTY);
  }

  @Inject(method = "initGoals", at = @At("TAIL"))
  public void onInitGoals(CallbackInfo ci) {
    var findFood = new FindFoodGoal(this);
    var walkToFood = new WalkToFoodGoal(this, findFood::getTarget);
    goalSelector.add(2, findFood);
    goalSelector.add(2, walkToFood);
    goalSelector.add(2, new EatFoodGoal(this, findFood::getTarget));
  }

  @Override
  public void tickMovement() {
    if (this.world.isClient) {
      this.eatFoodTimer = Math.max(0, this.eatFoodTimer - 1);
    }

    System.out.println(((HasTarget) this).getTargetPos());
    System.out.println(((HasTarget) this).getTargetStack());

    if (((HasTarget) this).getTargetPos() != null
        && ((HasTarget) this).getTargetStack() != ItemStack.EMPTY) {
      System.out.println(this.squaredDistanceTo(((HasTarget) this).getTargetPos()));
    }

    if (((HasTarget) this).getTargetPos() != null
        && ((HasTarget) this).getTargetStack() != ItemStack.EMPTY
        && this.squaredDistanceTo(((HasTarget) this).getTargetPos()) < 16
        && this.eatFoodTimer == 0) {
      HungryPigs.INSTANCE.onTickMovement(this, ((HasTarget) this).getTargetStack());
    }

    super.tickMovement();
  }

  private int eatFoodTimer;

  @Override
  public int getEatFoodTimer() {
    return eatFoodTimer;
  }

  @Override
  public void setEatFoodTimer(int eatFoodTimer) {
    this.eatFoodTimer = eatFoodTimer;
  }

  public void handleStatus(byte status) {
    if (status == 10) {
      this.eatFoodTimer = 40;
    } else {
      super.handleStatus(status);
    }
  }

  @Override
  public float getNeckAngle(float delta) {
    if (this.eatFoodTimer <= 0) {
      return 0.0F;
    } else if (this.eatFoodTimer >= 4 && this.eatFoodTimer <= 36) {
      return 0.4F;
    } else {
      return this.eatFoodTimer < 4
          ? ((float) this.eatFoodTimer - delta) / 4.0F
          : -((float) (this.eatFoodTimer - 40) - delta) / 4.0F;
    }
  }

  @Override
  public float getHeadAngle(float delta) {
    if (this.eatFoodTimer > 4 && this.eatFoodTimer <= 36) {
      float f = ((float) (this.eatFoodTimer - 4) - delta) / 32.0F;
      return 0.62831855F + 0.21991149F * MathHelper.sin(f * 28.7F);
    } else {
      return this.eatFoodTimer > 0 ? 0.62831855F : this.getPitch() * 0.017453292F;
    }
  }
}
