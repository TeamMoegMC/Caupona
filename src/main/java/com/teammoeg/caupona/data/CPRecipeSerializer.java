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

package com.teammoeg.caupona.data;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.Main;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CPRecipeSerializer<T extends IDataRecipe>
		extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {
	BiFunction<ResourceLocation, JsonObject, T> jsfactory;
	BiFunction<ResourceLocation, FriendlyByteBuf, T> pkfactory;
	BiConsumer<T, FriendlyByteBuf> writer;
	static final Logger logger = LogManager.getLogger(Main.MODID + " recipe serialize");

	@Override
	public T fromJson(ResourceLocation recipeId, JsonObject json) {
		try {
			return jsfactory.apply(recipeId, json);
		} catch (InvalidRecipeException e) {
			return null;
		}
	}

	@Override
	public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
		return pkfactory.apply(recipeId, buffer);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, T recipe) {
		writer.accept(recipe, buffer);
	}

	public CPRecipeSerializer(BiFunction<ResourceLocation, JsonObject, T> jsfactory,
			BiFunction<ResourceLocation, FriendlyByteBuf, T> pkfactory, BiConsumer<T, FriendlyByteBuf> writer) {
		this.jsfactory = jsfactory;
		this.pkfactory = pkfactory;
		this.writer = writer;
	}

}
