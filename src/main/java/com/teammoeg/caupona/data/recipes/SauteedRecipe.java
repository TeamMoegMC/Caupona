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

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.CPTags.Items;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.recipes.conditions.Conditions;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class SauteedRecipe extends IDataRecipe implements IConditionalRecipe {
	public static Set<CookIngredients> cookables;
	public static List<RecipeHolder<SauteedRecipe>> sorted;
	public static DeferredHolder<?,RecipeType<Recipe<?>>> TYPE;
	public static DeferredHolder<?,RecipeSerializer<?>> SERIALIZER;
	public static boolean isCookable(ItemStack stack) {
		FloatemTagStack s = new FloatemTagStack(stack);
		return stack.is(Items.COOKABLE) || cookables.stream().anyMatch(e -> e.fits(s));
		// return true;
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
	public Item output;
	public boolean removeNBT=false;
	public float count=2f;
	public SauteedRecipe(ResourceLocation id) {
		super(id);
	}

	public SauteedRecipe(ResourceLocation id, JsonObject data) {
		super(id);
		if (data.has("allow")) {
			allow = SerializeUtil.parseJsonList(data.get("allow"), Conditions::of);
			Conditions.checkConditions(allow);
		}
		if (data.has("deny")) {
			deny = SerializeUtil.parseJsonList(data.get("deny"), Conditions::of);
			Conditions.checkConditions(deny);
		}
		if (data.has("priority"))
			priority = data.get("priority").getAsInt();
		time = data.get("time").getAsInt();
		output = BuiltInRegistries.ITEM.get(new ResourceLocation(data.get("output").getAsString()));
		if (output == null)
			throw new InvalidRecipeException();
		if(data.has("removeNBT"))
			removeNBT=data.get("removeNBT").getAsBoolean();
		if(data.has("ingredientPerDish"))
			count=data.get("ingredientPerDish").getAsFloat();
	}

	public SauteedRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		allow = SerializeUtil.readList(data, Conditions::of);
		deny = SerializeUtil.readList(data, Conditions::of);
		priority = data.readVarInt();
		time = data.readVarInt();
		output = data.readById(BuiltInRegistries.ITEM);
		removeNBT=data.readBoolean();
		count=data.readFloat();
	}
	public SauteedRecipe(ResourceLocation id, List<IngredientCondition> allow, List<IngredientCondition> deny,
			int priority, int time, Item output,boolean removeNBT,float count) {
		super(id);
		this.allow = allow;
		this.deny = deny;
		this.priority = priority;
		this.time = time;
		this.output = output;
		this.removeNBT=removeNBT;
		this.count=count;
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.writeList(data, allow, Conditions::write);
		SerializeUtil.writeList(data, deny, Conditions::write);
		data.writeVarInt(priority);
		data.writeVarInt(time);
		data.writeId(BuiltInRegistries.ITEM, output);
		data.writeBoolean(removeNBT);
		data.writeFloat(count);
	}

	public boolean matches(PanPendingContext ctx) {
		if (allow != null)
			if (!allow.stream().allMatch(ctx::compute))
				return false;
		if (deny != null)
			if (deny.stream().anyMatch(ctx::compute))
				return false;
		return true;
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
		json.addProperty("time", time);
		json.addProperty("output", Utils.getRegistryName(output).toString());
		if(removeNBT)
			json.addProperty("removeNBT",removeNBT);
		json.addProperty("ingredientPerDish", count);
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

	public List<IngredientCondition> getAllow() {
		return allow;
	}

	public List<IngredientCondition> getDeny() {
		return deny;
	}

}
