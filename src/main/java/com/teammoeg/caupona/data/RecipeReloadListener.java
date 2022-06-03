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

package com.teammoeg.caupona.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.CookingRecipe;
import com.teammoeg.caupona.data.recipes.CountingTags;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("deprecation")
public class RecipeReloadListener implements ResourceManagerReloadListener {
	ReloadableServerResources data;
	public static final Logger logger = LogManager.getLogger(Main.MODNAME + " recipe generator");

	public RecipeReloadListener(ReloadableServerResources dpr) {
		data = dpr;
	}

	@Override
	public void onResourceManagerReload(@Nonnull ResourceManager resourceManager) {
		buildRecipeLists(data.getRecipeManager());
	}

	RecipeManager clientRecipeManager;
    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if(FoodValueRecipe.recipeset!=null)
        	FoodValueRecipe.recipeset.forEach(FoodValueRecipe::clearCache);
    }
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onRecipesUpdated(RecipesUpdatedEvent event) {
		buildRecipeLists(event.getRecipeManager());
	}

	static int generated_fv = 0;

	private static FoodValueRecipe addCookingTime(Item i, ItemStack iis, List<SmokingRecipe> irs, boolean force) {
		if (FoodValueRecipe.recipes.containsKey(i))
			return FoodValueRecipe.recipes.get(i);
		for (SmokingRecipe sr : irs) {
			if (sr.getIngredients().get(0).test(iis)) {
				ItemStack reslt = sr.assemble(null);
				if (DissolveRecipe.recipes.stream().anyMatch(e -> e.test(reslt)))
					continue;
				FoodValueRecipe ret = addCookingTime(reslt.getItem(), reslt, irs, true);
				FoodProperties of = i.getFoodProperties();
				if (of != null && of.getNutrition() > ret.heal) {
					ret.effects = of.getEffects();
					ret.heal = of.getNutrition();
					ret.sat = of.getSaturationModifier();
					ret.setRepersent(iis);
				}
				FoodValueRecipe.recipes.put(i, ret);
				ret.processtimes.put(i, sr.getCookingTime() + ret.processtimes.getOrDefault(reslt.getItem(), 0));
				return ret;
			}
		}
		if (force) {
			FoodProperties of = i.getFoodProperties();
			FoodValueRecipe ret = FoodValueRecipe.recipes.computeIfAbsent(i,
					e -> new FoodValueRecipe(new ResourceLocation(Main.MODID, "food/generated/" + (generated_fv++)), 0,
							0, iis, e));
			if (of != null && of.getNutrition() > ret.heal) {
				ret.effects = of.getEffects();
				ret.heal = of.getNutrition();
				ret.sat = of.getSaturationModifier();
				ret.setRepersent(iis);
			}
			return ret;
		}
		return null;
	}

	public static void buildRecipeLists(RecipeManager recipeManager) {
	
		Collection<Recipe<?>> recipes = recipeManager.getRecipes();
		if (recipes.size() == 0)
			return;
		
		logger.info("Building recipes...");
		Stopwatch sw=Stopwatch.createStarted();
		BowlContainingRecipe.recipes = filterRecipes(recipes, BowlContainingRecipe.class, BowlContainingRecipe.TYPE)
				.collect(Collectors.toMap(e -> e.fluid, UnaryOperator.identity()));
		FoodValueRecipe.recipes = filterRecipes(recipes, FoodValueRecipe.class, FoodValueRecipe.TYPE)
				.flatMap(t -> t.processtimes.keySet().stream().map(i -> new Pair<>(i, t)))
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		List<SmokingRecipe> irs = recipeManager.getAllRecipesFor(RecipeType.SMOKING);
		DissolveRecipe.recipes = filterRecipes(recipes, DissolveRecipe.class, DissolveRecipe.TYPE)
				.collect(Collectors.toList());
		CookingRecipe.recipes = filterRecipes(recipes, CookingRecipe.class, CookingRecipe.TYPE)
				.collect(Collectors.toMap(e -> e.output, UnaryOperator.identity()));
		BoilingRecipe.recipes = filterRecipes(recipes, BoilingRecipe.class, BoilingRecipe.TYPE)
				.collect(Collectors.toMap(e -> e.before, UnaryOperator.identity()));
		FluidFoodValueRecipe.recipes = filterRecipes(recipes, FluidFoodValueRecipe.class, FluidFoodValueRecipe.TYPE)
				.collect(Collectors.toMap(e -> e.f, UnaryOperator.identity()));
		CountingTags.tags = Stream
				.concat(filterRecipes(recipes, CountingTags.class, CountingTags.TYPE).flatMap(r -> r.tag.stream()),
						CookingRecipe.recipes.values().stream().flatMap(CookingRecipe::getTags))
				.collect(Collectors.toSet());
		// CountingTags.tags.forEach(System.out::println);
		CookingRecipe.cookables = CookingRecipe.recipes.values().stream().flatMap(CookingRecipe::getAllNumbers)
				.collect(Collectors.toSet());
		
		for (Item i : ForgeRegistries.ITEMS) {
			ItemStack iis = new ItemStack(i);
			if (FoodValueRecipe.recipes.containsKey(i))
				continue;
			if (DissolveRecipe.recipes.stream().anyMatch(e -> e.test(iis)))
				continue;
			addCookingTime(i, iis, irs, false);
		}

		FoodValueRecipe.recipeset = new HashSet<>(FoodValueRecipe.recipes.values());
		CookingRecipe.sorted = new ArrayList<>(CookingRecipe.recipes.values());
		CookingRecipe.sorted.sort((t2, t1) -> t1.getPriority() - t2.getPriority());
		sw.stop();
		logger.info("Recipes built, cost {}",sw);
	}

	static <R extends Recipe<?>> Stream<R> filterRecipes(Collection<Recipe<?>> recipes, Class<R> recipeClass,
			RecipeType<?> recipeType) {
		return recipes.stream().filter(iRecipe -> iRecipe.getType() == recipeType).map(recipeClass::cast);
	}
}
