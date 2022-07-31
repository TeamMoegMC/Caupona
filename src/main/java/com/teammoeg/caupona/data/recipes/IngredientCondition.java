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

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.ITranlatable;
import com.teammoeg.caupona.data.Writeable;

import net.minecraft.resources.ResourceLocation;

public interface IngredientCondition extends Predicate<IPendingContext>, Writeable, ITranlatable {
	public JsonObject serialize();

	public String getType();

	public default Stream<CookIngredients> getAllNumbers() {
		return Stream.empty();
	};

	public default Stream<ResourceLocation> getTags() {
		return Stream.empty();
	};

}
