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
import net.neoforged.neoforge.transfer.fluid.FluidResource;

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
		state.input=null;
		state.output=null;
		state.process=blockEntity.process;
		state.processMax=blockEntity.processMax;
		FluidResource cur=blockEntity.getTank().getResource(0);
		if(!cur.isEmpty())
			state.input=cur.toStack(blockEntity.getTank().getAmountAsInt(0));
		state.output=blockEntity.output;
		
	}

	@Override
	public void submit(StewPotRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.pushPose();
		if (state.input!= null && !state.input.isEmpty() && state.input.getFluid() != null) {
			float rr = state.input.getAmount();
			if (state.output !=null)// just animate fluid modification
				rr += (state.output.getAmount()-state.input.getAmount()) * ( state.process * 1f / state.processMax);
			float yy = Math.min(1, rr / 1250) * .5f + .1875f;
			poseStack.translate(0, yy, 0);
			poseStack.mulPose(FluidRenderHelper.rotate90);

			FluidModel inModel=FluidRenderHelper.getFluidModel(state.input);
			int inColor = FluidRenderHelper.getFluidColor(inModel, state.input);
			float alp = 1f;
			if (state.output != null&&!state.output.isEmpty() && state.processMax > 0) {
				FluidModel outModel=FluidRenderHelper.getFluidModel(state.output);
				float proc = state.process * 1f / state.processMax;
				int color =ARGB.srgbLerp(proc, inColor, FluidRenderHelper.getFluidColor(outModel, state.output));

				alp = 1 - proc;
			
				FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, outModel.stillMaterial().sprite(), .125f, .125f, .75f, .75f, ARGB.color(proc, color), state.lightCoords, OverlayTexture.NO_OVERLAY);


			}
			FluidRenderHelper.submitColoredTexturedRect(submitNodeCollector, poseStack, inModel.stillMaterial().sprite(),
				.125f, .125f, .75f, .75f,
				ARGB.color(alp, inColor), state.lightCoords, OverlayTexture.NO_OVERLAY);


		}

		poseStack.popPose();
	}

}