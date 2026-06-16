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

import java.util.ArrayList;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.pan.PanBlockEntity;
import com.teammoeg.caupona.blocks.pan.PanContainer;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PanScreen extends AbstractContainerScreen<PanContainer> {
	static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(CPMain.MODID, "textures/gui/frying_pan.png");

	PanBlockEntity blockEntity;

	public PanScreen(PanContainer container, Inventory inv, Component titleIn) {
		super(container, inv, titleIn);
		this.titleLabelY = 4;
		this.titleLabelX = 7;
		this.inventoryLabelY = this.imageHeight - 92;
		this.inventoryLabelX = 4;
		blockEntity = container.getBlock();
	}

	public static MutableComponent start = Utils.translate("gui." + CPMain.MODID + ".stewpot.canstart");
	public static MutableComponent started = Utils.translate("gui." + CPMain.MODID + ".stewpot.started");
	public static MutableComponent nostart = Utils.translate("gui." + CPMain.MODID + ".stewpot.cantstart");
	public static MutableComponent nors = Utils.translate("gui." + CPMain.MODID + ".stewpot.noredstone");
	public static MutableComponent rs = Utils.translate("gui." + CPMain.MODID + ".stewpot.redstone");
	private ArrayList<Component> tooltip = new ArrayList<>(2);
	ImageButton btn1;
	ImageButton btn2;

	@Override
	public void init() {
		super.init();
		this.clearWidgets();
		this.addRenderableWidget(btn1 = new ImageButton(Button.builder(start, _ -> {
			if (btn1.state == 0)
				menu.sendMessage((short) 0, 0);
		}).pos(leftPos + 7, topPos + 48).size(20, 12), 176, 83, 256, 256, TEXTURE,
				() -> (btn1.state == 0 ? Tooltip.create(start) : Tooltip.create(started))));
		this.addRenderableWidget(btn2 = new ImageButton(Button.builder(rs, _ -> {
			menu.sendMessage((short) 1, btn2.state);
		}).pos(leftPos + 7, topPos + 61).size(20, 20), 176, 107, 256, 256, TEXTURE,
				() -> (btn2.state == 2 ? Tooltip.create(rs) : Tooltip.create(nors))));

	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor transform, int mouseX, int mouseY, float partial) {
		tooltip.clear();
		btn1.state = blockEntity.handler.getProcessMax() > 0 ? 1 : 0;
		btn2.state = blockEntity.rsstate ? 1 : 2;
		super.extractRenderState(transform, mouseX, mouseY, partial);
		if (!tooltip.isEmpty()) {
			transform.setComponentTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
		}

	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
		graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

		Component name = this.playerInventoryTitle;
		int w = this.font.width(name.getString());
		graphics.text(this.font, name, this.imageWidth - w - this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);

		graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight,256,256);
		if (blockEntity.handler.getProcessMax() > 0 && blockEntity.handler.getProcess() > 0) {
			int h = (int) (29 * (blockEntity.handler.getProcess() / (float) blockEntity.handler.getProcessMax()));
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 39, topPos + 16 + h, 176, 54 + h, 16, 29 - h,256,256);
		}
		if (blockEntity.handler.getProcessMax() > 0) {
			graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 61, topPos + 12, 176, 0, 54, 54,256,256);
		}
	}

	public boolean isMouseIn(int mouseX, int mouseY, int x, int y, int w, int h) {
		return mouseX >= leftPos + x && mouseY >= topPos + y && mouseX < leftPos + x + w && mouseY < topPos + y + h;
	}

}
