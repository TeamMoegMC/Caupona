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

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.util.SoupInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AspicMeltingRecipe extends IDataRecipe {
	public static List<AspicMeltingRecipe> recipes;
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

	public Ingredient aspic;
	public Fluid fluid;
	public int amount=250;
	public int time=100;
	public AspicMeltingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		aspic=Ingredient.fromJson(jo.get("aspic"));
		fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("fluid").getAsString()));
		if(jo.has("time"))
			time=jo.get("time").getAsInt();
		if(jo.has("amount"))
			amount=jo.get("amount").getAsInt();
		if (fluid == null||fluid==Fluids.EMPTY)
			throw new InvalidRecipeException();
	}

	public AspicMeltingRecipe(ResourceLocation id, FriendlyByteBuf pb) {
		super(id);
		aspic= Ingredient.fromNetwork(pb);
		fluid = pb.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		amount =pb.readVarInt();
		time=pb.readVarInt();
	}

	public AspicMeltingRecipe(ResourceLocation id,Ingredient aspic, Fluid fluid) {
		super(id);
		this.aspic=aspic;
		this.fluid = fluid;
	}

	public void write(FriendlyByteBuf pack) {
		aspic.toNetwork(pack);
		pack.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS,fluid);
		pack.writeVarInt(amount);
		pack.writeVarInt(time);
	}

	public void serializeRecipeData(JsonObject jo) {
		jo.add("aspic", aspic.toJson());
		jo.addProperty("fluid", fluid.getRegistryName().toString());
		jo.addProperty("amount",amount);
		jo.addProperty("time",time);
	}

	public FluidStack handle(ItemStack s) {
		SoupInfo si=StewItem.getInfo(s);
		FluidStack fs=new FluidStack(fluid,amount);
		SoupFluid.setInfo(fs, si);
		return fs;
	}
	public SoupInfo info(ItemStack s) {
		SoupInfo si=StewItem.getInfo(s);
		return si;
	}
	public static AspicMeltingRecipe find(ItemStack aspic) {
		return recipes.stream().filter(t->t.aspic.test(aspic)).findFirst().orElse(null);
		
	}
}
