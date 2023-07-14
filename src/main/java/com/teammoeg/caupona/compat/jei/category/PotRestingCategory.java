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
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PotRestingCategory implements IRecipeCategory<DoliumRecipe> {
	public static RecipeType<DoliumRecipe> TYPE=RecipeType.create(CPMain.MODID, "pot_resting",DoliumRecipe.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public PotRestingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(CPMain.MODID, "goulash_aspic"))));
		ResourceLocation guiMain = new ResourceLocation(CPMain.MODID, "textures/gui/jei/fluid_resting.png");
		this.BACKGROUND = guiHelper.createDrawable(guiMain, 0, 0, 127, 63);
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CPMain.MODID + ".resting_aspic.title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(DoliumRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(CPConfig.COMMON.staticTime.get() / 20f) + "s";
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
		builder.addSlot(RecipeIngredientRole.OUTPUT, 83, 24).addIngredient(VanillaTypes.ITEM_STACK, recipe.output);
		builder.addSlot(RecipeIngredientRole.INPUT, 30, 9)
				.addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(recipe.fluid, recipe.amount))
				.setFluidRenderer(1250, false, 16, 46)
				.addTooltipCallback(new BaseCallback(recipe.base, recipe.density));
	}


	@Override
	public RecipeType<DoliumRecipe> getRecipeType() {
		return TYPE;
	}

}
