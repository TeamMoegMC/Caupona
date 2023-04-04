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

package com.teammoeg.caupona.data.recipes.numbers;

import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemTag implements CookIngredients {

	ResourceLocation tag;

	public ItemTag(JsonElement jo) {
		if (jo.isJsonObject())
			tag = new ResourceLocation(jo.getAsJsonObject().get("tag").getAsString());
		else
			tag = new ResourceLocation(jo.getAsString());
	}

	public ItemTag(ResourceLocation tag) {
		super();
		this.tag = tag;
	}

	@Override
	public Float apply(IPendingContext t) {
		return t.getOfType(tag);
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
		return stack.getTags().contains(tag);
	}

	@Override
	public JsonElement serialize() {
		return new JsonPrimitive(tag.toString());
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(tag);
	}

	public ItemTag(FriendlyByteBuf buffer) {
		tag = buffer.readResourceLocation();
	}

	@Override
	public String getType() {
		return "tag";
	}

	@Override
	public Stream<CookIngredients> getItemRelated() {
		return Stream.of(this);
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
		if (!(obj instanceof ItemTag))
			return false;
		ItemTag other = (ItemTag) obj;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return Stream.of(tag);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return p.getTranslationOrElse("tag." + this.tag.toLanguageKey().replaceAll("/", "."),"#"+this.tag.toString());
	}

	@Override
	public Stream<ItemStack> getStacks() {
		return ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(tag)).stream().map(ItemStack::new);
	}
}
