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

public class Mainly extends NumberedStewCondition {
	public Mainly(JsonObject obj) {
		super(obj);
	}

	public Mainly(CookIngredients number) {
		super(number);
	}


	@Override
	public boolean test(IPendingContext t, float n) {
		if (n < t.getTotalItems() / 3)
			return false;
		return FloatemTagStack.calculateTypes(t.getItems().stream().filter(e -> !number.fits(e))).values().stream()
				.allMatch(e -> e < n);
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

	public Mainly(FriendlyByteBuf buffer) {
		super(buffer);
	}

	@Override
	public String getType() {
		return "mainly";
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
