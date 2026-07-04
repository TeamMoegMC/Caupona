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

package com.teammoeg.caupona.blocks.decoration;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpokedFenceBlock extends Block implements SimpleWaterloggedBlock {
	public static final BooleanProperty EAST_WALL = BooleanProperty.create("east");
	public static final BooleanProperty NORTH_WALL = BooleanProperty.create("north");
	public static final BooleanProperty SOUTH_WALL = BooleanProperty.create("south");
	public static final BooleanProperty WEST_WALL = BooleanProperty.create("west");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private final Map<BlockState, VoxelShape> shapeByIndex;
	private final Map<BlockState, VoxelShape> collisionShapeByIndex;

	public SpokedFenceBlock(BlockBehaviour.Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH_WALL, false).setValue(EAST_WALL, false)
			.setValue(SOUTH_WALL, false).setValue(WEST_WALL, false).setValue(WATERLOGGED, false));
		this.shapeByIndex = this.makeShapes(2.0F, 2.0F, 16.0F, 0.0F, 16.0F);
		this.collisionShapeByIndex = this.makeShapes(2.0F, 2.0F, 24.0F, 0.0F, 24.0F);
	}

	private static VoxelShape applyWallShape(VoxelShape pBaseShape, boolean pHeight, VoxelShape pTallShape) {
		if (pHeight == true) {
			return Shapes.or(pBaseShape, pTallShape);
		}
		return pBaseShape;
	}

	private Map<BlockState, VoxelShape> makeShapes(float pWidth, float pDepth, float pWallPostHeight, float pWallMinY, float pWallTallHeight) {
		float f = 8.0F - pWidth;
		float f1 = 8.0F + pWidth;
		float f2 = 8.0F - pDepth;
		float f3 = 8.0F + pDepth;
		VoxelShape voxelshape = Block.box(f, 0.0D, f, f1, pWallPostHeight, f1);
		VoxelShape voxelshape5 = Block.box(f2, pWallMinY, 0.0D, f3, pWallTallHeight, f3);
		VoxelShape voxelshape6 = Block.box(f2, pWallMinY, f2, f3, pWallTallHeight, 16.0D);
		VoxelShape voxelshape7 = Block.box(0.0D, pWallMinY, f2, f3, pWallTallHeight, f3);
		VoxelShape voxelshape8 = Block.box(f2, pWallMinY, f2, 16.0D, pWallTallHeight, f3);
		ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

		for (Boolean wallside : EAST_WALL.getPossibleValues()) {
			for (Boolean wallside1 : NORTH_WALL.getPossibleValues()) {
				for (Boolean wallside2 : WEST_WALL.getPossibleValues()) {
					for (Boolean wallside3 : SOUTH_WALL.getPossibleValues()) {
						VoxelShape voxelshape9 = voxelshape;
						voxelshape9 = applyWallShape(voxelshape9, wallside, voxelshape8);
						voxelshape9 = applyWallShape(voxelshape9, wallside2, voxelshape7);
						voxelshape9 = applyWallShape(voxelshape9, wallside1, voxelshape5);
						voxelshape9 = applyWallShape(voxelshape9, wallside3, voxelshape6);

						BlockState blockstate = this.defaultBlockState().setValue(EAST_WALL, wallside).setValue(WEST_WALL, wallside2).setValue(NORTH_WALL, wallside1).setValue(SOUTH_WALL, wallside3);
						builder.put(blockstate.setValue(WATERLOGGED, Boolean.valueOf(false)), voxelshape9);
						builder.put(blockstate.setValue(WATERLOGGED, Boolean.valueOf(true)), voxelshape9);
					}
				}
			}

		}

		return builder.build();
	}
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return this.shapeByIndex.get(pState);
	}
	@Override
	public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return this.collisionShapeByIndex.get(pState);
	}

	
	@Override
	public @Nullable PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
		return PathType.BLOCKED;
	}

	private static boolean connectsTo(BlockState pState, boolean pSideSolid, Direction pDirection) {
		Block block = pState.getBlock();
		boolean flag = block instanceof FenceGateBlock && FenceGateBlock.connectsToDirection(pState, pDirection);
		return pState.is(BlockTags.WALLS) || !isExceptionForConnection(pState) && pSideSolid || block instanceof IronBarsBlock || flag || block instanceof SpokedFenceBlock;
	}
	@SuppressWarnings("resource")
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		LevelReader levelreader = pContext.getLevel();
		BlockPos blockpos = pContext.getClickedPos();
		FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.east();
		BlockPos blockpos3 = blockpos.south();
		BlockPos blockpos4 = blockpos.west();
		BlockState blockstate = levelreader.getBlockState(blockpos1);
		BlockState blockstate1 = levelreader.getBlockState(blockpos2);
		BlockState blockstate2 = levelreader.getBlockState(blockpos3);
		BlockState blockstate3 = levelreader.getBlockState(blockpos4);
		boolean flag = SpokedFenceBlock.connectsTo(blockstate, blockstate.isFaceSturdy(levelreader, blockpos1, Direction.SOUTH), Direction.SOUTH);
		boolean flag1 = SpokedFenceBlock.connectsTo(blockstate1, blockstate1.isFaceSturdy(levelreader, blockpos2, Direction.WEST), Direction.WEST);
		boolean flag2 = SpokedFenceBlock.connectsTo(blockstate2, blockstate2.isFaceSturdy(levelreader, blockpos3, Direction.NORTH), Direction.NORTH);
		boolean flag3 = SpokedFenceBlock.connectsTo(blockstate3, blockstate3.isFaceSturdy(levelreader, blockpos4, Direction.EAST), Direction.EAST);
		BlockState blockstate5 = this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
		return SpokedFenceBlock.updateShape(blockstate5, flag, flag1, flag2, flag3);
	}

	/**
	 * Update the provided state given the provided neighbor direction and neighbor
	 * state, returning a new state. For example, fences make their connections to
	 * the passed in state if possible, and wet concrete powder immediately returns
	 * its solidified counterpart. Note that this method should ideally consider
	 * only the specific direction passed in.
	 */

	@Override
	protected BlockState updateShape(BlockState pState, LevelReader pLevel, ScheduledTickAccess ticks, BlockPos pCurrentPos, Direction pFacing, BlockPos pFacingPos, BlockState pFacingState,
		RandomSource random) {
		if (pState.getValue(WATERLOGGED)) {
			ticks.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}

		if (pFacing == Direction.DOWN) {
			return super.updateShape(pState,pLevel,ticks, pCurrentPos, pFacing,  pFacingPos, pFacingState, random);
		}
		return pFacing == Direction.UP ? SpokedFenceBlock.topUpdate(pState) : SpokedFenceBlock.sideUpdate(pLevel, pState, pFacingPos, pFacingState, pFacing);}

	private static boolean isConnected(BlockState pState, BooleanProperty pHeightProperty) {
		return pState.getValue(pHeightProperty);
	}

	private static BlockState topUpdate(BlockState pState) {
		boolean flag = isConnected(pState, NORTH_WALL);
		boolean flag1 = isConnected(pState, EAST_WALL);
		boolean flag2 = isConnected(pState, SOUTH_WALL);
		boolean flag3 = isConnected(pState, WEST_WALL);
		return SpokedFenceBlock.updateShape(pState, flag, flag1, flag2, flag3);
	}

	private static BlockState sideUpdate(LevelReader pLevel, BlockState pFirstState, BlockPos pSecondPos, BlockState pSecondState, Direction pDir) {
		Direction direction = pDir.getOpposite();
		boolean flag = pDir == Direction.NORTH ? SpokedFenceBlock.connectsTo(pSecondState, pSecondState.isFaceSturdy(pLevel, pSecondPos, direction), direction) : isConnected(pFirstState, NORTH_WALL);
		boolean flag1 = pDir == Direction.EAST ? SpokedFenceBlock.connectsTo(pSecondState, pSecondState.isFaceSturdy(pLevel, pSecondPos, direction), direction) : isConnected(pFirstState, EAST_WALL);
		boolean flag2 = pDir == Direction.SOUTH ? SpokedFenceBlock.connectsTo(pSecondState, pSecondState.isFaceSturdy(pLevel, pSecondPos, direction), direction) : isConnected(pFirstState, SOUTH_WALL);
		boolean flag3 = pDir == Direction.WEST ? SpokedFenceBlock.connectsTo(pSecondState, pSecondState.isFaceSturdy(pLevel, pSecondPos, direction), direction) : isConnected(pFirstState, WEST_WALL);
		return SpokedFenceBlock.updateShape(pFirstState, flag, flag1, flag2, flag3);
	}

	private static BlockState updateShape(BlockState pState, boolean pNorthConnection, boolean pEastConnection, boolean pSouthConnection, boolean pWestConnection) {
		BlockState blockstate = SpokedFenceBlock.updateSides(pState, pNorthConnection, pEastConnection, pSouthConnection, pWestConnection);
		return blockstate;
	}

	private static BlockState updateSides(BlockState pState, boolean pNorthConnection, boolean pEastConnection, boolean pSouthConnection, boolean pWestConnection) {
		return pState.setValue(NORTH_WALL, pNorthConnection)
			.setValue(EAST_WALL, pEastConnection)
			.setValue(SOUTH_WALL, pSouthConnection)
			.setValue(WEST_WALL, pWestConnection);
	}
	@Override
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState pState) {
		return !pState.getValue(WATERLOGGED);
	}
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(NORTH_WALL, EAST_WALL, WEST_WALL, SOUTH_WALL, WATERLOGGED);
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via
	 *             {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#rotate}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		switch (pRotation) {
		case CLOCKWISE_180:
			return pState.setValue(NORTH_WALL, pState.getValue(SOUTH_WALL)).setValue(EAST_WALL, pState.getValue(WEST_WALL)).setValue(SOUTH_WALL, pState.getValue(NORTH_WALL)).setValue(WEST_WALL,
				pState.getValue(EAST_WALL));
		case COUNTERCLOCKWISE_90:
			return pState.setValue(NORTH_WALL, pState.getValue(EAST_WALL)).setValue(EAST_WALL, pState.getValue(SOUTH_WALL)).setValue(SOUTH_WALL, pState.getValue(WEST_WALL)).setValue(WEST_WALL,
				pState.getValue(NORTH_WALL));
		case CLOCKWISE_90:
			return pState.setValue(NORTH_WALL, pState.getValue(WEST_WALL)).setValue(EAST_WALL, pState.getValue(NORTH_WALL)).setValue(SOUTH_WALL, pState.getValue(EAST_WALL)).setValue(WEST_WALL,
				pState.getValue(SOUTH_WALL));
		default:
			return pState;
		}
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via
	 *             {@link net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase#mirror}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		switch (pMirror) {
		case LEFT_RIGHT:
			return pState.setValue(NORTH_WALL, pState.getValue(SOUTH_WALL)).setValue(SOUTH_WALL, pState.getValue(NORTH_WALL));
		case FRONT_BACK:
			return pState.setValue(EAST_WALL, pState.getValue(WEST_WALL)).setValue(WEST_WALL, pState.getValue(EAST_WALL));
		default:
			return super.mirror(pState, pMirror);
		}
	}
	@Override
	public boolean isPathfindable(BlockState pState, PathComputationType pType) {
	      return false;
	}
}