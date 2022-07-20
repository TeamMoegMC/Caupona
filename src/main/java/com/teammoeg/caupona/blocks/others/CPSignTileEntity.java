package com.teammoeg.caupona.blocks.others;

import com.teammoeg.caupona.CPTileTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CPSignTileEntity extends SignBlockEntity {
	public CPSignTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(pWorldPosition, pBlockState);
	}

	@Override
	public BlockEntityType<CPSignTileEntity> getType() {
		return CPTileTypes.SIGN.get();
	}
}