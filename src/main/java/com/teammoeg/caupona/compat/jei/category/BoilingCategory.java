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

package com.teammoeg.caupona.compat.jei.category;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;

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

public class BoilingCategory implements IRecipeCategory<BoilingRecipe> {
	public static ResourceLocation UID = new ResourceLocation(Main.MODID, "boiling");
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public BoilingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM,
				new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Main.MODID, "nail_soup"))));
		ResourceLocation guiMain = new ResourceLocation(Main.MODID, "textures/gui/jei/boiling.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);

	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends BoilingRecipe> getRecipeClass() {
		return BoilingRecipe.class;
	}

	public Component getTitle() {
		return new TranslatableComponent("gui.jei.category." + Main.MODID + ".boiling.title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(BoilingRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(recipe.time / 20f) + "s";
		Minecraft.getInstance().font.drawShadow(stack, burnTime, 103, 55, 0xFFFFFF);
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
	public void setRecipe(IRecipeLayoutBuilder builder, BoilingRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 30, 9)
				.addIngredient(VanillaTypes.FLUID, new FluidStack(recipe.before, 250))
				.setFluidRenderer(1250, false, 16, 46);
		builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 9)
				.addIngredient(VanillaTypes.FLUID, new FluidStack(recipe.after, 250))
				.setFluidRenderer(1250, false, 16, 46);
	}

}
