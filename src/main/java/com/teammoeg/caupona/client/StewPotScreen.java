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

import java.util.ArrayList;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.pot.StewPotContainer;
import com.teammoeg.caupona.blocks.pot.StewPotTileEntity;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.SoupInfo;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StewPotScreen extends AbstractContainerScreen<StewPotContainer> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(Main.MODID,
			"textures/gui/stew_pot.png");

	public static class ImageButton extends Button {
		int xTexStart;
		int yTexStart;
		private final int textureWidth;
		private final int textureHeight;
		int state;

		public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn,
				Button.OnPress onPressIn) {
			this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, NO_TOOLTIP, onPressIn);
		}

		public ImageButton(int xIn, int yIn, int widthIn, int heightIn, int xTexStartIn, int yTexStartIn,
				Button.OnTooltip tt, Button.OnPress onPressIn) {
			this(xIn, yIn, widthIn, heightIn, xTexStartIn, yTexStartIn, 256, 256, onPressIn, tt,
					TextComponent.EMPTY);
		}

		public ImageButton(int x, int y, int width, int height, int xTexStart, int yTexStart, int textureWidth,
				int textureHeight, Button.OnPress onPress, Component title) {
			this(x, y, width, height, xTexStart, yTexStart, textureWidth, textureHeight, onPress, NO_TOOLTIP, title);
		}

		public ImageButton(int p_i244513_1_, int p_i244513_2_, int p_i244513_3_, int p_i244513_4_, int p_i244513_5_,
				int p_i244513_6_, int p_i244513_9_, int p_i244513_10_, Button.OnPress p_i244513_11_,
				Button.OnTooltip p_i244513_12_, Component p_i244513_13_) {
			super(p_i244513_1_, p_i244513_2_, p_i244513_3_, p_i244513_4_, p_i244513_13_, p_i244513_11_, p_i244513_12_);
			this.textureWidth = p_i244513_9_;
			this.textureHeight = p_i244513_10_;
			this.xTexStart = p_i244513_5_;
			this.yTexStart = p_i244513_6_;
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
	        RenderSystem.setShaderTexture(0, TEXTURE);
			RenderSystem.enableDepthTest();
			blit(matrixStack, this.x, this.y, this.xTexStart + i, this.yTexStart + j, this.width, this.height,
					this.textureWidth, this.textureHeight);
			if (this.isHoveredOrFocused()) {
				this.renderToolTip(matrixStack, mouseX, mouseY);
			}

		}
	}

	StewPotTileEntity te;

	public StewPotScreen(StewPotContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		this.titleLabelY = 4;
		this.titleLabelX = 7;
		this.inventoryLabelY = this.imageHeight - 92;
		this.inventoryLabelX = 4;
		te = container.getTile();
	}

	public static TranslatableComponent start = new TranslatableComponent(
			"gui." + Main.MODID + ".stewpot.canstart");
	public static TranslatableComponent started = new TranslatableComponent(
			"gui." + Main.MODID + ".stewpot.started");
	public static TranslatableComponent nostart = new TranslatableComponent(
			"gui." + Main.MODID + ".stewpot.cantstart");
	public static TranslatableComponent nors = new TranslatableComponent(
			"gui." + Main.MODID + ".stewpot.noredstone");
	public static TranslatableComponent rs = new TranslatableComponent("gui." + Main.MODID + ".stewpot.redstone");
	private ArrayList<Component> tooltip = new ArrayList<>(2);
	ImageButton btn1;
	ImageButton btn2;

	@Override
	public void init() {
		super.init();
		
		this.clearWidgets();
		this.addRenderableWidget(btn1 = new ImageButton(leftPos + 7, topPos + 48, 20, 12, 176, 83, (b, s, x, y) -> {
			if (btn1.state == 0)
				tooltip.add(start);
			else
				tooltip.add(started);
		}, btn -> {
			if (btn1.state == 0)
				te.sendMessage((short) 0, 0);

		}));
		this.addRenderableWidget(btn2 = new ImageButton(leftPos + 7, topPos + 61, 20, 20, 176, 107, (b, s, x, y) -> {
			if (btn2.state == 1)
				tooltip.add(nors);
			else
				tooltip.add(rs);
		}, btn -> {
			te.sendMessage((short) 1, btn2.state);
		}));

	}

	@Override
	public void render(PoseStack transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		btn1.state = te.proctype > 0 ? 1 : 0;
		btn2.state = te.rsstate ? 1 : 2;
		super.render(transform, mouseX, mouseY, partial);
		if (te.proctype < 2&&!te.getTank().isEmpty()) {
			if (isMouseIn(mouseX, mouseY, 105, 20, 16, 46)) {
				tooltip.add(te.getTank().getFluid().getDisplayName());
				SoupInfo si=SoupFluid.getInfo(te.getTank().getFluid());
				FloatemStack fs=si.stacks.stream().max((t1,t2)->t1.getCount()>t2.getCount()?1:(t1.getCount()==t2.getCount()?0:-1)).orElse(null);
				if(fs!=null)
					tooltip.add(new TranslatableComponent("tooltip.caupona.main_ingredient",fs.getStack().getDisplayName()));
				StewItem.addPotionTooltip(si.effects,tooltip,1);
			}
			RenderUtils.handleGuiTank(transform, te.getTank(), leftPos + 105, topPos + 20, 16, 46);
		}
		if (!tooltip.isEmpty())
			super.renderComponentTooltip(transform, tooltip, mouseX, mouseY);
		else
			super.renderTooltip(transform, mouseX, mouseY);

	}

	protected void renderLabels(PoseStack matrixStack, int x, int y) {
		this.font.draw(matrixStack, this.title, this.titleLabelX, this.titleLabelY, 0xffda856b);
		
		Component name = this.playerInventoryTitle;
		int w = this.font.width(name.getString());
		this.font.draw(matrixStack, name, this.imageWidth - w - this.inventoryLabelX, this.inventoryLabelY,
				0xffda856b);
	}

	@Override
	protected void renderBg(PoseStack transform, float partial, int x, int y) {
		this.renderBackground(transform);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

		this.blit(transform, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		if (te.processMax > 0 && te.process > 0) {
			int h = (int) (29 * (te.process / (float) te.processMax));
			this.blit(transform, leftPos + 9, topPos + 17 + h, 176, 54 + h, 16, 29 - h);
		}
		if (te.proctype > 1) {
			if (te.proctype == 2)
				this.blit(transform, leftPos + 44, topPos + 16, 176, 0, 54, 54);
			this.blit(transform, leftPos + 102, topPos + 17, 230, 0, 21, 51);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

}
