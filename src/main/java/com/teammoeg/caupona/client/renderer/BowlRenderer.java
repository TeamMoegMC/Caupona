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
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.foods.BowlBlockEntity;
import com.teammoeg.caupona.client.util.FluidRenderHelper;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.Minecraft;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BowlRenderer implements BlockEntityRenderer<BowlBlockEntity, BowlRenderState> {
	/**
	 * @param rendererDispatcherIn
	 */
	public BowlRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	public void extractRenderState(BowlBlockEntity blockEntity, BowlRenderState state, float partialTicks,
			Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		BlockState bstate = blockEntity.getBlockState();
		state.type=0;
		state.fluid=null;
		if (bstate.getBlock() == CPBlocks.BOWL.get()) {
			state.type = 1;
		} else if (bstate.getBlock() == CPBlocks.LOAF_BOWL.get()) {
			state.type = 2;
		} else
			return;
		if (blockEntity.getInternal() == null || !(blockEntity.getInternal().getItem() instanceof StewItem))
			return;
		state.fluid = Utils.getFluidStack(blockEntity.getInternal());

	}

	@Override
	public void submit(BowlRenderState state, PoseStack poseStack, SubmitNodeCollector buffer,
			CameraRenderState camera) {

		poseStack.pushPose();
		if (state.fluid != null && !state.fluid.isEmpty() && state.fluid.getFluid() != null) {
			float y = state.type == 2 ? .3125f : .28125f;
			float lowerXZ = .28125f;
			float higherXZ = .4375f;
			poseStack.translate(0, y, 0);
			poseStack.mulPose(FluidRenderHelper.rotate90);

			FluidModel model = FluidRenderHelper.getFluidModel(state.fluid);
			int color = FluidRenderHelper.getFluidColor(model, state.fluid);
			TextureAtlasSprite sprite = model.stillMaterial().sprite();
			FluidRenderHelper.submitColoredTexturedRect(buffer, poseStack, sprite,lowerXZ,lowerXZ,higherXZ,higherXZ, color, state.lightCoords, OverlayTexture.NO_OVERLAY);
			

		}

		poseStack.popPose();
	}

	@Override
	public BowlRenderState createRenderState() {
		return new BowlRenderState();
	}

}