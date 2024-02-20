package com.teammoeg.caupona.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicMaterial;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicPattern;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.ModelUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

public class MosaicRenderer extends BlockEntityWithoutLevelRenderer {
	//ResourceLocation name=new ResourceLocation("block/block");
	public MosaicRenderer() {
		super(null,null);
	}
	private static DynamicBlockModelReference model=ModelUtils.getModel("item_block");
	@Override
	public void renderByItem(ItemStack is, ItemDisplayContext ctx, PoseStack matrixStack,
			MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
		CompoundTag tag=is.getTagElement("caupona:mosaic");
		if(tag==null)
			return;
		MosaicPattern pattern=MosaicPattern.valueOf(tag.getString("pattern"));
		MosaicMaterial m1=MosaicMaterial.valueOf(tag.getString("mat1"));
		MosaicMaterial m2=MosaicMaterial.valueOf(tag.getString("mat2"));
		
		BlockState bs=CPBlocks.MOSAIC.get().defaultBlockState();
		bs=bs.setValue(MosaicBlock.MATERIAL_1, m1).setValue(MosaicBlock.MATERIAL_2, m2).setValue(MosaicBlock.PATTERN, pattern);
		BlockRenderDispatcher rd = Minecraft.getInstance().getBlockRenderer();
		BakedModel bm=rd.getBlockModel(bs);
		ItemRenderer ir=Minecraft.getInstance().getItemRenderer();
		VertexConsumer pBuffer = buffer.getBuffer(RenderType.cutout());
		RandomSource randomsource = RandomSource.create();
		for (Direction direction : Direction.values()) {
			randomsource.setSeed(42L);
			ir.renderQuadList(matrixStack, pBuffer, bm.getQuads(bs, direction, randomsource), is, combinedLightIn,
					combinedOverlayIn);
		}

		randomsource.setSeed(42L);
		ir.renderQuadList(matrixStack, pBuffer, bm.getQuads(bs, (Direction) null, randomsource), is, combinedLightIn,
				combinedOverlayIn);
	}

}
