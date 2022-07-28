package com.teammoeg.caupona.blocks.dolium.hypocast;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPHorizontalTileBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WolfStatueBlock extends CPHorizontalTileBlock<WolfStatueTile> {
	public static final IntegerProperty HEAT=IntegerProperty.create("heat", 0, 3);
	public WolfStatueBlock(Properties blockProps) {
		super(CPTileTypes.WOLF,blockProps);
		super.registerDefaultState(this.defaultBlockState().setValue(HEAT,0));
	}
	static final VoxelShape shapeNS = Block.box(3, 0, 0, 13, 16, 16);
	static final VoxelShape shapeEW = Block.box(0, 0, 3, 16, 16, 13);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if(state.getValue(FACING).getAxis()==Axis.Z)
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
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(HEAT,0);

	}
}
