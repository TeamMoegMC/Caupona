package com.teammoeg.caupona.blocks.dolium.hypocast;

import java.util.function.BiFunction;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPBaseTileBlock;
import com.teammoeg.caupona.blocks.CPHorizontalTileBlock;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WolfStatueBlock extends CPHorizontalTileBlock<WolfStatueTile> {
	public static final IntegerProperty HEAT=IntegerProperty.create("heat", 0, 3);
	public WolfStatueBlock(Properties blockProps) {
		super(CPTileTypes.WOLF,blockProps);
		super.registerDefaultState(this.defaultBlockState().setValue(HEAT,0));
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
