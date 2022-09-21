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

package com.teammoeg.caupona.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.client.DoliumScreen;
import com.teammoeg.caupona.client.KitchenStoveScreen;
import com.teammoeg.caupona.client.PanScreen;
import com.teammoeg.caupona.client.PortableBrazierScreen;
import com.teammoeg.caupona.client.StewPotScreen;
import com.teammoeg.caupona.compat.jei.category.BoilingCategory;
import com.teammoeg.caupona.compat.jei.category.BowlEmptyCategory;
import com.teammoeg.caupona.compat.jei.category.BowlFillCategory;
import com.teammoeg.caupona.compat.jei.category.BrazierCategory;
import com.teammoeg.caupona.compat.jei.category.DoliumRestingCategory;
import com.teammoeg.caupona.compat.jei.category.FryingCategory;
import com.teammoeg.caupona.compat.jei.category.PotCategory;
import com.teammoeg.caupona.compat.jei.category.PotRestingCategory;
import com.teammoeg.caupona.compat.jei.category.StewCookingCategory;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

@JeiPlugin
public class JEICompat implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation(Main.MODID, "jei_plugin");
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(CPItems.pbrazier), BrazierCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.stew_pot), PotCategory.UID, BoilingCategory.UID,
				PotRestingCategory.UID, StewCookingCategory.UID);
		for (Block bl : CPBlocks.dolium)
			registration.addRecipeCatalyst(new ItemStack(bl), DoliumRestingCategory.UID, PotRestingCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.STONE_PAN), FryingCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.COPPER_PAN), FryingCategory.UID);
		registration.addRecipeCatalyst(new ItemStack(CPBlocks.IRON_PAN), FryingCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(new ArrayList<>(AspicMeltingRecipe.recipes), BrazierCategory.UID);
		registration.addRecipes(new ArrayList<>(AspicMeltingRecipe.recipes), PotCategory.UID);
		registration.addRecipes(new ArrayList<>(BoilingRecipe.recipes.values()), BoilingCategory.UID);
		registration.addRecipes(new ArrayList<>(BowlContainingRecipe.recipes.values()), BowlEmptyCategory.UID);
		registration.addRecipes(new ArrayList<>(BowlContainingRecipe.recipes.values()), BowlFillCategory.UID);
		registration.addRecipes(new ArrayList<>(DoliumRecipe.recipes), DoliumRestingCategory.UID);
		registration.addRecipes(new ArrayList<>(StewCookingRecipe.sorted), StewCookingCategory.UID);
		registration.addRecipes(new ArrayList<>(SauteedRecipe.sorted), FryingCategory.UID);
		registration.addRecipes(
				DoliumRecipe.recipes.stream().filter(e -> e.items.size() == 0).collect(Collectors.toList()),
				PotRestingCategory.UID);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
		registration.addRecipeCategories(new BrazierCategory(guiHelper), new PotCategory(guiHelper),
				new BoilingCategory(guiHelper), new BowlEmptyCategory(guiHelper), new BowlFillCategory(guiHelper),
				new DoliumRestingCategory(guiHelper), new PotRestingCategory(guiHelper),
				new StewCookingCategory(guiHelper), new FryingCategory(guiHelper));
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registry) {
		registry.addGuiContainerHandler(PortableBrazierScreen.class, new IGuiContainerHandler<PortableBrazierScreen>() {
			@Override
			public Collection<IGuiClickableArea> getGuiClickableAreas(PortableBrazierScreen containerScreen,
					double mouseX, double mouseY) {
				IGuiClickableArea clickableArea1 = IGuiClickableArea.createBasic(60, 11, 14, 16, BrazierCategory.UID);
				IGuiClickableArea clickableArea2 = IGuiClickableArea.createBasic(90, 11, 14, 16, BrazierCategory.UID);
				return List.of(clickableArea1, clickableArea2);
			}
		});

		registry.addRecipeClickArea(DoliumScreen.class, 118, 32, 10, 25, DoliumRestingCategory.UID);
		registry.addRecipeClickArea(StewPotScreen.class, 132, 34, 38, 16, PotCategory.UID, BoilingCategory.UID,
				PotRestingCategory.UID, StewCookingCategory.UID);
		registry.addRecipeClickArea(KitchenStoveScreen.class, 61, 0, 54, 28, VanillaRecipeCategoryUid.FUEL);
		registry.addRecipeClickArea(PanScreen.class, 125, 30, 38, 16, FryingCategory.UID);
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registration) {

	}

}
