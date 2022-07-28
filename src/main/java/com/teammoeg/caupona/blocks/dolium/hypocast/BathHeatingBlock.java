package com.teammoeg.caupona.blocks.dolium.hypocast;

import java.util.Random;

import com.teammoeg.caupona.blocks.CPHorizontalTileBlock;
import com.teammoeg.caupona.client.Particles;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

public abstract class BathHeatingBlock<V extends BathHeatingTile> extends CPHorizontalTileBlock<V> {

	public BathHeatingBlock(RegistryObject<BlockEntityType<V>> te, Properties p_54120_) {
		super(te, p_54120_);
	}

	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRandom) {
		super.animateTick(pState, pLevel, pPos, pRandom);
		if(pRandom.nextDouble()<0.05&&pLevel.getFluidState(pPos.above()).is(FluidTags.WATER)) {
			BlockEntity te=pLevel.getBlockEntity(pPos);
			if(te instanceof BathHeatingTile) {
				if(((BathHeatingTile) te).getHeat()>0) {
					pLevel.addParticle(Particles.STEAM.get(), pPos.getX() + pRandom.nextFloat(),pPos.getY()+2, pPos.getZ() + pRandom.nextFloat(), 0.0D,
					0.0D, 0.0D);
				}
			}
		}
	}

}
