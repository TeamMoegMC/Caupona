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

package com.teammoeg.caupona.datagen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SauteedRecipeBuilder {

	private List<IngredientCondition> allow = new ArrayList<>();
	private List<IngredientCondition> deny = new ArrayList<>();
	private int priority = 0;
	private int time = 200;
	private Item output;
	private ResourceLocation id;
	private boolean removeNBT=false;
	public SauteedRecipeBuilder(ResourceLocation id, Item out) {
		output = out;
		this.id = id;
	}

	public static SauteedRecipeBuilder start(Item out) {
		return new SauteedRecipeBuilder(new ResourceLocation(Main.MODID, "frying/" + Utils.getRegistryName(out).getPath()),
				out);
	}

	public IngredientConditionsBuilder<SauteedRecipeBuilder> require() {
		return new IngredientConditionsBuilder<SauteedRecipeBuilder>(this, allow, allow, deny);
	}

	public IngredientConditionsBuilder<SauteedRecipeBuilder> not() {
		return new IngredientConditionsBuilder<SauteedRecipeBuilder>(this, deny, allow, deny);
	}

	public SauteedRecipeBuilder prio(int p) {
		priority = p;
		return this;
	}

	public SauteedRecipeBuilder special() {
		priority |= 1024;
		return this;
	}

	public SauteedRecipeBuilder high() {
		priority |= 128;
		return this;
	}

	public SauteedRecipeBuilder med() {
		priority |= 64;
		return this;
	}

	public SauteedRecipeBuilder low() {
		return this;
	}

	public SauteedRecipeBuilder time(int t) {
		time = t;
		return this;
	}
	public SauteedRecipeBuilder removeNBT() {
		removeNBT=true;
		return this;
	}
	public SauteedRecipe end() {
		return new SauteedRecipe(id, allow, deny, priority, time, output,removeNBT);
	}

	public SauteedRecipe finish(Consumer<? super SauteedRecipe> csr) {
		SauteedRecipe r = end();
		csr.accept(r);
		return r;
	}
}
