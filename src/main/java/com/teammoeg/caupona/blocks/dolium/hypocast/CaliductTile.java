package com.teammoeg.caupona.blocks.dolium.hypocast;

import com.teammoeg.caupona.CPTileTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CaliductTile extends BathHeatingTile {
	
	
	
	
	public CaliductTile( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.CALIDUCT.get(), pWorldPosition, pBlockState);
		
	}

	@Override
	public void handleMessage(short type, int data) {
	}


	@Override
	public void tick() {
		if(this.level.isClientSide)return;
		super.tick();
		
	}


}
