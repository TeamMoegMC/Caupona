package com.teammoeg.caupona.blocks;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ChimneyPotTileEntity extends CPBaseTile {
	private int process;
	private int processMax;
	int countSoot;
	private int maxStore;
	public ChimneyPotTileEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.CHIMNEY.get(), pWorldPosition, pBlockState);
		processMax=Config.SERVER.chimneyTicks.get();
		maxStore=Config.SERVER.chimneyStorage.get();
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
	public void addAsh(int val) {
		process+=val;
	}
	@Override
	public void tick() {
		if(process>=processMax) {
			if(countSoot<maxStore) {
				countSoot++;
				this.syncData();
			}
			process=0;
		}
	}

}