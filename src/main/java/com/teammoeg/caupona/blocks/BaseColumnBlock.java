package com.teammoeg.caupona.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseColumnBlock extends Block {
	static final VoxelShape shaft=Block.box(2, 0, 2, 14, 16, 14);
	boolean isPlinth;
	static final VoxelShape plinth=Shapes.or(shaft,Block.box(0, 0, 0, 16, 4, 16));
	
	public BaseColumnBlock(Properties p_49795_, boolean isPlinth) {
		super(p_49795_);
		this.isPlinth = isPlinth;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return isPlinth?plinth:shaft;
	}
}
