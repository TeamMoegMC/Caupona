package com.teammoeg.caupona.worldgen;

import java.util.Random;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class WolfberryTreeGrower extends AbstractTreeGrower {
   /**
    * @return a {@link net.minecraft.world.level.levelgen.feature.ConfiguredFeature} of this tree
    */
   protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(Random p_204329_, boolean p_204330_) {
      return CPFeatures.WOLFBERRY;
   }
}