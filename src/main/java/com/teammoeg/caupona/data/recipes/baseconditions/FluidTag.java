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

package com.teammoeg.caupona.data.recipes.baseconditions;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class FluidTag implements StewBaseCondition {
	ResourceLocation tag;

	public FluidTag(JsonObject jo) {
		tag = new ResourceLocation(jo.get("tag").getAsString());
	}

	public FluidTag(ResourceLocation tag) {
		super();
		this.tag = tag;
	}

	@Override
	public Integer apply(ResourceLocation t, ResourceLocation u) {
		return test(u) ? 2 : test(t) ? 1 : 0;
	}

	public boolean test(ResourceLocation t) {
		Fluid f = ForgeRegistries.FLUIDS.getValue(t);
		if (f == null)
			return false;
		return Registry.FLUID.createIntrusiveHolder(f).getTagKeys().anyMatch(i->i.location().equals(tag));
	}

	public JsonObject serialize() {
		JsonObject jo = new JsonObject();
		jo.addProperty("tag", tag.toString());
		return jo;
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(tag);
	}

	public FluidTag(FriendlyByteBuf buffer) {
		tag = buffer.readResourceLocation();
	}

	@Override
	public String getType() {
		return "tag";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof FluidTag))
			return false;
		FluidTag other = (FluidTag) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslation("tag."+this.tag.toString().replaceAll("[:/]","."));
	}

}
