package com.teammoeg.caupona.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CounterDoliumBlock extends CPHorizontalBlock {

	public CounterDoliumBlock(Properties p_54120_) {
		super(p_54120_);
	}
	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}
	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}
	static final VoxelShape shape = Shapes.or(
			Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 4, 0, 4, 16, 16)),
			Shapes.or(Block.box(0, 4, 0, 16, 16, 4), Shapes
					.or(Block.box(12, 4, 0, 16, 16, 16), Block.box(0, 4, 12, 16, 16, 16))));

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos,
			CollisionContext context) {
		return shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}
}
