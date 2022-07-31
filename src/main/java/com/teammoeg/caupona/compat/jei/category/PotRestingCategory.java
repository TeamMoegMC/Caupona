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

package com.teammoeg.caupona.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PotRestingCategory implements IRecipeCategory<DoliumRecipe> {
	public static ResourceLocation UID = new ResourceLocation(Main.MODID, "pot_resting");
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public PotRestingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM,
				new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Main.MODID, "goulash_aspic"))));
		ResourceLocation guiMain = new ResourceLocation(Main.MODID, "textures/gui/jei/fluid_resting.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends DoliumRecipe> getRecipeClass() {
		return DoliumRecipe.class;
	}

	public Component getTitle() {
		return new TranslatableComponent("gui.jei.category." + Main.MODID + ".resting_aspic.title");
	}

	@Override
	public void draw(DoliumRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(Config.COMMON.staticTime.get() / 20f) + "s";
		Minecraft.getInstance().font.drawShadow(stack, burnTime, 90, 55, 0xFFFFFF);
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DoliumRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 24).addIngredient(VanillaTypes.ITEM, recipe.output);
		builder.addSlot(RecipeIngredientRole.INPUT, 30, 9)
				.addIngredient(VanillaTypes.FLUID, new FluidStack(recipe.fluid, recipe.amount))
				.setFluidRenderer(1250, false, 16, 46)
				.addTooltipCallback(new BaseCallback(recipe.base, recipe.density));
	}

}
