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
import com.mojang.blaze3d.vertex.QuadInstance;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.client.renderer.PanRenderState.LayerType;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class PanRenderer implements BlockEntityRenderer<PanBlockEntity,PanRenderState> {
	private final QuadInstance quadInstance = new QuadInstance();
	/**
	 * @param rendererDispatcherIn  
	 */
	public PanRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	@Override
	public PanRenderState createRenderState() {
		return new PanRenderState();
	}
	@Override
	public void submit(PanRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		
		if(state.model==null)
			return;
		DynamicBlockModelReference model=DynamicBlockModelReference.getModel(state.model);
		submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose,buffer)->{
			for(BakedQuad quad:model.get().getAll()) {
				buffer.putBakedQuad(pose, quad, quadInstance);
			}
		});
	}
	@Override
	public void extractRenderState(PanBlockEntity blockEntity, PanRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		BlockState bstate = blockEntity.getBlockState();
		Block b = bstate.getBlock();
		if((b == CPBlocks.STONE_PAN.get()))
			state.layer=LayerType.PLATE;
		else
			state.layer=LayerType.PAN;
		state.model=null;
		if(blockEntity.model!=null)
			state.model=blockEntity.model.withSuffix(state.layer.getPathSuffix());
	}

}