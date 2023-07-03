package com.teammoeg.caupona.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CPRoadBlock extends Block {
	protected static final VoxelShape BASE_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);
	public CPRoadBlock(Properties pProperties) {
		super(pProperties);
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return BASE_AABB;
	}

	@Override
	public float getSpeedFactor() {
		return super.getSpeedFactor();
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pos, BlockState pState, Entity entity) {
		if(entity.getBlockY()==pos.getY()) {
			
			if(entity.isSprinting()) {
				
				if(entity.getDeltaMovement().lengthSqr()<=6) {
					float f = entity.getYRot() * ((float)Math.PI / 180F);
					entity.addDeltaMovement(new Vec3(-Mth.sin(f) * 0.2F, 0.0D, Mth.cos(f) * 0.2F));
				}
			}else {
				if(entity.getDeltaMovement().lengthSqr()<=0.25) {
					float f = entity.getYRot() * ((float)Math.PI / 180F);
					entity.addDeltaMovement(new Vec3(-Mth.sin(f) * 0.1F, 0.0D, Mth.cos(f) * 0.1F));
				}
			}
		}
	}



}
