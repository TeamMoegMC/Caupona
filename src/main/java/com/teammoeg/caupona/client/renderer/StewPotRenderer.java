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
import com.teammoeg.caupona.blocks.pot.StewPotBlockEntity;
import com.teammoeg.caupona.client.util.FluidRenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;

public class StewPotRenderer implements BlockEntityRenderer<StewPotBlockEntity,StewPotRenderState> {

	/**
	 * @param rendererDispatcherIn
	 */
	public StewPotRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}




	@Override
	public StewPotRenderState createRenderState() {
		return new StewPotRenderState();
	}

	@Override
	public void extractRenderState(StewPotBlockEntity blockEntity, StewPotRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.inModel=null;
		state.outModel=null;
		FluidStack input=FluidUtil.getStack(blockEntity.getTank(), 0);
		FluidStack output=blockEntity.output;
		if (!input.isEmpty()) {
			FluidModel inModel=FluidRenderHelper.getFluidModel(input);
			state.inColor = FluidRenderHelper.getFluidColor(inModel, input);
			state.inModel=inModel.stillMaterial().sprite();
			state.level = input.getAmount();
			if (output !=null)// just animate fluid modification
				state.level += (output.getAmount()-input.getAmount()) * ( blockEntity.process * 1f / blockEntity.processMax);
			state.level=Math.min(1, state.level / 1250) * .5f + .1875f;
			if (output != null&&!output.isEmpty() && blockEntity.processMax > 0) {
				FluidModel outModel=FluidRenderHelper.getFluidModel(output);
				float proc = blockEntity.process * 1f / blockEntity.processMax;
				state.outColor = ARGB.srgbLerp(proc, state.inColor, FluidRenderHelper.getFluidColor(outModel, output));
				state.outColor = ARGB.color(ARGB.alphaFloat(state.outColor)*(proc), state.outColor);
				state.inColor = ARGB.color(ARGB.alphaFloat(state.inColor)*(1 - proc), state.inColor);
			}
		}
	}

	@Override
	public void submit(StewPotRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		if (state.inModel != null) {
			poseStack.translate(0, state.level, 0);
			poseStack.mulPose(FluidRenderHelper.rotate90);
			if (state.outModel != null) {
				FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, state.outModel,
					.125f, .125f, .75f, .75f, 
					state.outColor, state.lightCoords, OverlayTexture.NO_OVERLAY);
			}
			FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack,  state.inModel,
				.125f, .125f, .75f, .75f, 
				state.inColor, state.lightCoords, OverlayTexture.NO_OVERLAY);

		}

		poseStack.popPose();
	}

}