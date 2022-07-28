/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.blocks.hypocast;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.blocks.CPHorizontalTileBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class WolfStatueBlock extends CPHorizontalTileBlock<WolfStatueTile> {
	public static final IntegerProperty HEAT = IntegerProperty.create("heat", 0, 3);
	private boolean gch;
	public WolfStatueBlock(Properties blockProps) {
		super(CPTileTypes.WOLF, blockProps);
		super.registerDefaultState(this.defaultBlockState().setValue(HEAT, 0));
		gch=Config.SERVER.genCH.get();
	}

	static final VoxelShape shapeNS = Block.box(3, 0, 0, 13, 16, 16);
	static final VoxelShape shapeEW = Block.box(0, 0, 3, 16, 16, 13);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (state.getValue(FACING).getAxis() == Axis.Z)
			return shapeNS;
		return shapeEW;

	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HEAT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HEAT,
				0);

	}

	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@Override
	public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
		super.entityInside(pState, pLevel, pPos, pEntity);
		BlockEntity te = pLevel.getBlockEntity(pPos);
		if (te instanceof WolfStatueTile) {
			WolfStatueTile wst = (WolfStatueTile) te;
			if (wst.isVeryHot)
				pEntity.hurt(DamageSource.HOT_FLOOR, pState.getValue(HEAT));
		}
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		BlockEntity te = pLevel.getBlockEntity(pPos);
		if (te instanceof WolfStatueTile) {
			WolfStatueTile wst = (WolfStatueTile) te;
			if (wst.isVeryHot)
				pEntity.hurt(DamageSource.HOT_FLOOR, pState.getValue(HEAT));
		}
		super.stepOn(pLevel, pPos, pState, pEntity);
	}
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {
		if(gch&&!pLevel.isClientSide) {
			if (pPlayer.getItemInHand(pHand).is(CPItems.acquacotta)&&pState.getValue(HEAT)>0) {
				pPlayer.getItemInHand(pHand).shrink(1);
				
				ItemHandlerHelper.giveItemToPlayer(pPlayer, new ItemStack(CPItems.haze));
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}
}
