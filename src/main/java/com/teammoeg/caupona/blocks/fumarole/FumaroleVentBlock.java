package com.teammoeg.caupona.blocks.fumarole;

import java.util.Random;
import java.util.function.BiFunction;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPBaseTileBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FumaroleVentBlock extends CPBaseTileBlock<FumaroleVentTileEntity> {
	public static final IntegerProperty HEAT = IntegerProperty.create("heat", 0, 2);
	static final VoxelShape shape = Block.box(0, 0, 0, 16, 6, 16);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	public FumaroleVentBlock(String name, Properties blockProps,
			BiFunction<Block, net.minecraft.world.item.Item.Properties, Item> createItemBlock) {
		super(name, blockProps, CPTileTypes.FUMAROLE, createItemBlock);
		this.registerDefaultState(this.defaultBlockState().setValue(HEAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(HEAT);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(HEAT, 0);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel,
			BlockPos pCurrentPos, BlockPos pFacingPos) {
		return pFacing == Direction.DOWN && !this.canSurvive(pState, pLevel, pCurrentPos)
				? Blocks.AIR.defaultBlockState()
				: super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return canSupportRigidBlock(pLevel, pPos.below());
	}

	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRand) {
		super.animateTick(pState, pLevel, pPos, pRand);
		int heat = pState.getValue(HEAT);
		if (heat > 0) {
			if (pRand.nextInt(10/heat) == 0) {
				pLevel.playLocalSound(pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D,
						SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.5F + pRand.nextFloat(),
						pRand.nextFloat() * 0.7F + 0.6F, false);
			}

			if (pRand.nextInt(5/heat) == 0) {
				for (int i = 0; i < pRand.nextInt(1) + 1; ++i) {
					pLevel.addParticle(ParticleTypes.LAVA, pPos.getX() + 0.5D, pPos.getY() + 0.5D, pPos.getZ() + 0.5D,
							pRand.nextFloat() / 2.0F, 5.0E-5D, pRand.nextFloat() / 2.0F);
				}
			}
		}
	}

}
