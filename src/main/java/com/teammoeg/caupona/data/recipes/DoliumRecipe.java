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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.SerializeUtil;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DoliumRecipe extends IDataRecipe {
	public static List<DoliumRecipe> recipes;
	public static RecipeType<?> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}

	public List<Pair<Ingredient,Integer>> items;
	
	public ResourceLocation base;
	public Fluid fluid;
	public float density=0;


	public DoliumRecipe(ResourceLocation id, ResourceLocation base, Fluid fluid, float density, List<Pair<Ingredient,Integer>> items) {
		super(id);
		if(items!=null)
			this.items = new ArrayList<>(items);
		else
			this.items=new ArrayList<>();
		this.base = base;
		this.fluid = fluid;
		this.density = density;
	}

	public DoliumRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		if(jo.has("items")) 
			items=SerializeUtil.parseJsonList(jo.get("items"),j->Pair.of(Ingredient.fromJson(j.get("item")),(j.has("count")?j.get("count").getAsInt():1)));
		
		if(jo.has("base"))
			base=new ResourceLocation(jo.get("base").getAsString());
		fluid=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("fluid").getAsString()));
		if(jo.has("density"))
			density=jo.get("density").getAsFloat();
		
	}

	public boolean test(Fluid f,ItemStack... ss) {
		if(ss.length==0&&items.size()>0)return false;
		return true;
	}

	public DoliumRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		items = SerializeUtil.readList(data,d->Pair.of(Ingredient.fromNetwork(d),d.readVarInt()));
		base=SerializeUtil.readOptional(data,FriendlyByteBuf::readResourceLocation).orElse(null);
		fluid=data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		density=data.readFloat();
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, items,(r,d)->{r.getFirst().toNetwork(data);data.writeVarInt(r.getSecond());});
		SerializeUtil.writeOptional2(data,base,FriendlyByteBuf::writeResourceLocation);
		data.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
		data.writeFloat(density);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("items",SerializeUtil.toJsonList(items,(r)->{JsonObject jo=new JsonObject();jo.add("item",r.getFirst().toJson());jo.addProperty("count",r.getSecond());return jo;}));
		if(base!=null)
		json.addProperty("base",base.toString());
		json.addProperty("fluid",fluid.getRegistryName().toString());
		json.addProperty("density", density);
	}

}
