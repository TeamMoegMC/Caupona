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
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlockEntity;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class CounterDoliumRenderer implements BlockEntityRenderer<CounterDoliumBlockEntity,CounterDoliumRenderState> {

	/**
	 * @param rendererDispatcherIn  
	 */
	public CounterDoliumRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}

	@Override
	public void submit(CounterDoliumRenderState state, PoseStack poseStack, SubmitNodeCollector buffer,
			CameraRenderState camera) {
		if (state.sprite != null) {
			poseStack.pushPose();
			poseStack.translate(0, state.level, 0);
			poseStack.mulPose(FluidRenderHelper.rotate90);
			FluidRenderHelper.submitColoredTexturedRect(buffer, poseStack, state.sprite,.125f,.125f,.75f,.75f, state.color, state.lightCoords, OverlayTexture.NO_OVERLAY);
			poseStack.popPose();
		}
	}



	@Override
	public CounterDoliumRenderState createRenderState() {
		return new CounterDoliumRenderState();
	}

	public void extractRenderState(CounterDoliumBlockEntity blockEntity, CounterDoliumRenderState state, float partialTicks,
			Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.sprite=null;
		
		FluidStack fs = FluidUtil.getStack(blockEntity.tank, 0);
		if (fs.isEmpty())
			return;
		FluidModel model = FluidRenderHelper.getFluidModel(fs);
		state.color = FluidRenderHelper.getFluidColor(model, fs);
		state.sprite = model.stillMaterial().sprite();
		state.level = (fs.getAmount() / 1250f) * 0.5f + 0.375f;
	}
}