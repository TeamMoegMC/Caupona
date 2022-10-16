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

package com.teammoeg.caupona.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ImageButton extends Button {
	int xTexStart;
	int yTexStart;
	private final int textureWidth;
	private final int textureHeight;
	int state;
	ResourceLocation texture;

	public ImageButton(ResourceLocation texture, int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn,
			int yTexStartIn, Button.OnPress onPressIn) {
		this(texture, xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, NO_TOOLTIP, onPressIn);
	}

	public ImageButton(ResourceLocation texture, int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn,
			int yTexStartIn, Button.OnTooltip tt, Button.OnPress onPressIn) {
		this(texture, xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, 256, 256, onPressIn, tt,
				Component.empty());
	}

	public ImageButton(ResourceLocation texture, int x, int y, int width, int height, int xTexStart, int yTexStart,
			int textureWidth, int textureHeight, Button.OnPress onPress, Component title) {
		this(texture, x, y, width, height, xTexStart, yTexStart, textureWidth, textureHeight, onPress, NO_TOOLTIP,
				title);
	}

	public ImageButton(ResourceLocation texture, int p_i244513_1_, int p_i244513_2_, int p_i244513_3_, int p_i244513_4_,
			int p_i244513_5_, int p_i244513_6_, int p_i244513_9_, int p_i244513_10_, Button.OnPress p_i244513_11_,
			Button.OnTooltip p_i244513_12_, Component p_i244513_13_) {
		super(p_i244513_1_, p_i244513_2_, p_i244513_3_, p_i244513_4_, p_i244513_13_, p_i244513_11_, p_i244513_12_);
		this.textureWidth = p_i244513_9_;
		this.textureHeight = p_i244513_10_;
		this.xTexStart = p_i244513_5_;
		this.yTexStart = p_i244513_6_;
		this.texture = texture;
	}

	public void setPosition(int xIn, int yIn) {
		this.x = xIn;
		this.y = yIn;
	}

	public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		int i = 0, j = state * this.height;

		if (this.isHoveredOrFocused()) {
			i += this.width;
		}
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.enableDepthTest();
		blit(matrixStack, this.x, this.y, this.xTexStart + i, this.yTexStart + j, this.width, this.height,
				this.textureWidth, this.textureHeight);
		if (this.isHoveredOrFocused()) {
			this.renderToolTip(matrixStack, mouseX, mouseY);
		}

	}
}