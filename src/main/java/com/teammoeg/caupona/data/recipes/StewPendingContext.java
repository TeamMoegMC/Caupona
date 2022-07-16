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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.ResultCachingMap;
import com.teammoeg.caupona.util.SoupInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * For caching data and reduce calculation
 */
public class StewPendingContext {
	private List<FloatemTagStack> items;
	private float totalTypes;
	private float totalItems;
	private SoupInfo info;
	ResourceLocation cur;
	// cache results to prevent repeat calculation
	private ResultCachingMap<StewNumber, Float> numbers = new ResultCachingMap<>(e -> e.apply(this));
	private ResultCachingMap<StewCondition, Boolean> results = new ResultCachingMap<>(e -> e.test(this));
	private ResultCachingMap<StewBaseCondition, Integer> basetypes = new ResultCachingMap<>(
			e -> e.apply(info.base, cur));

	public ResourceLocation getCur() {
		return cur;
	}

	public StewPendingContext(SoupInfo info, ResourceLocation current) {
		this.info = info;
		items = new ArrayList<>(info.stacks.size());
		for (FloatemStack fs : info.stacks) {
			FloatemTagStack fst = new FloatemTagStack(fs);
			items.add(fst);
			totalItems += fs.getCount();
		}

		cur = current;
	}

	public float compute(StewNumber sn) {
		return numbers.compute(sn);
	}

	public boolean compute(StewCondition sc) {
		return results.compute(sc);
	}

	public int compute(StewBaseCondition sbc) {
		return basetypes.compute(sbc);
	}

	public float getOfType(ResourceLocation rl) {
		return (float) items.stream().filter(e -> e.getTags().contains(rl)).mapToDouble(FloatemTagStack::getCount)
				.sum();
	}

	public float getOfItem(Predicate<ItemStack> pred) {
		for (FloatemTagStack fs : items)
			if (pred.test(fs.getStack()))
				return fs.getCount();
		return 0f;
	}

	public float getTotalTypes() {
		return totalTypes;
	}

	public float getTotalItems() {
		return totalItems;
	}

	public SoupInfo getInfo() {
		return info;
	}

	public List<FloatemTagStack> getItems() {
		return items;
	}
}
