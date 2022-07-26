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

package com.teammoeg.caupona.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SauteedFoodInfo {
	public List<FloatemStack> stacks;
	public List<Pair<MobEffectInstance, Float>> foodeffect = new ArrayList<>();
	public int healing;
	public float saturation;

	public SauteedFoodInfo(List<FloatemStack> stacks, int healing, float saturation) {
		super();
		this.stacks = stacks;
		this.healing = healing;
		this.saturation = saturation;
	}

	public SauteedFoodInfo() {
		this(new ArrayList<>(), 0, 0);
	}

	public static List<FloatemStack> getStacks(CompoundTag nbt) {
		return nbt.getList("items", 10).stream().map(e -> (CompoundTag) e).map(FloatemStack::new)
				.collect(Collectors.toList());
	}


	public SauteedFoodInfo(CompoundTag nbt) {
		stacks = nbt.getList("items", 10).stream().map(e -> (CompoundTag) e).map(FloatemStack::new)
				.collect(Collectors.toList());
		healing = nbt.getInt("heal");
		saturation = nbt.getFloat("sat");
		foodeffect = nbt.getList("feffects", 10).stream().map(e -> (CompoundTag) e)
				.map(e -> new Pair<>(MobEffectInstance.load(e.getCompound("effect")), e.getFloat("chance")))
				.collect(Collectors.toList());
	}

	public boolean isEmpty() {
		return stacks.isEmpty();
	}
	public void completeAll() {
		completeData();
	}
	public void completeData() {
		stacks.sort(Comparator.comparingInt(e->Item.getId(e.stack.getItem())));
		foodeffect.sort(Comparator.<Pair<MobEffectInstance,Float>>comparingInt(e->MobEffect.getId(e.getFirst().getEffect())).thenComparing(Pair::getSecond));
	}
	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void recalculateHAS() {
		foodeffect.clear();
		float nh = 0;
		float ns = 0;
		for (FloatemStack fs : stacks) {
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(fs.getItem());
			if (fvr != null) {
				nh += fvr.heal * fs.count;
				ns += fvr.sat * fs.count;
				foodeffect.addAll(fvr.effects);
				continue;
			}
			FoodProperties f = fs.getItem().getFoodProperties(fs.getStack(),null);
			if (f != null) {
				nh += fs.count * f.getNutrition();
				ns += fs.count * f.getSaturationModifier();
				foodeffect.addAll(f.getEffects());
			}
		}

		this.healing = (int) Math.ceil(nh);
		this.saturation = ns;
	}


	public CompoundTag save() {
		CompoundTag nbt = new CompoundTag();
		write(nbt);
		return nbt;
	}



	public void addItem(ItemStack is, float parts) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.count += is.getCount() / parts;
				return;
			}
		}
		stacks.add(new FloatemStack(is.copy(), is.getCount() / parts));
	}

	public void addItem(FloatemStack is) {
		for (FloatemStack i : stacks) {
			if (i.equals(is.getStack())) {
				i.count += is.count;
				return;
			}
		}
		stacks.add(is);
	}

	public void write(CompoundTag nbt) {
		nbt.put("items", SerializeUtil.toNBTList(stacks, FloatemStack::serializeNBT));
		nbt.put("feffects", SerializeUtil.toNBTList(foodeffect, e -> {
			CompoundTag cnbt = new CompoundTag();
			cnbt.put("effect", e.getFirst().save(new CompoundTag()));
			cnbt.putFloat("chance", e.getSecond());
			return cnbt;
		}));
		nbt.putInt("heal", healing);
		nbt.putFloat("sat", saturation);
	}

}
