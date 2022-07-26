package com.teammoeg.caupona.blocks;

import java.util.Iterator;
import java.util.Random;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;

public class FumaroleVentTileEntity extends CPBaseTile implements AbstractStove{
	private final int heat;
	private final int checkmax;
	private final int updatemax;
	public static final TagKey<Fluid> pumice=FluidTags.create(new ResourceLocation(Main.MODID,"pumice_bloom_grow_on"));
	public static final TagKey<Block> hot=BlockTags.create(new ResourceLocation(Main.MODID,"fumarole_hot"));
	public static final TagKey<Block> vhot=BlockTags.create(new ResourceLocation(Main.MODID,"fumarole_very_hot"));
	public FumaroleVentTileEntity( BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.FUMAROLE.get(), pWorldPosition, pBlockState);
		heat=Config.SERVER.fumarolePower.get();
		checkmax=Config.SERVER.fumaroleCheck.get();
		updatemax=Config.SERVER.fumaroleSpeed.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		if(isClient)return;
		update=nbt.getInt("update");
		check=nbt.getInt("check");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		if(isClient)return;
		nbt.putInt("update", update);
		nbt.putInt("check", check);
	}
	int update;
	int check;
	@Override
	public void tick() {
		BlockState bs=this.getBlockState();
		if(bs.getValue(FumaroleVentBlock.HEAT)==2) {
			if(update<updatemax) {
				update++;
			}else {
				update=0;
				placeFumarole(this.getLevel(),this.getBlockPos());
			}
		}
		if(check<checkmax) {
			check++;
		}else {
			check=0;
			BlockState below=this.getLevel().getBlockState(this.getBlockPos().below(2));
			int cheat=bs.getValue(FumaroleVentBlock.HEAT);
			if(below.is(vhot)) {
				if(cheat!=2) {
					bs.setValue(FumaroleVentBlock.HEAT, 2);
					this.getLevel().setBlockAndUpdate(getBlockPos(), bs);
				}
			}else if(below.is(hot)) {
				if(cheat!=1) {
					bs.setValue(FumaroleVentBlock.HEAT, 1);
					this.getLevel().setBlockAndUpdate(getBlockPos(), bs);
				}
			}else if(cheat!=0) {
				bs.setValue(FumaroleVentBlock.HEAT, 0);
				this.getLevel().setBlockAndUpdate(getBlockPos(), bs);
			}
		}
		this.setChanged();
	}
	public static void placeFumarole(Level pLevel,BlockPos pPos) {
		Random pRandom=pLevel.getRandom();
		int dx=(pRandom.nextBoolean()?1:-1)*(pRandom.nextInt(4));
		int dz=(pRandom.nextBoolean()?1:-1)*(pRandom.nextInt(4));
		if(dx==0&&dz==0)return;
		BlockPos pendPos=pPos.offset(dx,0,dz);
		for(int i=0;i<2;i++) {
			BlockState b0=pLevel.getBlockState(pendPos);
			BlockState b1=pLevel.getBlockState(pendPos.below());
			if(b0.isAir()) {
				if(b1.getFluidState().is(pumice)) {
					if(shouldPlacePumice(pLevel,pendPos))
						pLevel.setBlockAndUpdate(pendPos,CPBlocks.PUMICE_BLOOM.defaultBlockState());
					return;
				}
			}
			pendPos=pendPos.below();
		}
	}
	public static boolean shouldPlacePumice(Level pLevel,BlockPos pPos) {
		if(!pLevel.isAreaLoaded(pPos, 1))return false;
		int cnt=0;
		AABB aabb=new AABB(pPos.offset(-1, 0, -1),pPos.offset(1,0,1));
		Iterator<BlockState> it=pLevel.getBlockStates(aabb).iterator();
		while(it.hasNext()) {
			if(it.next().getBlock()==CPBlocks.PUMICE_BLOOM)cnt++;
			if(cnt>=2)return false;
		}
		return true;
	}
	@Override
	public int requestHeat() {
		return heat;
	}

	@Override
	public boolean canEmitHeat() {
		if(heat==0)return false;
		return this.getBlockState().getValue(FumaroleVentBlock.HEAT)!=0;
	}

}
