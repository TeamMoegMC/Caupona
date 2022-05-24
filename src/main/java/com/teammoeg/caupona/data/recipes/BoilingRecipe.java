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

package com.teammoeg.caupona.data.recipes;

import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BoilingRecipe extends IDataRecipe {
	public static Map<Fluid, BoilingRecipe> recipes;
	public static RecipeType<?> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public Fluid before;
	public Fluid after;
	public int time;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}

	public BoilingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		before = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("from").getAsString()));
		after = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("to").getAsString()));
		time = jo.get("time").getAsInt();
		if(before==Fluids.EMPTY||after==Fluids.EMPTY)
			throw new InvalidRecipeException();
	}

	public BoilingRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		before = data.readRegistryId();
		after = data.readRegistryId();
		time = data.readVarInt();
	}

	public BoilingRecipe(ResourceLocation id, Fluid before, Fluid after, int time) {
		super(id);
		this.before = before;
		this.after = after;
		this.time = time;
	}

	public void write(FriendlyByteBuf data) {
		data.writeRegistryId(before);
		data.writeRegistryId(after);
		data.writeVarInt(time);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("from", before.getRegistryName().toString());
		json.addProperty("to", after.getRegistryName().toString());
		json.addProperty("time", time);
	}

	public FluidStack handle(FluidStack org) {
		FluidStack fs = new FluidStack(after, org.getAmount());
		if (org.hasTag())
			fs.setTag(org.getTag());
		return fs;
	}
}
