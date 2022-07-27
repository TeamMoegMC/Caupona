/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.BowlTileEntity;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumTileEntity;
import com.teammoeg.caupona.blocks.pot.StewPotTileEntity;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;

public class CounterDoliumRenderer implements BlockEntityRenderer<CounterDoliumTileEntity> {

	public CounterDoliumRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	private static Vector3f clr(int fromcol, int tocol, float proc) {
		float fcolr = (fromcol >> 16 & 255) / 255.0f, fcolg = (fromcol >> 8 & 255) / 255.0f,
				fcolb = (fromcol & 255) / 255.0f, tcolr = (tocol >> 16 & 255) / 255.0f,
				tcolg = (tocol >> 8 & 255) / 255.0f, tcolb = (tocol & 255) / 255.0f;
		return new Vector3f(fcolr + (tcolr - fcolr) * proc, fcolg + (tcolg - fcolg) * proc,
				fcolb + (tcolb - fcolb) * proc);
	}

	private static Vector3f clr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(CounterDoliumTileEntity te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!te.getLevel().hasChunkAt(te.getBlockPos()))
			return;
		BlockState state = te.getBlockState();
		if (!CPBlocks.dolium.contains(state.getBlock()))
			return;
		
		if(te.tank.isEmpty())return;
		FluidStack fs = te.tank.getFluid();
		matrixStack.pushPose();
		if (fs != null && !fs.isEmpty() && fs.getFluid() != null) {
			float rr=(fs.getAmount()/1250f)*0.5f+0.375f;
			matrixStack.translate(0, rr, 0);
			matrixStack.mulPose(new Quaternion(90, 0, 0, true));
			
			VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
			TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager()
					.getAtlas(InventoryMenu.BLOCK_ATLAS)
					.getSprite(fs.getFluid().getAttributes().getStillTexture(fs));
			int col = fs.getFluid().getAttributes().getColor(fs);
			int iW = sprite.getWidth();
			int iH = sprite.getHeight();
			if (iW > 0 && iH > 0) {
				Vector3f clr;
				float alp = 1f;
				clr = clr(col);
				RenderUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f, clr.x(),
						clr.y(), clr.z(), alp, sprite.getU0(), sprite.getU1(), sprite.getV0(),
						sprite.getV1(), combinedLightIn, combinedOverlayIn);

			}
		}
		
		matrixStack.popPose();
	}

}