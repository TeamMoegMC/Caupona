package com.teammoeg.caupona.blocks.decoration;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class CPWoodFenceGateBlock extends FenceGateBlock{

	public CPWoodFenceGateBlock(Optional<WoodType> type, Properties properties, Optional<SoundEvent> openSound, Optional<SoundEvent> closeSound) {
		super(type, properties, openSound, closeSound);
		// TODO Auto-generated constructor stub
	}

	public CPWoodFenceGateBlock(Properties properties, SoundEvent openSound, SoundEvent closeSound) {
		super(properties, openSound, closeSound);
		// TODO Auto-generated constructor stub
	}

	public CPWoodFenceGateBlock(WoodType p_type, Properties p_properties) {
		super(p_type, p_properties);
		// TODO Auto-generated constructor stub
	}
	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 5;
	}

	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 20;
	}


}
