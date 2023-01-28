package com.teammoeg.caupona.compat.treechop;

import ht.treechop.api.ISimpleChoppableBlock;
import ht.treechop.api.IStrippableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SlimTreeHandler implements ISimpleChoppableBlock,IStrippableBlock {

	@Override
	public int getRadius(BlockGetter level, BlockPos blockPos, BlockState blockState) {
		return 4;
	}


	@Override
	public BlockState getStrippedState(BlockGetter arg0, BlockPos arg1, BlockState arg2) {
		return Blocks.STRIPPED_BIRCH_LOG.defaultBlockState();
	}




}
