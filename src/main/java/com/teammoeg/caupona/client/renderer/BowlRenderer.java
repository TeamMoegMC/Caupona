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

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

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
		state.sprite=null;
		if (bstate.getBlock() == CPBlocks.BOWL.get()) {
			state.type = 1;
		} else if (bstate.getBlock() == CPBlocks.LOAF_BOWL.getSecond().get()) {
			state.type = 2;
		} else
			return;
		if (blockEntity.getInternal().getResource(0).isEmpty())
			return;
		FluidStack fluid = FluidUtil.getFirstStackContained(blockEntity.getInternal().getResource(0).toStack());
		FluidModel model = FluidRenderHelper.getFluidModel(fluid);
		state.color = FluidRenderHelper.getFluidColor(model, fluid);
		state.sprite = model.stillMaterial().sprite();

	}

	@Override
	public void submit(BowlRenderState state, PoseStack poseStack, SubmitNodeCollector buffer,
			CameraRenderState camera) {

		if (state.sprite != null) {
			float y = state.type == 2 ? .3125f : .28125f;
			float lowerXZ = .28125f;
			float higherXZ = .4375f;

			
			FluidRenderHelper.submitColoredTexturedRect(buffer, poseStack, state.sprite,lowerXZ,y,lowerXZ,higherXZ, 0, higherXZ, state.color, state.lightCoords, OverlayTexture.NO_OVERLAY);
			

		}
	}

	@Override
	public BowlRenderState createRenderState() {
		return new BowlRenderState();
	}

}