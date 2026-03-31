/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.caupona.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.level.material.Fluid;

public class SauteedFoodInfo extends SpicedFoodInfo implements IFoodInfo,TooltipProvider{
	public static final Codec<SauteedFoodInfo> CODEC=RecordCodecBuilder.create(o->codecStart(o)
		.and(o.group(Codec.list(FloatemStack.CODEC).fieldOf("items").forGetter(i->i.stacks),
			Codec.list(ChancedEffect.CODEC).fieldOf("effects").forGetter(i->i.foodeffect),
			Codec.INT.fieldOf("heal").forGetter(i->i.healing),
			Codec.FLOAT.fieldOf("sat").forGetter(i->i.saturation))
		).apply(o, SauteedFoodInfo::new));
	public List<FloatemStack> stacks;
	public List<ChancedEffect> foodeffect = new ArrayList<>();
	public int healing;
	public float saturation;
	
	public SauteedFoodInfo(Optional<MobEffectInstance> spice, Boolean hasSpice, Optional<Identifier> spiceName, List<FloatemStack> stacks, List<ChancedEffect> foodeffect, int healing,
		float saturation) {
		super(spice, hasSpice, spiceName);
		this.stacks = stacks;
		this.foodeffect = foodeffect;
		this.healing = healing;
		this.saturation = saturation;
	}

	public SauteedFoodInfo(List<FloatemStack> stacks, int healing, float saturation) {
		super();
		this.stacks = stacks;
		this.healing = healing;
		this.saturation = saturation;
	}

	public SauteedFoodInfo() {
		this(new ArrayList<>(), 0, 0);
	}


	public boolean isEmpty() {
		return stacks.isEmpty();
	}

	public void completeAll() {
		completeData();
	}

	public void completeData() {
		stacks.sort(Comparator.comparingInt(e -> Item.getId(e.getStack().getItem())));
		
		foodeffect.sort(
				Comparator.<ChancedEffect,String>comparing(e -> e.effect.getEffect().getRegisteredName())
						.thenComparing(e->e.chance));
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
				if(fvr.effects!=null)
					fvr.effects.stream().forEach(foodeffect::add);
				continue;
			}
			FoodProperties f = fs.getStack().getComponents().get(DataComponents.FOOD);
			Consumable c = fs.getStack().getComponents().get(DataComponents.CONSUMABLE);
			if (f != null) {
				nh += fs.count * f.nutrition();
				ns += fs.count * f.saturation();
			}
			if(c != null) {
				c.onConsumeEffects().stream().<ChancedEffect>flatMap(t->{
					if(t instanceof ApplyStatusEffectsConsumeEffect eff) {
						float chance=eff.probability();
						return eff.effects().stream().map(o->new ChancedEffect(o,chance));
					}
					return Stream.empty();
				}).forEach(foodeffect::add);
			}
		}
		int conv = (int) (0.075 * nh);
		this.healing = (int) Math.ceil(nh - conv);
		ns += conv / 2f;
		if(this.healing>0)
			this.saturation = Math.max(0.6f, ns / this.healing/2);
		else
			this.saturation =0;
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
		stacks.add(new FloatemStack(is));
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

	@Override
	public List<ChancedEffect> getEffects() {
		List<ChancedEffect> li=new ArrayList<>();
		if (spice != null)
			li.add(new ChancedEffect(new MobEffectInstance(spice), 1f));
		li.addAll(foodeffect);
		return li;
	}

	@Override
	public Fluid getBase() {
		return null;
	}

	@Override
	public Builder getFood(int extraHealing, int extraSaturation) {
		FoodProperties.Builder b = new FoodProperties.Builder();
		b.nutrition(healing+extraHealing);
		float extraSat=0;
		if(healing+extraHealing>0) {
			extraSat=extraSaturation/(healing+extraHealing);
		}
		if(Float.isNaN(saturation))
			b.saturationModifier(extraSat);
		else
			b.saturationModifier(saturation+extraSat);
		return b;
	}
	@Override
	public Consumable.Builder getConsumable() {
		Consumable.Builder b=Consumable.builder()
		.consumeSeconds(1.6F)
		.animation(ItemUseAnimation.DRINK)
		.sound(SoundEvents.GENERIC_DRINK)
		.hasConsumeParticles(true);
		if (spice != null)
			b.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(spice)));
		for (ChancedEffect ef : foodeffect) {
			ef.toPossibleEffects(b);
		}
		return b;
	}
	@Override
	public void addToTooltip(TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, DataComponentGetter components) {
		FloatemStack fs = stacks.stream()
			.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
			.orElse(null);
	if (fs != null)
		tooltipAdder.accept(Utils.translate("tooltip.caupona.main_ingredient", fs.getStack().getDisplayName()));
	Identifier rl = spiceName;
	if (rl != null)
		tooltipAdder.accept(Utils.translate("tooltip.caupona.spice",
				Utils.translate("spice." + rl.getNamespace() + "." + rl.getPath())));
	}


}
