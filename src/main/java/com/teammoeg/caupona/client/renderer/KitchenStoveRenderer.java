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
import com.teammoeg.caupona.blocks.stove.KitchenStoveBlockEntity;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.client.util.RenderHelper;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

public class KitchenStoveRenderer implements BlockEntityRenderer<KitchenStoveBlockEntity,KitchenStoveRenderState> {
	private final QuadInstance quadInstance = new QuadInstance();
	/**
	 * @param rendererDispatcherIn  
	 */
	public KitchenStoveRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}
	@Override
	public KitchenStoveRenderState createRenderState() {
		return new KitchenStoveRenderState();
	}
	@Override
	public void submit(KitchenStoveRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
		poseStack.rotateAround(RenderHelper.getRotation(state.dir), .5f, .5f, .5f);
		if(state.stock!=null) {
			submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose,buffer)->{
				for(BakedQuad quad:state.stock.get().getAll()) {
					buffer.putBakedQuad(pose, quad, quadInstance);
				}
			});
		}
		if(state.ash!=null) {
			submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (pose,buffer)->{
				for(BakedQuad quad:state.ash.get().getAll()) {
					buffer.putBakedQuad(pose, quad, quadInstance);
				}
			});
		}
	}
	@Override
	public void extractRenderState(KitchenStoveBlockEntity blockEntity, KitchenStoveRenderState state, float partialTicks, Vec3 cameraPosition, @Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		state.stock=DynamicBlockModelReference.getModel(blockEntity.inventory_fuel.modelLayer());
		
		if(blockEntity.getBlockState().getValue(BlockStateProperties.LIT))
			state.ash=DynamicBlockModelReference.getModel(blockEntity.current.hot_ash());
		else
			state.ash=DynamicBlockModelReference.getModel(blockEntity.current.cold_ash());

		state.dir=blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).getOpposite();
	}

}