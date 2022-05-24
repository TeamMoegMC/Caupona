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
import com.teammoeg.caupona.data.recipes.StewNumber;
import com.teammoeg.caupona.data.recipes.StewPendingContext;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.network.FriendlyByteBuf;

public class Mainly extends NumberedStewCondition {
	private boolean isItem = true;

	public Mainly(JsonObject obj) {
		super(obj);
		if (obj.has("isItem"))
			isItem = obj.get("isItem").getAsBoolean();
	}

	public Mainly(StewNumber number) {
		super(number);
	}

	public Mainly(StewNumber number, boolean isItem) {
		this(number);
		this.isItem = isItem;
	}

	@Override
	public boolean test(StewPendingContext t, float n) {
		if (isItem) {
			if (n < t.getTotalItems() / 3)
				return false;
		} else if (n < t.getTotalTypes() / 3)
			return false;
		return FloatemTagStack.calculateTypes(t.getItems().stream().filter(e -> !number.fits(e))).values().stream()
				.allMatch(e -> e < n);
	}

	@Override
	public JsonObject serialize() {
		JsonObject jo = super.serialize();
		if (!isItem)
			jo.addProperty("isItem", isItem);
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		super.write(buffer);
		buffer.writeBoolean(isItem);
	}

	public Mainly(FriendlyByteBuf buffer) {
		super(buffer);
		isItem = buffer.readBoolean();
	}

	@Override
	public String getType() {
		return "mainly";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isItem ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Mainly))
			return false;
		if (!super.equals(obj))
			return false;

		Mainly other = (Mainly) obj;
		if (isItem != other.isItem)
			return false;
		return true;
	}
	
	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.caupona.cond.mainly",number.getTranslation(p));
	}
}
