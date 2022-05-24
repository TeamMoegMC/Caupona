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

import java.util.function.BiFunction;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.ITranlatable;

import net.minecraft.resources.ResourceLocation;

public interface StewBaseCondition extends BiFunction<ResourceLocation, ResourceLocation, Integer>,Writeable,ITranlatable  {
	public JsonObject serialize();

	public String getType();
}
