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
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.serialization.Codec;
import com.teammoeg.caupona.CPMain;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class CPRecipeSerializer<T extends IDataRecipe> implements RecipeSerializer<T> {
	Codec<T> codec;
	Function<FriendlyByteBuf, T> pkfactory;
	BiConsumer<T, FriendlyByteBuf> writer;
	static final Logger logger = LogManager.getLogger(CPMain.MODID + " recipe serialize");


	@Override
	public T fromNetwork(FriendlyByteBuf buffer) {
		return pkfactory.apply(buffer);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, T recipe) {
		writer.accept(recipe, buffer);
	}

	public CPRecipeSerializer(Codec<T> codec,
			Function<FriendlyByteBuf, T> pkfactory, BiConsumer<T, FriendlyByteBuf> writer) {
		this.codec = codec;
		this.pkfactory = pkfactory;
		this.writer = writer;
	}

	@Override
	public Codec<T> codec() {
		return codec;
	}

}
