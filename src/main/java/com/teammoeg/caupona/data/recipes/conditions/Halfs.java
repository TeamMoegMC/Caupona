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

import net.minecraft.network.FriendlyByteBuf;

public class Halfs extends NumberedStewCondition {
	private boolean isItem = true;

	public Halfs(JsonObject obj) {
		super(obj);
		if (obj.has("isItem"))
			isItem = obj.get("isItem").getAsBoolean();
	}

	public Halfs(StewNumber number) {
		super(number);
	}

	public Halfs(StewNumber number, boolean isItem) {
		super(number);
		this.isItem = isItem;
	}

	@Override
	public boolean test(StewPendingContext t, float n) {
		if (isItem)
			return n > t.getTotalItems() / 2;
		return n > t.getTotalTypes() / 2;
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

	public Halfs(FriendlyByteBuf buffer) {
		super(buffer);
		isItem = buffer.readBoolean();
	}

	@Override
	public String getType() {
		return "half";
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
		if (!(obj instanceof Halfs))
			return false;
		if (!super.equals(obj))
			return false;
		Halfs other = (Halfs) obj;
		if (isItem != other.isItem)
			return false;
		return true;
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("recipe.caupona.cond.half",number.getTranslation(p));
	}
}
