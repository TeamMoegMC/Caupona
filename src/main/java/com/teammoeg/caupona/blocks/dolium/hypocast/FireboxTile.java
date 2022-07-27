package com.teammoeg.caupona.blocks.dolium.hypocast;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class FireboxTile extends CPBaseTile {

	public FireboxTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.FIREBOX.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
	}

	@Override
	public void tick() {
	}

}
