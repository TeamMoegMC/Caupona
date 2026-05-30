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

package com.teammoeg.caupona;

import com.mojang.serialization.MapCodec;
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
import com.teammoeg.caupona.util.SerializeUtil;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
			.create(Registries.RECIPE_SERIALIZER, CPMain.MODID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister
			.create(Registries.RECIPE_TYPE, CPMain.MODID);
	static {
		StewCookingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("cooking",() -> createSerializer(StewCookingRecipe.CODEC,StewCookingRecipe.STREAM_CODEC));
		SauteedRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("frying",() -> createSerializer(SauteedRecipe.CODEC,SauteedRecipe.STREAM_CODEC));
		DoliumRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dolium",() -> createSerializer(DoliumRecipe.CODEC,DoliumRecipe.STREAM_CODEC));
		BoilingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("boiling",() -> createSerializer(BoilingRecipe.CODEC,BoilingRecipe.STREAM_CODEC));
		BowlContainingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("bowl",() -> createSerializer(BowlContainingRecipe.CODEC,BowlContainingRecipe.STREAM_CODEC));
		DissolveRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dissolve",() -> createSerializer(DissolveRecipe.CODEC,DissolveRecipe.STREAM_CODEC));
		CountingTags.SERIALIZER = RECIPE_SERIALIZERS.register("tags",() -> createSerializer(CountingTags.CODEC,CountingTags.STREAM_CODEC));
		FoodValueRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("food",() -> createSerializer(FoodValueRecipe.CODEC,FoodValueRecipe.STREAM_CODEC));
		FluidFoodValueRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("fluid_food",() -> createSerializer(FluidFoodValueRecipe.CODEC,FluidFoodValueRecipe.STREAM_CODEC));
		AspicMeltingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("aspic_melt",() -> createSerializer(AspicMeltingRecipe.CODEC,AspicMeltingRecipe.STREAM_CODEC));
		SpiceRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("spice",() -> createSerializer(SpiceRecipe.CODEC,SpiceRecipe.STREAM_CODEC));
	}
	public static <T extends Recipe<?>> RecipeSerializer<T> createSerializer(MapCodec<T> codec,StreamCodec<RegistryFriendlyByteBuf, T> stream){
		return new RecipeSerializer<>(codec,stream);
	}
	static {
		StewCookingRecipe.TYPE = RECIPE_TYPES.register("stew",RecipeType::simple);
		BoilingRecipe.TYPE = RECIPE_TYPES.register("boil",RecipeType::simple);
		BowlContainingRecipe.TYPE = RECIPE_TYPES.register("bowl",RecipeType::simple);
		DissolveRecipe.TYPE = RECIPE_TYPES.register("dissolve",RecipeType::simple);
		CountingTags.TYPE = RECIPE_TYPES.register("tags",RecipeType::simple);
		FoodValueRecipe.TYPE = RECIPE_TYPES.register("food",RecipeType::simple);
		FluidFoodValueRecipe.TYPE = RECIPE_TYPES.register("fluid_food",RecipeType::simple);
		SauteedRecipe.TYPE = RECIPE_TYPES.register("frying",RecipeType::simple);
		DoliumRecipe.TYPE = RECIPE_TYPES.register("dolium",RecipeType::simple);
		AspicMeltingRecipe.TYPE = RECIPE_TYPES.register("aspic_melt",RecipeType::simple);
		SpiceRecipe.TYPE = RECIPE_TYPES.register("spice",RecipeType::simple);
	}
}