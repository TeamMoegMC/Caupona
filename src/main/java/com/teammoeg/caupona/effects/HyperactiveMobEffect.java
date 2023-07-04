package com.teammoeg.caupona.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class HyperactiveMobEffect extends MobEffect {
   protected final double multiplier;

   public HyperactiveMobEffect(MobEffectCategory pCategory, int pColor, double pMultiplier) {
      super(pCategory, pColor);
      this.multiplier = pMultiplier;
   }

   public double getAttributeModifierValue(int pAmplifier, AttributeModifier pModifier) {
      return this.multiplier * (double)(pAmplifier + 1);
   }
}