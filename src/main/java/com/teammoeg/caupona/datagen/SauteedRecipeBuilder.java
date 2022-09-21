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

package com.teammoeg.caupona.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.recipes.FryingRecipe;
import com.teammoeg.caupona.data.recipes.IngredientCondition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class FryingRecipeBuilder {

	private List<IngredientCondition> allow = new ArrayList<>();
	private List<IngredientCondition> deny = new ArrayList<>();
	private int priority = 0;
	private int time = 200;
	private Item output;
	private ResourceLocation id;

	public FryingRecipeBuilder(ResourceLocation id, Item out) {
		output = out;
		this.id = id;
	}

	public static FryingRecipeBuilder start(Item out) {
		return new FryingRecipeBuilder(new ResourceLocation(Main.MODID, "frying/" + out.getRegistryName().getPath()),
				out);
	}

	public IngredientConditionsBuilder<FryingRecipeBuilder> require() {
		return new IngredientConditionsBuilder<FryingRecipeBuilder>(this, allow, allow, deny);
	}

	public IngredientConditionsBuilder<FryingRecipeBuilder> not() {
		return new IngredientConditionsBuilder<FryingRecipeBuilder>(this, deny, allow, deny);
	}

	public FryingRecipeBuilder prio(int p) {
		priority = p;
		return this;
	}

	public FryingRecipeBuilder special() {
		priority |= 1024;
		return this;
	}

	public FryingRecipeBuilder high() {
		priority |= 128;
		return this;
	}

	public FryingRecipeBuilder med() {
		priority |= 64;
		return this;
	}

	public FryingRecipeBuilder low() {
		return this;
	}

	public FryingRecipeBuilder time(int t) {
		time = t;
		return this;
	}

	public FryingRecipe end() {
		return new FryingRecipe(id, allow, deny, priority, time, output);
	}

	public FryingRecipe finish(Consumer<? super FryingRecipe> csr) {
		FryingRecipe r = end();
		csr.accept(r);
		return r;
	}
}
