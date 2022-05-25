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

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.KitchenStoveTileEntity;
import com.teammoeg.caupona.container.KitchenStoveContainer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

public class KitchenStoveScreen extends AbstractContainerScreen<KitchenStoveContainer> {
	KitchenStoveTileEntity te;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/kitchen_stove.png");
	public KitchenStoveScreen(KitchenStoveContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		te=screenContainer.tile;
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
		this.renderBackground(matrixStack);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

		this.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (te.processMax > 0 && te.process > 0) {
			int h = (int) (20 * (1-te.process / (float) te.processMax));
			this.blit(matrixStack, leftPos + 73, topPos + 7 + h, 176,h, 31,20 - h);
		}
	}

}
