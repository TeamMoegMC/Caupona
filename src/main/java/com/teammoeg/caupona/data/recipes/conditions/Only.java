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

package com.teammoeg.caupona.data.recipes.conditions;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.network.FriendlyByteBuf;

public class Only extends NumberedStewCondition {
	public Only(JsonObject obj) {
		super(obj);
	}

	public Only(CookIngredients number) {
		super(number);
	}


	@Override
	public boolean test(IPendingContext t, float n) {
		return n==t.getTotalItems();
	}

	@Override
	public JsonObject serialize() {
		JsonObject jo = super.serialize();
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
	}

	public Only(FriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	public String getType() {
		return "only";
	}


	
	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.caupona.cond.mainly",number.getTranslation(p));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
}
