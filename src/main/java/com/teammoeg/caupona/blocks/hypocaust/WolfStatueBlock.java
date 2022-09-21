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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.blocks.hypocaust;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class WolfStatueBlock extends CPHorizontalEntityBlock<WolfStatueTile> implements SimpleWaterloggedBlock {
	public static final IntegerProperty HEAT = IntegerProperty.create("heat", 0, 2);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private boolean gch;

	public WolfStatueBlock(Properties blockProps) {
		super(CPTileTypes.WOLF, blockProps);
		super.registerDefaultState(this.defaultBlockState().setValue(HEAT, 0).setValue(WATERLOGGED, false));
		gch = Config.SERVER.genCH.get();
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
		builder.add(HEAT).add(WATERLOGGED);
	}

	@SuppressWarnings("resource")
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
				.setValue(HEAT, 0).setValue(WATERLOGGED,
						context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);

	}

	@SuppressWarnings("deprecation")
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
			BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return super.updateShape(pState, pFacing, pState, pLevel, pCurrentPos, pFacingPos);
	}

	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
		super.entityInside(pState, pLevel, pPos, pEntity);
		BlockEntity te = pLevel.getBlockEntity(pPos);
		if (te instanceof WolfStatueTile wst) {
			if (wst.isVeryHot)
				pEntity.hurt(DamageSource.HOT_FLOOR, pState.getValue(HEAT));
		}
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		BlockEntity te = pLevel.getBlockEntity(pPos);
		if (te instanceof WolfStatueTile wst) {
			if (wst.isVeryHot)
				pEntity.hurt(DamageSource.HOT_FLOOR, pState.getValue(HEAT));
		}
		super.stepOn(pLevel, pPos, pState, pEntity);
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand,
			BlockHitResult pHit) {
		if (gch && !pLevel.isClientSide) {
			if (pPlayer.getItemInHand(pHand).is(CPItems.acquacotta) && pState.getValue(HEAT) > 0) {
				pPlayer.getItemInHand(pHand).shrink(1);

				ItemHandlerHelper.giveItemToPlayer(pPlayer, new ItemStack(CPItems.haze));
				return InteractionResult.SUCCESS;
			}
		}
		return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
	}


	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		BlockEntity te = pLevel.getBlockEntity(pPos);
		int ret=pState.getValue(HEAT)*3;
		if (te instanceof WolfStatueTile wst) {
			if (wst.isVeryHot)
				ret+=9;
		}
		return ret;
	}
}
