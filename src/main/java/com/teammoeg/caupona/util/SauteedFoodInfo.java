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

package com.teammoeg.caupona.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.SerializeUtil;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SauteedFoodInfo extends SpicedFoodInfo implements IFoodInfo{
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
		super(nbt);
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
		stacks.sort(Comparator.comparingInt(e -> Item.getId(e.stack.getItem())));
		foodeffect.sort(
				Comparator.<Pair<MobEffectInstance, Float>>comparingInt(e -> MobEffect.getId(e.getFirst().getEffect()))
						.thenComparing(Pair::getSecond));
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
				ns += fvr.sat * fs.count * fvr.heal;
				if(fvr.effects!=null)
					foodeffect.addAll(fvr.effects);
				continue;
			}
			FoodProperties f = fs.getStack().getFoodProperties(null);
			if (f != null) {
				nh += fs.count * f.getNutrition();
				ns += fs.count * f.getSaturationModifier()* f.getNutrition();
				foodeffect.addAll(f.getEffects());
			}
		}
		int conv = (int) (0.075 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		if(this.healing>0)
			this.saturation = Math.max(0.6f, ns / this.healing);
		else
			this.saturation =0;
	}

	public CompoundTag save() {
		CompoundTag nbt = new CompoundTag();
		write(nbt);
		return nbt;
	}
	public void setParts(int parts) {
		for (FloatemStack i : stacks) {
			i.count/=parts;
		}
	}
	public void addItem(ItemStack is) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.count += is.getCount();
				return;
			}
		}
		stacks.add(new FloatemStack(is.copy(), is.getCount()));
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
		super.write(nbt);
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
	@Override
	public List<FloatemStack> getStacks() {
		return stacks;
	}

	public int getHealing() {
		return healing;
	}

	public float getSaturation() {
		return saturation;
	}
	public FoodProperties getFood() {
		
		FoodProperties.Builder b = new FoodProperties.Builder();

		if (spice != null)
			b.effect(()->new MobEffectInstance(spice), 1);
		for (Pair<MobEffectInstance, Float> ef : foodeffect) {
			b.effect(()->new MobEffectInstance(ef.getFirst()), ef.getSecond());
		}
		b.nutrition(healing);
		if(Float.isNaN(saturation))
			b.saturationMod(0);
		else
			b.saturationMod(saturation);
		return b.build();
	}

	@Override
	public List<Pair<Supplier<MobEffectInstance>, Float>> getEffects() {
		List<Pair<Supplier<MobEffectInstance>, Float>> li=new ArrayList<>();
		if (spice != null)
			li.add(Pair.of(()->new MobEffectInstance(spice), 1f));
		for (Pair<MobEffectInstance, Float> ef : foodeffect) {
			li.add(Pair.of(()->new MobEffectInstance(ef.getFirst()), ef.getSecond()));
		}
		return null;
	}
}
