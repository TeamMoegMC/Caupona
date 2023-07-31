package com.teammoeg.caupona.blocks.decoration.mosaic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MosaicBlock extends Block{
	public static final EnumProperty<MosaicMaterial> MATERIAL_1=EnumProperty.create("material_1", MosaicMaterial.class);
	
	public static final EnumProperty<MosaicMaterial> MATERIAL_2=EnumProperty.create("material_2", MosaicMaterial.class);
	@SuppressWarnings("unchecked")
	public static final EnumProperty<MosaicMaterial>[] MATERIAL=new EnumProperty[] {MATERIAL_1,MATERIAL_2};
	public static final EnumProperty<MosaicPattern> PATTERN=EnumProperty.create("pattern", MosaicPattern.class);
	public MosaicBlock(Properties pProperties) {
		super(pProperties);
	}
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(MATERIAL_1).add(MATERIAL_2).add(PATTERN);
	}
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		// TODO Auto-generated method stub
		BlockState bs=super.getStateForPlacement(pContext);
		CompoundTag tag=pContext.getItemInHand().getOrCreateTagElement("caupona:mosaic");
		if(tag.isEmpty())
			return bs;
		MosaicPattern pattern=MosaicPattern.valueOf(tag.getString("pattern"));
		MosaicMaterial m1=MosaicMaterial.valueOf(tag.getString("mat1"));
		MosaicMaterial m2=MosaicMaterial.valueOf(tag.getString("mat2"));
		return bs.setValue(MosaicBlock.MATERIAL_1, m1).setValue(MosaicBlock.MATERIAL_2, m2).setValue(MosaicBlock.PATTERN, pattern);
	}
	@Override
	public String getDescriptionId() {
		// TODO Auto-generated method stub
		return super.getDescriptionId();
	}
	
}
