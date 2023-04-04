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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.recipes.conditions.Halfs;
import com.teammoeg.caupona.data.recipes.conditions.Mainly;
import com.teammoeg.caupona.data.recipes.conditions.Only;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StewCookingRecipe extends IDataRecipe implements IConditionalRecipe {
	public static Set<CookIngredients> cookables;
	public static Map<Fluid, StewCookingRecipe> recipes;
	public static List<StewCookingRecipe> sorted;
	public static RegistryObject<RecipeType<Recipe<?>>> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static final TagKey<Item> cookable = ItemTags.create(new ResourceLocation(Main.MODID, "cookable"));
	public static final TagKey<Fluid> boilable = FluidTags.create(new ResourceLocation(Main.MODID, "boilable"));

	public static boolean isCookable(ItemStack stack) {
		FloatemTagStack s = new FloatemTagStack(stack);
		return stack.is(cookable) || cookables.stream().anyMatch(e -> e.fits(s));
		// return true;
	}

	@SuppressWarnings("deprecation")
	public static boolean isBoilable(FluidStack f) {
		Fluid fd = f.getFluid();
		return fd instanceof SoupFluid || f.getFluid().is(boilable) || recipes.keySet().contains(fd);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE.get();
	}

	List<IngredientCondition> allow;
	List<IngredientCondition> deny;
	int priority = 0;
	public int time;
	float density;
	List<StewBaseCondition> base;
	public Fluid output;
	public boolean removeNBT=false;
	public StewCookingRecipe(ResourceLocation id) {
		super(id);
	}

	public StewCookingRecipe(ResourceLocation id, JsonObject data) {
		super(id);
		if (data.has("allow")) {
			allow = SerializeUtil.parseJsonList(data.get("allow"), SerializeUtil::ofCondition);
			SerializeUtil.checkConditions(allow);
		}
		if (data.has("deny")) {
			deny = SerializeUtil.parseJsonList(data.get("deny"), SerializeUtil::ofCondition);
			SerializeUtil.checkConditions(deny);
		}
		if (data.has("priority"))
			priority = data.get("priority").getAsInt();
		if (data.has("density"))
			density = data.get("density").getAsFloat();
		time = data.get("time").getAsInt();
		if (data.has("base"))
			base = SerializeUtil.parseJsonList(data.get("base"), SerializeUtil::ofBase);
		output = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(data.get("output").getAsString()));
		if (output == Fluids.EMPTY)
			throw new InvalidRecipeException();
		if(data.has("removeNBT"))
			removeNBT=data.get("removeNBT").getAsBoolean();
	}

	public StewCookingRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		allow = SerializeUtil.readList(data, SerializeUtil::ofCondition);
		deny = SerializeUtil.readList(data, SerializeUtil::ofCondition);
		priority = data.readVarInt();
		density = data.readFloat();
		time = data.readVarInt();
		base = SerializeUtil.readList(data, SerializeUtil::ofBase);
		output = data.readRegistryIdUnsafe(ForgeRegistries.FLUIDS);
		removeNBT=data.readBoolean();
	}

	public StewCookingRecipe(ResourceLocation id, List<IngredientCondition> allow, List<IngredientCondition> deny,
			int priority, int time, float density, List<StewBaseCondition> base, Fluid output,boolean removeNBT) {
		super(id);
		this.allow = allow;
		this.deny = deny;
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.base = base;
		this.output = output;
		this.removeNBT=removeNBT;
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, allow, SerializeUtil::write);
		SerializeUtil.writeList(data, deny, SerializeUtil::write);
		data.writeVarInt(priority);
		data.writeFloat(density);
		data.writeVarInt(time);
		SerializeUtil.writeList(data, base, SerializeUtil::write);
		data.writeRegistryIdUnsafe(ForgeRegistries.FLUIDS,output);
		data.writeBoolean(removeNBT);
	}

	public int matches(StewPendingContext ctx) {
		if (ctx.getTotalItems() < density)
			return 0;
		int matchtype = 0;
		if (base != null) {
			for (StewBaseCondition e : base) {
				matchtype = ctx.compute(e);
				if (matchtype != 0)
					break;
			}
			if (matchtype == 0)
				return 0;
		}
		if (matchtype == 0)
			matchtype = 1;
		if (allow != null)
			if (!allow.stream().allMatch(ctx::compute))
				return 0;
		if (deny != null)
			if (deny.stream().anyMatch(ctx::compute))
				return 0;
		return matchtype;
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		if (allow != null && !allow.isEmpty()) {
			json.add("allow", SerializeUtil.toJsonList(allow, IngredientCondition::serialize));
		}
		if (deny != null && !deny.isEmpty()) {
			json.add("deny", SerializeUtil.toJsonList(deny, IngredientCondition::serialize));
		}
		if (priority != 0)
			json.addProperty("priority", priority);
		json.addProperty("density", density);
		json.addProperty("time", time);
		if (base != null && !base.isEmpty()) {
			json.add("base", SerializeUtil.toJsonList(base, StewBaseCondition::serialize));
		}
		json.addProperty("output",Utils.getRegistryName(output).toString());
		if(removeNBT)
			json.addProperty("removeNBT",removeNBT);
	}

	public Stream<CookIngredients> getAllNumbers() {
		return Stream.concat(
				allow == null ? Stream.empty() : allow.stream().flatMap(IngredientCondition::getAllNumbers),
				deny == null ? Stream.empty() : deny.stream().flatMap(IngredientCondition::getAllNumbers));
	}

	public Stream<ResourceLocation> getTags() {
		return Stream.concat(allow == null ? Stream.empty() : allow.stream().flatMap(IngredientCondition::getTags),
				deny == null ? Stream.empty() : deny.stream().flatMap(IngredientCondition::getTags));
	}

	public int getPriority() {
		return priority;
	}

	public List<StewBaseCondition> getBase() {
		return base;
	}

	@Override
	public List<IngredientCondition> getAllow() {
		return allow;
	}

	@Override
	public List<IngredientCondition> getDeny() {
		return deny;
	}

	public float getDensity() {
		return density;
	}

}
