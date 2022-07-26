package com.teammoeg.caupona.worldgen;

import java.util.Random;
import java.util.function.BiConsumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.blocks.plants.BushLogBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class BushFoliagePlacer extends BlobFoliagePlacer {
	public static final Codec<BushFoliagePlacer> CODEC = RecordCodecBuilder.create((p_68427_) -> {
		return blobParts(p_68427_).apply(p_68427_, BushFoliagePlacer::new);
	});

	protected FoliagePlacerType<?> type() {
		return CPFeatures.bfp;
	}

	public BushFoliagePlacer(IntProvider p_161356_, IntProvider p_161357_, int p_161358_) {
		super(p_161356_, p_161357_, p_161358_);
	}

	@Override
	protected void createFoliage(LevelSimulatedReader pLevel, BiConsumer<BlockPos, BlockState> pBlockSetter,
			Random pRandom, TreeConfiguration pConfig, int pMaxFreeTreeHeight, FoliageAttachment pAttachment,
			int pFoliageHeight, int pFoliageRadius, int pOffset) {
		for (int i = pOffset - 1; i >= pOffset - pFoliageHeight; --i) {
			BlockPos pos = pAttachment.pos().offset(0, i, 0);
			pBlockSetter.accept(pos, BushLogBlock.setFullShape(pConfig.trunkProvider.getState(pRandom, pos)));

		}
		super.createFoliage(pLevel, pBlockSetter, pRandom, pConfig, pMaxFreeTreeHeight, pAttachment, pFoliageHeight,
				pFoliageRadius, pOffset);
	}

}
