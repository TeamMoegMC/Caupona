package com.teammoeg.caupona.blocks.plants;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class CPStripPillerBlock extends RotatedPillarBlock{
	Block stripped;
	public CPStripPillerBlock(Block stripped,Properties p_55926_) {
		super(p_55926_);
		this.stripped=stripped;
	}

	@Override
	public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction,
			boolean simulate) {
		if(toolAction==ToolActions.AXE_STRIP&&stripped!=null) {
			return stripped.defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
		}
		return super.getToolModifiedState(state, context, toolAction, simulate);
	}



}
