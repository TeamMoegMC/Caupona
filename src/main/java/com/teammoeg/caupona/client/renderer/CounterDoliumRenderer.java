/*
 * Copyright (c) 2024 TeamMoeg
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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.client.renderer;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.blocks.foods.BowlBlockEntity;
import com.teammoeg.caupona.client.util.GuiUtils;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class CounterDoliumRenderer implements BlockEntityRenderer<CounterDoliumBlockEntity,CounterDoliumRenderState> {

	/**
	 * @param rendererDispatcherIn  
	 */
	public CounterDoliumRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	@Override
	public void submit(CounterDoliumRenderState state, PoseStack poseStack, SubmitNodeCollector buffer,
			CameraRenderState camera) {
		
		poseStack.pushPose();
		if (state.fs != null && !state.fs.isEmpty() && state.fs.getFluid() != null) {
			float rr = (state.fs.getAmount() / 1250f) * 0.5f + 0.375f;
			poseStack.translate(0, rr, 0);
			poseStack.mulPose(GuiUtils.rotate90);

			FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet()
					.get(state.fs.getFluid().defaultFluidState());
			int color = model.fluidTintSource().colorAsStack(state.fs);
			TextureAtlasSprite sprite = model.stillMaterial().sprite();

			buffer.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (matrixStack, builder) -> {
				GuiUtils.drawTexturedColoredRect(builder, matrixStack, .125f, .125f, .75f, .75f,
						(color >> 16 & 255) , (color >> 8 & 255) , (color & 255), 255,
						sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), state.lightCoords,
						OverlayTexture.NO_OVERLAY);
			});

		}

		poseStack.popPose();
	}



	@Override
	public CounterDoliumRenderState createRenderState() {
		return new CounterDoliumRenderState();
	}

	public void extractRenderState(CounterDoliumBlockEntity blockEntity, CounterDoliumRenderState state, float partialTicks,
			Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		state.fs=null;
		if (blockEntity.tank.getResource(0).isEmpty())
			return;
		state.fs = blockEntity.tank.getResource(0).toStack(blockEntity.tank.getAmountAsInt(0));


	}
}