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

package com.teammoeg.caupona.data.recipes;

import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BoilingRecipe extends IDataRecipe {
	public static Map<Fluid, RecipeHolder<BoilingRecipe>> recipes;
	public static DeferredHolder<?,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<?,RecipeSerializer<?>> SERIALIZER;
	public Fluid before;
	public Fluid after;
	public int time;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public BoilingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		before = BuiltInRegistries.FLUID.get(new ResourceLocation(jo.get("from").getAsString()));
		after = BuiltInRegistries.FLUID.get(new ResourceLocation(jo.get("to").getAsString()));
		time = jo.get("time").getAsInt();
		if (before == Fluids.EMPTY || after == Fluids.EMPTY)
			throw new InvalidRecipeException();
	}

	public BoilingRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		before = data.readById(BuiltInRegistries.FLUID);
		after = data.readById(BuiltInRegistries.FLUID);
		time = data.readVarInt();
	}

	public BoilingRecipe(ResourceLocation id, Fluid before, Fluid after, int time) {
		super(id);
		this.before = before;
		this.after = after;
		this.time = time;
	}

	public void write(FriendlyByteBuf data) {
		data.writeId(BuiltInRegistries.FLUID,before);
		data.writeId(BuiltInRegistries.FLUID,after);
		data.writeVarInt(time);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("from", Utils.getRegistryName(before).toString());
		json.addProperty("to", Utils.getRegistryName(after).toString());
		json.addProperty("time", time);
	}

	public FluidStack handle(FluidStack org) {
		FluidStack fs = new FluidStack(after, org.getAmount());
		if (org.hasTag())
			fs.setTag(org.getTag());
		return fs;
	}
}
