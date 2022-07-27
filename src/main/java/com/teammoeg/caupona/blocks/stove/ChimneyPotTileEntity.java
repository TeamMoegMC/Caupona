package com.teammoeg.caupona.blocks.stove;

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
		process=nbt.getInt("process");
		countSoot=nbt.getInt("soot");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process", process);
		nbt.putInt("soot", countSoot);
	}
	public void addAsh(int val) {
		process+=val;
		this.setChanged();
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
