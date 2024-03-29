package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class MosaicBlock extends CPHorizontalBlock{
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
		BlockState bs=super.getStateForPlacement(pContext);
		CompoundTag tag=pContext.getItemInHand().getTagElement("caupona:mosaic");
		if(tag==null)
			return bs;
		MosaicPattern pattern=MosaicPattern.valueOf(tag.getString("pattern"));
		MosaicMaterial m1=MosaicMaterial.valueOf(tag.getString("mat1"));
		MosaicMaterial m2=MosaicMaterial.valueOf(tag.getString("mat2"));
		return bs.setValue(MosaicBlock.MATERIAL_1, m1).setValue(MosaicBlock.MATERIAL_2, m2).setValue(MosaicBlock.PATTERN, pattern);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState state,
			net.minecraft.world.level.storage.loot.LootParams.Builder p_287596_) {
		ItemStack is=new ItemStack(CPBlocks.MOSAIC.get());
		MosaicItem.setMosaic(is,state.getValue(MATERIAL_1),state.getValue(MATERIAL_2),state.getValue(PATTERN));
		List<ItemStack> ret=new ArrayList<>();
		ret.add(is);
		return ret;
	}
	@Override
	public ItemStack getCloneItemStack(BlockGetter pLevel, BlockPos pPos, BlockState state) {
		ItemStack is=new ItemStack(CPBlocks.MOSAIC.get());
		MosaicItem.setMosaic(is,state.getValue(MATERIAL_1),state.getValue(MATERIAL_2),state.getValue(PATTERN));
		return is;
	}

}
