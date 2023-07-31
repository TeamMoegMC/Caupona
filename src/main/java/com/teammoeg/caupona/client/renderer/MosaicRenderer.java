package com.teammoeg.caupona.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicMaterial;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class MosaicRenderer extends BlockEntityWithoutLevelRenderer {
	//ResourceLocation name=new ResourceLocation("block/block");
	public MosaicRenderer() {
		super(null,null);
	}

	@Override
	public void renderByItem(ItemStack is, ItemDisplayContext ctx, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		CompoundTag tag=is.getOrCreateTagElement("caupona:mosaic");
		if(tag.isEmpty())
			return;
		MosaicPattern pattern=MosaicPattern.valueOf(tag.getString("pattern"));
		MosaicMaterial m1=MosaicMaterial.valueOf(tag.getString("mat1"));
		MosaicMaterial m2=MosaicMaterial.valueOf(tag.getString("mat2"));
		BlockState bs=CPBlocks.MOSAIC.get().defaultBlockState();
		bs=bs.setValue(MosaicBlock.MATERIAL_1, m1).setValue(MosaicBlock.MATERIAL_2, m2).setValue(MosaicBlock.PATTERN, pattern);
		BlockRenderDispatcher rd = Minecraft.getInstance().getBlockRenderer();
		/*Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager().getModel(name)
		.applyTransform(ctx, matrixStack, ctx==ItemDisplayContext.FIRST_PERSON_LEFT_HAND);*/
		rd.renderSingleBlock(bs, matrixStack, buffer, combinedLightIn, combinedOverlayIn,ModelData.builder().build(),RenderType.cutout());
	}

}
