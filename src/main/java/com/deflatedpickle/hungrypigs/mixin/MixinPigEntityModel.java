/* Copyright (c) 2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.hungrypigs.mixin;

import com.deflatedpickle.hungrypigs.api.EatFood;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@SuppressWarnings({"unused", "UnusedMixin", "rawtypes", "unchecked"})
@Mixin(PigEntityModel.class)
public abstract class MixinPigEntityModel extends QuadrupedEntityModel {
  private float headPitchModifier;

  protected MixinPigEntityModel(
      ModelPart root,
      boolean headScaled,
      float childHeadYOffset,
      float childHeadZOffset,
      float invertedChildHeadScale,
      float invertedChildBodyScale,
      int childBodyYOffset) {
    super(
        root,
        headScaled,
        childHeadYOffset,
        childHeadZOffset,
        invertedChildHeadScale,
        invertedChildBodyScale,
        childBodyYOffset);
  }

  @Override
  public void animateModel(Entity entity, float limbAngle, float limbDistance, float tickDelta) {
    super.animateModel(entity, limbAngle, limbDistance, tickDelta);

    this.head.pivotY = 12.0f + ((EatFood) entity).getNeckAngle(tickDelta) * 9.0f;
    this.headPitchModifier = ((EatFood) entity).getHeadAngle(tickDelta);
  }

  @Override
  public void setAngles(Entity entity, float f, float g, float h, float i, float j) {
    super.setAngles(entity, f, g, h, i, j);
    this.head.pitch = this.headPitchModifier;
  }
}
