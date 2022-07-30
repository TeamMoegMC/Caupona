package com.teammoeg.caupona.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ColumnCapitalBlock extends CPHorizontalBlock {
	static final VoxelShape shaft=Block.box(2, 0, 2, 14, 16, 14);
	boolean isLarge;
	static final VoxelShape small=Shapes.or(shaft,Block.box(0, 12, 0, 16, 16, 16));
	static final VoxelShape large=Shapes.or(shaft,Block.box(0, 9, 0, 16, 16, 16));
	public ColumnCapitalBlock(Properties p_54120_, boolean isLarge) {
		super(p_54120_);
		this.isLarge = isLarge;
	}
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return isLarge?large:small;
	}
}
