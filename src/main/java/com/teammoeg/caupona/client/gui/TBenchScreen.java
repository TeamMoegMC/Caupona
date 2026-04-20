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

import java.util.List;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.decoration.mosaic.TBenchMenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay.ItemStackSlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class TBenchScreen extends AbstractContainerScreen<TBenchMenu> {
   private static final Identifier BG_LOCATION = Identifier.fromNamespaceAndPath(CPMain.MODID,"textures/gui/tessellation_workbench.png");
   private static final int RECIPES_COLUMNS = 4;
   private static final int RECIPES_ROWS = 3;
   private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
   private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
   private static final int RECIPES_X = 52;
   private static final int RECIPES_Y = 14;
   private int startIndex;
   private boolean displayRecipes;

   public TBenchScreen(TBenchMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
      super(pMenu, pPlayerInventory, pTitle);
      --this.titleLabelY;
   }

	@Override
	public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractBackground(graphics, mouseX, mouseY, a);

      int i = this.leftPos;
      int j = this.topPos;
      graphics.blit(RenderPipelines.GUI_TEXTURED,BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight,256,256);
      int l = this.leftPos + RECIPES_X;
      int i1 = this.topPos + RECIPES_Y;
      this.extractButtons(graphics, mouseX, mouseY, l, i1);
      this.renderRecipes(graphics, l, i1);
   }
   @Override
   protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
       super.extractTooltip(graphics, mouseX, mouseY);
       if (this.displayRecipes) {
           int edgeLeft = this.leftPos + 52;
           int edgeTop = this.topPos + 14;
           int endIndex = this.startIndex + 12;
           List<ItemStack> visibleRecipes = this.menu.getRecipes();

           for (int index = this.startIndex; index < endIndex && index < visibleRecipes.size(); index++) {
               int posIndex = index - this.startIndex;
               int itemLeft = edgeLeft + posIndex % 4 * 16;
               int itemRight = edgeTop + posIndex / 4 * 18 + 2;
               if (mouseX >= itemLeft && mouseX < itemLeft + 16 && mouseY >= itemRight && mouseY < itemRight + 18) {
                   ContextMap context = SlotDisplayContext.fromLevel(this.minecraft.level);
                   SlotDisplay buttonIcon = new ItemStackSlotDisplay(new ItemStackTemplate(visibleRecipes.get(index).getItem(),visibleRecipes.get(index).getComponentsPatch()));
                   graphics.setTooltipForNextFrame(this.font, buttonIcon.resolveForFirstStack(context), mouseX, mouseY);
               }
           }
       }
   }

   private void extractButtons(GuiGraphicsExtractor p_282733_, int p_282136_, int p_282147_, int p_281987_, int p_281276_) {
      for(int i = 0; i < this.menu.getNumRecipes(); ++i) {
         int k = p_281987_ + i % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
         int l = i / RECIPES_COLUMNS;
         int i1 = p_281276_ + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
         int j1 = this.imageHeight;
         if (i == this.menu.getSelectedRecipeIndex()) {
            j1 += RECIPES_IMAGE_SIZE_HEIGHT;
         } else if (p_282136_ >= k && p_282147_ >= i1 && p_282136_ < k + RECIPES_IMAGE_SIZE_WIDTH && p_282147_ < i1 + RECIPES_IMAGE_SIZE_HEIGHT) {
            j1 += 36;
         }

         p_282733_.blit(RenderPipelines.GUI_TEXTURED,BG_LOCATION, k, i1 - 1, 0, j1, RECIPES_IMAGE_SIZE_WIDTH, RECIPES_IMAGE_SIZE_HEIGHT,256,256);
      }

   }

   private void renderRecipes(GuiGraphicsExtractor p_281999_, int p_282658_, int p_282563_) {
      List<ItemStack> list = this.menu.getRecipes();

      for(int i = 0; i < this.menu.getNumRecipes(); ++i) {
         int k = p_282658_ + i % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH;
         int l = i / RECIPES_COLUMNS;
         int i1 = p_282563_ + l * RECIPES_IMAGE_SIZE_HEIGHT + 2;
         p_281999_.item(list.get(i), k, i1);
      }

   }
   @Override
   public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
	   if (!this.menu.getRecipes().isEmpty()) {
	         int i = this.leftPos + RECIPES_X;
	         int j = this.topPos + RECIPES_Y;
	 

	         for(int l = 0; l < RECIPES_ROWS*RECIPES_COLUMNS; ++l) {
	            double d0 = event.x() - (double)(i + l % RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_WIDTH);
	            double d1 = event.y() - (double)(j + l / RECIPES_COLUMNS * RECIPES_IMAGE_SIZE_HEIGHT);
	            if (d0 >= 0.0D && d1 >= 0.0D && d0 < RECIPES_IMAGE_SIZE_WIDTH && d1 < RECIPES_IMAGE_SIZE_HEIGHT && this.menu.clickMenuButton(this.minecraft.player, l)) {
	               Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
	               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
	               return true;
	            }
	         }
	      }
	return super.mouseClicked(event, doubleClick);
}





}