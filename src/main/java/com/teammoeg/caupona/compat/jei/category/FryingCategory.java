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
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.api.GameTranslation;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FryingCategory implements IRecipeCategory<SauteedRecipe> {
	public static ResourceLocation UID = new ResourceLocation(Main.MODID, "frying");
	private IDrawable BACKGROUND;
	private IDrawable ICON;
	private IGuiHelper helper;

	public FryingCategory(IGuiHelper guiHelper) {
		this.ICON = guiHelper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(CPItems.gravy_boat));
		this.BACKGROUND = guiHelper.createBlankDrawable(100, 105);
		this.helper = guiHelper;
	}

	@Override
	public ResourceLocation getUid() {
		return UID;
	}

	@Override
	public Class<? extends SauteedRecipe> getRecipeClass() {
		return SauteedRecipe.class;
	}

	public Component getTitle() {
		return new TranslatableComponent("gui.jei.category." + Main.MODID + ".frying.title");
	}

	@Override
	public void draw(SauteedRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX,
			double mouseY) {
		stack.pushPose();
		stack.scale(0.5f, 0.5f, 0);
		helper.createDrawable(new ResourceLocation(recipe.output.getRegistryName().getNamespace(),
				"textures/gui/recipes/" + recipe.output.getRegistryName().getPath() + ".png"), 0, 0, 200, 210)
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

		builder.addSlot(RecipeIngredientRole.INPUT, 30, 13).addIngredient(VanillaTypes.ITEM,
				new ItemStack(CPItems.gravy_boat));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 18).addIngredient(VanillaTypes.ITEM,
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
				allowence = conds.stream().map(e -> e.getTranslation(GameTranslation.get())).map(TextComponent::new)
						.collect(Collectors.toList());
			if (allowence != null && !allowence.isEmpty()) {
				if (mouseX < 50)
					allowence.add(0, new TranslatableComponent("recipe.caupona.allow"));
				else
					allowence.add(0, new TranslatableComponent("recipe.caupona.deny"));
				return allowence;
			}

		}
		return Arrays.asList();
	}

}
