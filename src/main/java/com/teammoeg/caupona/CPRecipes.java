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

package com.teammoeg.caupona;

import com.teammoeg.caupona.data.CPRecipeSerializer;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.CountingTags;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CPRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, CPMain.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
			.create(ForgeRegistries.RECIPE_TYPES, CPMain.MODID);
	static {
		StewCookingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("cooking",
				() -> new CPRecipeSerializer<StewCookingRecipe>(StewCookingRecipe::new, StewCookingRecipe::new,
						StewCookingRecipe::write));
		SauteedRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("frying",
				() -> new CPRecipeSerializer<SauteedRecipe>(SauteedRecipe::new, SauteedRecipe::new, SauteedRecipe::write));
		DoliumRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dolium",
				() -> new CPRecipeSerializer<DoliumRecipe>(DoliumRecipe::new, DoliumRecipe::new, DoliumRecipe::write));
		BoilingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("boiling",
				() -> new CPRecipeSerializer<BoilingRecipe>(BoilingRecipe::new, BoilingRecipe::new,
						BoilingRecipe::write));
		BowlContainingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("bowl",
				() -> new CPRecipeSerializer<BowlContainingRecipe>(BowlContainingRecipe::new, BowlContainingRecipe::new,
						BowlContainingRecipe::write));
		DissolveRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dissolve",
				() -> new CPRecipeSerializer<DissolveRecipe>(DissolveRecipe::new, DissolveRecipe::new,
						DissolveRecipe::write));
		CountingTags.SERIALIZER = RECIPE_SERIALIZERS.register("tags",
				() -> new CPRecipeSerializer<CountingTags>(CountingTags::new, CountingTags::new, CountingTags::write));
		FoodValueRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("food",
				() -> new CPRecipeSerializer<FoodValueRecipe>(FoodValueRecipe::new, FoodValueRecipe::new,
						FoodValueRecipe::write));
		FluidFoodValueRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("fluid_food",
				() -> new CPRecipeSerializer<FluidFoodValueRecipe>(FluidFoodValueRecipe::new, FluidFoodValueRecipe::new,
						FluidFoodValueRecipe::write));
		AspicMeltingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("aspic_melt",
				() -> new CPRecipeSerializer<AspicMeltingRecipe>(AspicMeltingRecipe::new, AspicMeltingRecipe::new,
						AspicMeltingRecipe::write));
		SpiceRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("spice",
				() -> new CPRecipeSerializer<SpiceRecipe>(SpiceRecipe::new, SpiceRecipe::new, SpiceRecipe::write));
	}

	static {
		StewCookingRecipe.TYPE = RECIPE_TYPES.register("stew",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"stew")));
		BoilingRecipe.TYPE = RECIPE_TYPES.register("boil",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"boil")));
		BowlContainingRecipe.TYPE = RECIPE_TYPES.register("bowl",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"bowl")));
		DissolveRecipe.TYPE = RECIPE_TYPES.register("dissolve",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"dissolve")));
		CountingTags.TYPE = RECIPE_TYPES.register("tags",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"tags")));
		FoodValueRecipe.TYPE = RECIPE_TYPES.register("food",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"food")));
		FluidFoodValueRecipe.TYPE = RECIPE_TYPES.register("fluid_food",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"fluid_food")));
		SauteedRecipe.TYPE = RECIPE_TYPES.register("frying",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"frying")));
		DoliumRecipe.TYPE = RECIPE_TYPES.register("dolium",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"dolium")));
		AspicMeltingRecipe.TYPE = RECIPE_TYPES.register("aspic_melt",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"aspic_melt")));
		SpiceRecipe.TYPE = RECIPE_TYPES.register("spice",()->RecipeType.simple(new ResourceLocation(CPMain.MODID ,"spice")));
	}
}