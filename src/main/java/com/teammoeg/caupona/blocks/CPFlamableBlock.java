package com.teammoeg.caupona.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CPFlamableBlock extends Block {



	public CPFlamableBlock(Properties p_properties, int speed, int flammability) {
		super(p_properties);
		this.speed = speed;
		this.flammability = flammability;
	}
	private final int speed;
	private final int flammability;
	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return speed;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return flammability;
	}


}
