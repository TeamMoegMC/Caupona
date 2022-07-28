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
import com.mojang.math.Vector3f;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.blocks.pan.PanTile;
import com.teammoeg.caupona.items.DishItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

public class PanRenderer implements BlockEntityRenderer<PanTile> {

	public PanRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
	}


	private static Vector3f clr(int col) {
		return new Vector3f((col >> 16 & 255) / 255.0f, (col >> 8 & 255) / 255.0f, (col & 255) / 255.0f);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(PanTile te, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer,
			int combinedLightIn, int combinedOverlayIn) {
		if (!te.getLevel().hasChunkAt(te.getBlockPos()))
			return;
		BlockState state = te.getBlockState();
		Block b=state.getBlock();
		
		Item torender=null;
		if(!te.sout.isEmpty()) {
			torender=te.sout.getItem();
		}else if(!(te.preout==Items.AIR)) {
			torender=te.preout;
		}else { 
			ItemStack is=te.inv.getStackInSlot(10);
			if(!is.isEmpty())
				torender=is.getItem();
		}
		if(!(torender instanceof DishItem))return;
		BlockState bs=((DishItem)torender).bl.defaultBlockState();
		if (b==CPBlocks.STONE_PAN) {
			bs=bs.setValue(DishBlock.PAN,1);
		}else
			bs=bs.setValue(DishBlock.PAN,2);
		BlockRenderDispatcher rd=Minecraft.getInstance().getBlockRenderer();
		IModelData imd=rd.getBlockModel(bs).getModelData(te.getLevel(),te.getBlockPos(), bs,ModelDataManager.getModelData(te.getLevel(),te.getBlockPos()));
		rd.renderSingleBlock(bs,matrixStack, buffer,combinedLightIn,combinedOverlayIn,imd);

	}

}