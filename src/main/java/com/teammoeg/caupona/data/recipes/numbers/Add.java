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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.TranslationProvider;
import com.teammoeg.caupona.data.recipes.ComplexCalculated;
import com.teammoeg.caupona.data.recipes.CookIngredients;
import com.teammoeg.caupona.data.recipes.IPendingContext;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class Add implements CookIngredients, ComplexCalculated {
	List<CookIngredients> nums;

	public Add(JsonElement jo) {
		if (jo.isJsonObject())
			nums = SerializeUtil.parseJsonElmList(jo.getAsJsonObject().get("types").getAsJsonArray(),
					SerializeUtil::ofNumber);
		else if (jo.isJsonArray())
			nums = SerializeUtil.parseJsonElmList(jo.getAsJsonArray(), SerializeUtil::ofNumber);
	}

	public Add() {
		this(new ArrayList<>());
	}

	public Add(List<CookIngredients> nums) {
		super();
		this.nums = nums;
	}

	@Override
	public Float apply(IPendingContext t) {
		/*
		 * float sum=nums.stream().map(s->{
		 * float rslt=t.compute(s);
		 * System.out.println(rslt);
		 * return rslt;
		 * }).reduce(0F,Float::sum);
		 * System.out.println(sum);
		 * return sum;
		 */
		return nums.stream().map(t::compute).reduce(0F, Float::sum);
	}

	@Override
	public boolean fits(FloatemTagStack stack) {
		return nums.stream().anyMatch(s -> s.fits(stack));
	}

	/**
	 * Convenience method for adding number<br>
	 * <b>Warning! only available in datagen environment!</b>
	 */
	public void add(CookIngredients sn) {
		nums.add(sn);
	}

	@Override
	public JsonElement serialize() {
		return SerializeUtil.toJsonList(nums, CookIngredients::serialize);
	}

	@Override
	public void write(FriendlyByteBuf buffer) {
		SerializeUtil.writeList(buffer, nums, SerializeUtil::write);
	}

	public Add(FriendlyByteBuf buffer) {
		nums = SerializeUtil.readList(buffer, SerializeUtil::ofNumber);
	}

	@Override
	public String getType() {
		return "add";
	}

	@Override
	public Stream<CookIngredients> getItemRelated() {
		return nums.stream().flatMap(CookIngredients::getItemRelated);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nums == null) ? 0 : nums.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Add))
			return false;
		Add other = (Add) obj;
		if (nums == null) {
			if (other.nums != null)
				return false;
		} else if (!nums.equals(other.nums))
			return false;
		return true;
	}

	@Override
	public Stream<ResourceLocation> getTags() {
		return nums.stream().flatMap(CookIngredients::getTags);
	}

	@Override
	public String getTranslation(TranslationProvider p) {
		return nums.stream().map(e -> e.getTranslation(p)).reduce((s1, s2) -> s1 + "+" + s2).orElse("");
	}

	@Override
	public Stream<ItemStack> getStacks() {
		return nums.stream().flatMap(CookIngredients::getStacks);
	}

}
