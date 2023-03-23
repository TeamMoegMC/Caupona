package com.teammoeg.caupona.worldgen;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class DefaultTreeGrower extends AbstractTreeGrower {
	ResourceKey<ConfiguredFeature<?, ?>> key;
	public DefaultTreeGrower(ResourceKey<ConfiguredFeature<?, ?>> key) {
		this.key = key;
	}
	@Override
	protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource pRandom, boolean pHasFlowers) {
		return key;
	}
	public static Supplier<AbstractTreeGrower> supply(ResourceKey<ConfiguredFeature<?, ?>> key) {
		return ()->new DefaultTreeGrower(key);
	}
}
