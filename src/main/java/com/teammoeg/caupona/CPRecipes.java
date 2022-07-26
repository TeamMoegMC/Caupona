package com.teammoeg.caupona;

import com.teammoeg.caupona.data.CPRecipeSerializer;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.CookingRecipe;
import com.teammoeg.caupona.data.recipes.CountingTags;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FryingRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CPRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
			.create(ForgeRegistries.RECIPE_SERIALIZERS, Main.MODID);

	static {
		CookingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("cooking",
				() -> new CPRecipeSerializer<CookingRecipe>(CookingRecipe::new, CookingRecipe::new,
						CookingRecipe::write));
		FryingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("frying",
				() -> new CPRecipeSerializer<FryingRecipe>(FryingRecipe::new, FryingRecipe::new,
						FryingRecipe::write));
		DoliumRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dolium",
				() -> new CPRecipeSerializer<DoliumRecipe>(DoliumRecipe::new, DoliumRecipe::new,
						DoliumRecipe::write));
		BoilingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("boiling",
				() -> new CPRecipeSerializer<BoilingRecipe>(BoilingRecipe::new, BoilingRecipe::new,
						BoilingRecipe::write));
		BowlContainingRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("bowl",
				() -> new CPRecipeSerializer<BowlContainingRecipe>(BowlContainingRecipe::new,
						BowlContainingRecipe::new, BowlContainingRecipe::write));
		DissolveRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("dissolve",
				() -> new CPRecipeSerializer<DissolveRecipe>(DissolveRecipe::new, DissolveRecipe::new,
						DissolveRecipe::write));
		CountingTags.SERIALIZER = RECIPE_SERIALIZERS.register("tags",
				() -> new CPRecipeSerializer<CountingTags>(CountingTags::new, CountingTags::new,
						CountingTags::write));
		FoodValueRecipe.SERIALIZER = RECIPE_SERIALIZERS.register("food",
				() -> new CPRecipeSerializer<FoodValueRecipe>(FoodValueRecipe::new, FoodValueRecipe::new,
						FoodValueRecipe::write));
		FluidFoodValueRecipe.SERIALIZER=RECIPE_SERIALIZERS.register("fluid_food",() ->new CPRecipeSerializer<FluidFoodValueRecipe>(FluidFoodValueRecipe::new, FluidFoodValueRecipe::new,
				FluidFoodValueRecipe::write));
		
	}

	public static void registerRecipeTypes() {
		CookingRecipe.TYPE = RecipeType.register(Main.MODID + ":stew");
		BoilingRecipe.TYPE = RecipeType.register(Main.MODID + ":boil");
		BowlContainingRecipe.TYPE = RecipeType.register(Main.MODID + ":bowl");
		DissolveRecipe.TYPE = RecipeType.register(Main.MODID + ":dissolve");
		CountingTags.TYPE = RecipeType.register(Main.MODID + ":tags");
		FoodValueRecipe.TYPE = RecipeType.register(Main.MODID + ":food");
		FluidFoodValueRecipe.TYPE = RecipeType.register(Main.MODID + ":fluid_food");
		FryingRecipe.TYPE = RecipeType.register(Main.MODID + ":frying");
		DoliumRecipe.TYPE = RecipeType.register(Main.MODID + ":dolium");
	}
}