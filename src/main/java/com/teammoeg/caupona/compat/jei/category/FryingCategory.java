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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.api.GameTranslation;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.util.Utils;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FryingCategory implements IRecipeCategory<SauteedRecipe> {
	public static RecipeType<SauteedRecipe> TYPE=RecipeType.create(CPMain.MODID, "frying",SauteedRecipe.class);
	private IDrawable BACKGROUND;
	private IDrawable ICON;
	private IGuiHelper helper;

	public FryingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CPItems.gravy_boat.get()));
		this.BACKGROUND = guiHelper.createBlankDrawable(100, 105);
		this.helper = guiHelper;
	}


	public Component getTitle() {
		return Utils.translate("gui.jei.category." + CPMain.MODID + ".frying.title");
	}

	@Override
	public void draw(SauteedRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		stack.pushPose();
		stack.scale(0.5f, 0.5f, 0);
		helper.createDrawable(new ResourceLocation(Utils.getRegistryName(recipe.output).getNamespace(),
				"textures/gui/recipes/" + Utils.getRegistryName(recipe.output).getPath() + ".png"), 0, 0, 200, 210)
				.draw(stack);
		stack.popPose();
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
	public void setRecipe(IRecipeLayoutBuilder builder, SauteedRecipe recipe, IFocusGroup focuses) {

		builder.addSlot(RecipeIngredientRole.INPUT, 30, 13).addIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(CPItems.gravy_boat.get()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 18).addIngredient(VanillaTypes.ITEM_STACK,
				new ItemStack(recipe.output));
	}

	public static boolean inRange(double x, double y, int ox, int oy, int w, int h) {
		return x > ox && x < ox + w && y > oy && y < oy + h;
	}

	@Override
	public List<Component> getTooltipStrings(SauteedRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX,
			double mouseY) {
		if (inRange(mouseX, mouseY, 0, 50, 100, 50)) {
			List<Component> allowence = null;
			List<IngredientCondition> conds;
			if (mouseX < 50)
				conds = recipe.getAllow();
			else
				conds = recipe.getDeny();
			if (conds != null)
				allowence = conds.stream().map(e -> e.getTranslation(GameTranslation.get())).map(Utils::string)
						.collect(Collectors.toList());
			if (allowence != null && !allowence.isEmpty()) {
				if (mouseX < 50)
					allowence.add(0, Utils.translate("recipe.caupona.allow"));
				else
					allowence.add(0, Utils.translate("recipe.caupona.deny"));
				return allowence;
			}

		}
		return Arrays.asList();
	}


	@Override
	public RecipeType<SauteedRecipe> getRecipeType() {
		return TYPE;
	}

}
