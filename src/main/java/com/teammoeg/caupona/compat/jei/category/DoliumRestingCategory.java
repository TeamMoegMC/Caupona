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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class DoliumRestingCategory implements IRecipeCategory<DoliumRecipe> {
	public static ResourceLocation UID = new ResourceLocation(Main.MODID, "dolium_resting");
	private IDrawable BACKGROUND;
	private IDrawable ICON;

	public DoliumRestingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CPBlocks.dolium.get(0)));
		ResourceLocation guiMain = new ResourceLocation(Main.MODID, "textures/gui/jei/maximum_resting.png");
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
		return new TranslatableComponent("gui.jei.category." + Main.MODID + ".resting.title");
	}

	@SuppressWarnings("resource")
	@Override
	public void draw(DoliumRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		String burnTime = String.valueOf(Config.COMMON.staticTime.get() / 20f) + "s";
		Minecraft.getInstance().font.drawShadow(stack, burnTime, 100, 55, 0xFFFFFF);
	}

	@Override
	public IDrawable getBackground() {
		return BACKGROUND;
	}

	@Override
	public IDrawable getIcon() {
		return ICON;
	}

	private static List<ItemStack> unpack(Pair<Ingredient, Integer> ps) {
		List<ItemStack> sl = new ArrayList<>();
		for (ItemStack is : ps.getFirst().getItems())
			sl.add(ItemHandlerHelper.copyStackWithSize(is, ps.getSecond() > 0 ? ps.getSecond() : 1));
		return sl;
	}

	private static List<ItemStack> unpack(Ingredient ps) {
		if (ps instanceof NBTIngredient)
			return Arrays.asList(ps.getItems());
		List<ItemStack> sl = new ArrayList<>();
		for (ItemStack is : ps.getItems())
			if (is.isDamageableItem())
				for (int i = 0; i <= is.getMaxDamage(); i++) {
					ItemStack iss = is.copy();
					iss.setDamageValue(i);
					sl.add(iss);
				}
			else
				sl.add(is);
		return sl;
	}

	private static RecipeIngredientRole type(Pair<Ingredient, Integer> ps) {
		return ps.getSecond() == 0 ? RecipeIngredientRole.CATALYST : RecipeIngredientRole.INPUT;
	}

	private static class CatalistCallback implements IRecipeSlotTooltipCallback {
		int cnt;

		public CatalistCallback(int cnt) {
			super();
			this.cnt = cnt;
		}

		@Override
		public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
			if (cnt == 0)
				tooltip.add(new TranslatableComponent("gui.jei.category.caupona.catalyst"));
		}

	};

	private static CatalistCallback cb(Pair<Ingredient, Integer> ps) {
		return new CatalistCallback(ps.getSecond());
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, DoliumRecipe recipe, IFocusGroup focuses) {
		if (recipe.items.size() > 0) {
			builder.addSlot(type(recipe.items.get(0)), 4, 6)
					.addIngredients(VanillaTypes.ITEM, unpack(recipe.items.get(0)))
					.addTooltipCallback(cb(recipe.items.get(0)));
			if (recipe.items.size() > 1) {
				builder.addSlot(type(recipe.items.get(1)), 4, 24)
						.addIngredients(VanillaTypes.ITEM, unpack(recipe.items.get(1)))
						.addTooltipCallback(cb(recipe.items.get(1)));
				if (recipe.items.size() > 2) {
					builder.addSlot(type(recipe.items.get(2)), 4, 42)
							.addIngredients(VanillaTypes.ITEM, unpack(recipe.items.get(2)))
							.addTooltipCallback(cb(recipe.items.get(2)));
				}
			}
		}
		builder.addSlot(RecipeIngredientRole.OUTPUT, 109, 24).addIngredient(VanillaTypes.ITEM, recipe.output);
		if (recipe.extra != null) {
			builder.addSlot(RecipeIngredientRole.INPUT, 89, 10).addIngredients(VanillaTypes.ITEM, unpack(recipe.extra));
		}
		if (!(recipe.fluid == Fluids.EMPTY))
			builder.addSlot(RecipeIngredientRole.INPUT, 26, 9)
					.addIngredient(VanillaTypes.FLUID, new FluidStack(recipe.fluid, recipe.amount))
					.setFluidRenderer(1250, false, 16, 46)
					.addTooltipCallback(new BaseCallback(recipe.base, recipe.density));

	}

}
