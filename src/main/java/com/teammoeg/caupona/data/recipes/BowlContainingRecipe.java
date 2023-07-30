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

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BowlContainingRecipe extends IDataRecipe {
	public static Map<Fluid, BowlContainingRecipe> recipes;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
 
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	public Item bowl;
	public Fluid fluid;

	public BowlContainingRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		bowl = ForgeRegistries.ITEMS.getValue(new ResourceLocation(jo.get("item").getAsString()));
		fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(jo.get("fluid").getAsString()));
		if (bowl == null || bowl == Items.AIR || fluid == null || fluid == Fluids.EMPTY)
			throw new InvalidRecipeException();
	}

	public BowlContainingRecipe(ResourceLocation id, FriendlyByteBuf pb) {
		super(id);
		bowl = pb.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
		fluid = pb.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
	}

	public BowlContainingRecipe(ResourceLocation id, Item bowl, Fluid fluid) {
		super(id);
		this.bowl = bowl;
		this.fluid = fluid;
	}

	public void write(FriendlyByteBuf pack) {
		pack.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, bowl);
		pack.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS, fluid);
	}

	public void serializeRecipeData(JsonObject jo) {
		jo.addProperty("item", Utils.getRegistryName(bowl).toString());
		jo.addProperty("fluid", Utils.getRegistryName(fluid).toString());
	}

	public ItemStack handle(Fluid f) {
		ItemStack is = new ItemStack(bowl);
		Utils.writeItemFluid(is, f);
		return is;
	}

	public boolean matches(Fluid f) {
		return fluid == f;
	}

	public ItemStack handle(FluidStack stack) {
		ItemStack is = new ItemStack(bowl);
		Utils.writeItemFluid(is, stack);
		return is;
	}
}
