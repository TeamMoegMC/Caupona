package com.teammoeg.caupona.blocks.plants;

import com.teammoeg.caupona.CPTags.Blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class SnailBlock extends FruitBlock {

	public SnailBlock(Properties p_52247_) {
		super(p_52247_);
	}
	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return pLevel.getBlockState(pPos.above()).is(Blocks.SNAIL_GROWABLE_ON);
	}
}
