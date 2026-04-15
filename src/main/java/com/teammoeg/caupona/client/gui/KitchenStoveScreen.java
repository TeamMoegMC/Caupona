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

package com.teammoeg.caupona.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.stove.KitchenStoveBlockEntity;
import com.teammoeg.caupona.blocks.stove.KitchenStoveContainer;
import com.teammoeg.caupona.util.FuelType;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class KitchenStoveScreen extends AbstractContainerScreen<KitchenStoveContainer> {
	KitchenStoveBlockEntity blockEntity;
	private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CPMain.MODID, "textures/gui/kitchen_stove.png");

	public KitchenStoveScreen(KitchenStoveContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		blockEntity = screenContainer.getBlock();
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
		graphics.text(this.font, this.title, this.titleLabelX - 2, this.titleLabelY, 0xEEEEEE, false);
		graphics.text(this.font, this.playerInventoryTitle, this.inventoryLabelX - 2, this.inventoryLabelY - 2,4210752, false);
	
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);
		
		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);
		if (blockEntity.processMax > 0 && blockEntity.process > 0) {
			int h = (int) (26 * (1 - blockEntity.process / (float) blockEntity.processMax));
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 61, topPos + h, 176, h, 54, 26 - h, 256, 256);
			if(blockEntity.current==FuelType.CHARCOAL) {
				graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 61, topPos + 13, 176, 42, 54, 16, 256, 256);
			}else if(blockEntity.current==FuelType.CHARCOAL) {
				graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 61, topPos + 13, 176, 58, 54, 16, 256, 256);
			}else {
				graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 61, topPos + 13, 176, 26, 54, 16, 256, 256);
			}
		}
	}

	



}
