package com.teammoeg.caupona.blocks.decoration.mosaic;

import com.teammoeg.caupona.blocks.CPHorizontalBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MosaicBlock extends CPHorizontalBlock{
	public static final EnumProperty<MosaicMaterial> MATERIAL_1=EnumProperty.create("material_1", MosaicMaterial.class);
	public static final EnumProperty<MosaicMaterial> MATERIAL_2=EnumProperty.create("material_2", MosaicMaterial.class);
	public static final EnumProperty<MosaicPattern> PATTERN=EnumProperty.create("material_2", MosaicPattern.class);
	public MosaicBlock(Properties pProperties) {
		super(pProperties);
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(MATERIAL_1).add(MATERIAL_2).add(PATTERN);
	}
	
}
