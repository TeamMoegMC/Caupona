/*
 * Copyright (c) 2024 TeamMoeg
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

import org.jetbrains.annotations.Nullable;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WolfStatueBlock extends CPHorizontalEntityBlock<WolfStatueBlockEntity> implements SimpleWaterloggedBlock {
	public static final IntegerProperty HEAT = IntegerProperty.create("heat", 0, 2);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public WolfStatueBlock(Properties blockProps) {
		super(CPBlockEntityTypes.WOLF_STATUE, blockProps);
		super.registerDefaultState(this.defaultBlockState().setValue(HEAT, 0).setValue(WATERLOGGED, false));
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
	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState,
		RandomSource random) {
		if (state.getValue(WATERLOGGED)) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);

	}
	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	public boolean isPathfindable(BlockState pState, PathComputationType pType) {
		return false;
	}


	@Override
	protected void entityInside(BlockState p_state, Level p_level, BlockPos p_pos, Entity p_entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
		super.entityInside(p_state, p_level, p_pos, p_entity, effectApplier, isPrecise);
		if (p_level.getBlockEntity(p_pos) instanceof WolfStatueBlockEntity wst&&p_level instanceof ServerLevel sl) {
			if (wst.isVeryHot)
				p_entity.hurtServer(sl,p_level.damageSources().hotFloor(), p_state.getValue(HEAT));
		}
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		if (pLevel.getBlockEntity(pPos) instanceof WolfStatueBlockEntity wst) {
			if (wst.isVeryHot)
				pEntity.hurt(pLevel.damageSources().hotFloor(), pState.getValue(HEAT));
		}
		super.stepOn(pLevel, pPos, pState, pEntity);
	}




	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction dir) {
		int ret=pState.getValue(HEAT)*3;
		if (pLevel.getBlockEntity(pPos) instanceof WolfStatueBlockEntity wst) {
			if (wst.isVeryHot)
				ret+=9;
		}
		return ret;
	}
	@Override
	public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
		return PathType.FIRE;
	}
}
