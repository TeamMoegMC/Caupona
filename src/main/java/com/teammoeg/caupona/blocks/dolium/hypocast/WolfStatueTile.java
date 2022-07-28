package com.teammoeg.caupona.blocks.dolium.hypocast;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WolfStatueTile extends CPBaseTile {
	int ticks;
	private int checkTicks;
	public WolfStatueTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.WOLF.get(), pWorldPosition, pBlockState);
		checkTicks=Config.SERVER.wolfTick.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		ticks=nbt.getInt("process");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process", ticks);
	}

	@Override
	public void tick() {
		if(this.level.isClientSide)return;
		ticks++;
		if(ticks>=checkTicks) {
			ticks=0;
			BlockPos bp=this.getBlockPos();
			for(int i=0;i<4;i++) {
				bp=bp.below();
				BlockEntity te=this.getLevel().getBlockEntity(bp);
				if(te instanceof BathHeatingTile) {
					int cheat=((BathHeatingTile) te).getHeat();
					BlockState bs=this.getBlockState();
					int bheat=bs.getValue(WolfStatueBlock.HEAT);
					if(cheat!=bheat)
						this.getLevel().setBlockAndUpdate(this.getBlockPos(),bs.setValue(WolfStatueBlock.HEAT,cheat));
					return;
				}
			}
		}
	}

}
