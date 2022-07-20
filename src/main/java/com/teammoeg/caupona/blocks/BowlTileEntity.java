package com.teammoeg.caupona.blocks;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.network.INetworkTile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class BowlTileEntity extends INetworkTile {
	public ItemStack internal;

	public BowlTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.BOWL.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		internal = ItemStack.of(nbt.getCompound("bowl"));
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.put("bowl", internal.serializeNBT());
	}

	@Override
	public void tick() {
	}

}
