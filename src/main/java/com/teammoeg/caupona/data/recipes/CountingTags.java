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
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class CountingTags extends IDataRecipe {
	public static Set<ResourceLocation> tags;
	public static RecipeType<?> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public List<ResourceLocation> tag;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}

	public CountingTags(ResourceLocation id) {
		super(id);
		tag = new ArrayList<>();
	}

	public CountingTags(ResourceLocation id, JsonObject jo) {
		super(id);
		if (jo.has("tag"))
			tag = ImmutableList.of(new ResourceLocation(jo.get("tag").getAsString()));
		else if (jo.has("tags"))
			tag = SerializeUtil.parseJsonElmList(jo.get("tags"), e -> new ResourceLocation(e.getAsString()));
	}

	public CountingTags(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		tag = SerializeUtil.readList(data, FriendlyByteBuf::readResourceLocation);
	}

	public void write(FriendlyByteBuf data) {
		SerializeUtil.<ResourceLocation>writeList2(data, tag, FriendlyByteBuf::writeResourceLocation);
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.add("tags", SerializeUtil.toJsonList(tag, e -> new JsonPrimitive(e.toString())));
	}

}
