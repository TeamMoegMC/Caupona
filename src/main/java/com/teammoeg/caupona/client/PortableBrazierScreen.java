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
import com.teammoeg.caupona.container.PortableBrazierContainer;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class PortableBrazierScreen extends AbstractContainerScreen<PortableBrazierContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/portable_brazier.png");
	PortableBrazierContainer container;

	public PortableBrazierScreen(PortableBrazierContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		container = screenContainer;
	}

	protected void renderLabels(PoseStack matrixStack, int x, int y) {
		this.font.draw(matrixStack, this.title, this.titleLabelX - 2, this.titleLabelY, 0xEEEEEE);
		this.font.draw(matrixStack, this.playerInventoryTitle, this.inventoryLabelX - 2, this.inventoryLabelY - 3,
				4210752);
	}

	@Override
	protected void renderBg(PoseStack transform, float partial, int x, int y) {
		this.renderBackground(transform);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);

		this.blit(transform, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (container.processMax > 0) {
			int h = (int) (29 * (container.process / (float) container.processMax));
			this.blit(transform, leftPos + 116, topPos + 36 + h, 176, 1 + h, 16, 29 - h);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		super.renderTooltip(pPoseStack, pMouseX, pMouseY);
	}
}
