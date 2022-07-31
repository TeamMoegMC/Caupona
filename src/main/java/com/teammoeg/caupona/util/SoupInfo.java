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

public class SoupInfo extends SpicedFoodInfo {
	public List<FloatemStack> stacks;
	public List<MobEffectInstance> effects;
	public List<Pair<MobEffectInstance, Float>> foodeffect = new ArrayList<>();
	public int healing;
	public float saturation;
	public float shrinkedFluid = 0;
	public ResourceLocation base;

	public SoupInfo(List<FloatemStack> stacks, List<MobEffectInstance> effects, int healing, float saturation,
			ResourceLocation base) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.healing = healing;
		this.saturation = saturation;
		this.base = base;
	}

	public SoupInfo() {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, new ResourceLocation("minecraft:water"));
	}

	public static List<FloatemStack> getStacks(CompoundTag nbt) {
		return nbt.getList("items", 10).stream().map(e -> (CompoundTag) e).map(FloatemStack::new)
				.collect(Collectors.toList());
	}


	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}

	public boolean canAlwaysEat() {
		return healing <= 1 || getDensity() <= 0.5;
	}

	public SoupInfo(CompoundTag nbt) {
		super(nbt);
		stacks = nbt.getList("items", 10).stream().map(e -> (CompoundTag) e).map(FloatemStack::new)
				.collect(Collectors.toList());
		effects = nbt.getList("effects", 10).stream().map(e -> (CompoundTag) e).map(MobEffectInstance::load)
				.collect(Collectors.toList());
		healing = nbt.getInt("heal");
		saturation = nbt.getFloat("sat");
		foodeffect = nbt.getList("feffects", 10).stream().map(e -> (CompoundTag) e)
				.map(e -> new Pair<>(MobEffectInstance.load(e.getCompound("effect")), e.getFloat("chance")))
				.collect(Collectors.toList());
		base = new ResourceLocation(nbt.getString("base"));

		shrinkedFluid = nbt.getFloat("afluid");
	}

	public boolean isEmpty() {
		return stacks.isEmpty() && effects.isEmpty();
	}

	public boolean canMerge(SoupInfo f, float cparts, float oparts) {
		return (this.getDensity() * cparts + f.getDensity() * oparts) / (cparts + oparts) <= 3;
	}

	public boolean merge(SoupInfo f, float cparts, float oparts) {
		if (!canMerge(f, cparts, oparts))
			return false;
		forceMerge(f, cparts, oparts);
		return true;
	}

	public void forceMerge(SoupInfo f, float cparts, float oparts) {

		for (MobEffectInstance es : f.effects) {
			boolean added = false;
			for (MobEffectInstance oes : effects) {
				if (isEffectEquals(oes, es)) {
					oes.duration += es.duration * oparts / cparts;
					added = true;
					break;
				}
			}
			if (!added) {
				if (effects.size() < 3)
					effects.add(es);
			}
		}
		for (Pair<MobEffectInstance, Float> es : f.foodeffect) {
			boolean added = false;
			for (Pair<MobEffectInstance, Float> oes : foodeffect) {
				if (es.getSecond() == oes.getSecond() && isEffectEquals(oes.getFirst(), es.getFirst())) {
					oes.getFirst().duration += es.getFirst().duration * oparts / cparts;
					added = true;
					break;
				}
			}
			if (!added) {
				foodeffect.add(es);
			}
		}
		shrinkedFluid += f.shrinkedFluid * oparts / cparts;
		for (FloatemStack fs : f.stacks) {
			this.addItem(new FloatemStack(fs.getStack(), fs.count * oparts / cparts));
		}
		completeAll();
	}

	public void completeAll() {
		clearSpice();
		completeData();
		completeEffects();
	}

	public void completeData() {
		stacks.sort(Comparator.comparingInt(e -> Item.getId(e.stack.getItem())));
		foodeffect.sort(
				Comparator.<Pair<MobEffectInstance, Float>>comparingInt(e -> MobEffect.getId(e.getFirst().getEffect()))
						.thenComparing(Pair::getSecond));
	}

	public void completeEffects() {
		effects.sort(Comparator.<MobEffectInstance>comparingInt(x -> MobEffect.getId(x.getEffect()))
				.thenComparingInt(e -> e.getDuration()));
	}

	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void addEffect(MobEffectInstance eff, float parts) {

		for (MobEffectInstance oes : effects) {
			if (isEffectEquals(oes, eff)) {
				oes.duration = Math.max(oes.duration,
						(int) Math.min(oes.duration + eff.duration / parts, eff.duration * 2f));
				return;
			}
		}
		if (effects.size() < 3) {
			MobEffectInstance copy = new MobEffectInstance(eff);
			copy.duration /= parts;
			effects.add(copy);
		}
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
				foodeffect.addAll(fvr.effects);
				continue;
			}
			FoodProperties f = fs.getStack().getFoodProperties(null);
			if (f != null) {
				nh += fs.count * f.getNutrition();
				ns += fs.count * f.getSaturationModifier();
				foodeffect.addAll(f.getEffects());
			}
		}
		FluidFoodValueRecipe ffvr = FluidFoodValueRecipe.recipes.get(this.base);
		if (ffvr != null) {
			nh += ffvr.heal * (1 + this.shrinkedFluid);
			ns += ffvr.sat * (1 + this.shrinkedFluid);
		}
		float dense = this.getDensity();
		/*
		 * if(nh>0) {
		 * nh+=Mth.clamp(dense,1,2);
		 * }
		 */
		int conv = (int) (Mth.clamp((dense - 1) / 2f, 0, 1) * 0.3 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		this.saturation = Math.max(0.7f, ns / this.healing);
	}

	public void adjustParts(float oparts, float parts) {
		if (oparts == parts)
			return;
		for (FloatemStack fs : stacks) {
			fs.setCount(fs.getCount() * oparts / parts);
		}

		for (MobEffectInstance es : effects) {
			es.duration = (int) (es.duration * oparts / parts);
		}
		for (Pair<MobEffectInstance, Float> es : foodeffect) {
			es.getFirst().duration = (int) (es.getFirst().duration * oparts / parts);
		}
		float delta = 0;
		if (oparts > parts)
			delta = oparts - parts;
		clearSpice();
		shrinkedFluid = (shrinkedFluid * oparts + delta) / parts;
		healing = (int) (healing * oparts / parts);
		saturation = saturation * oparts / parts;
	}

	public SoupInfo(ResourceLocation base) {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, base);
	}

	public static String getRegName(CompoundTag nbt) {
		return nbt.getString("base");
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
		super.write(nbt);
		nbt.put("items", SerializeUtil.toNBTList(stacks, FloatemStack::serializeNBT));
		nbt.put("effects", SerializeUtil.toNBTList(effects, e -> e.save(new CompoundTag())));
		nbt.put("feffects", SerializeUtil.toNBTList(foodeffect, e -> {
			CompoundTag cnbt = new CompoundTag();
			cnbt.put("effect", e.getFirst().save(new CompoundTag()));
			cnbt.putFloat("chance", e.getSecond());
			return cnbt;
		}));
		nbt.putInt("heal", healing);
		nbt.putFloat("sat", saturation);
		nbt.putString("base", base.toString());
		nbt.putFloat("afluid", shrinkedFluid);

	}

}
