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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.stove.KitchenStoveBlockEntity;
import com.teammoeg.caupona.blocks.stove.KitchenStoveContainer;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class KitchenStoveScreen extends AbstractContainerScreen<KitchenStoveContainer> {
	KitchenStoveBlockEntity blockEntity;
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/kitchen_stove.png");

	public KitchenStoveScreen(KitchenStoveContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		blockEntity = screenContainer.tile;
	}

	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

		this.renderTooltip(pPoseStack, pMouseX, pMouseY);

	}

	protected void renderLabels(PoseStack matrixStack, int x, int y) {
		this.font.draw(matrixStack, this.title, this.titleLabelX - 2, this.titleLabelY, 0xEEEEEE);
		this.font.draw(matrixStack, this.playerInventoryTitle, this.inventoryLabelX - 2, this.inventoryLabelY - 2,
				4210752);
	}

	@Override
	protected void renderBg(PoseStack matrixStack, float partialTicks, int x, int y) {
		this.renderBackground(matrixStack);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, TEXTURE);

		GuiComponent.blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (blockEntity.processMax > 0 && blockEntity.process > 0) {
			int h = (int) (26 * (1 - blockEntity.process / (float) blockEntity.processMax));
			GuiComponent.blit(matrixStack, leftPos + 61, topPos + h, 176, h, 54, 26 - h);
			switch (blockEntity.last) {
			case CHARCOAL:
				GuiComponent.blit(matrixStack, leftPos + 61, topPos + 13, 176, 42, 54, 16);
				break;
			case WOODS:
				GuiComponent.blit(matrixStack, leftPos + 61, topPos + 13, 176, 58, 54, 16);
				break;
			default:
				GuiComponent.blit(matrixStack, leftPos + 61, topPos + 13, 176, 26, 54, 16);
				break;
			}
		}
	}

}
