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

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.util.ChancedEffect;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.SerializeUtil;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.Lazy;

public class StewInfo extends SpicedFoodInfo implements IFoodInfo,TooltipProvider {
	public static final Codec<ImmutableStewInfo> CODEC=RecordCodecBuilder.create(t->t.group(
		spiceCodec(),
		hasSpiceCodec(),
		spiceNameCodec(),
		Codec.list(FloatemStack.CODEC).fieldOf("items").forGetter(o->o.stacks),
		Codec.list(ChancedEffect.CODEC).fieldOf("effects").forGetter(o->o.effects),
		Codec.list(ChancedEffect.CODEC).fieldOf("feffects").forGetter(o->o.foodeffect),
		Codec.INT.fieldOf("heal").forGetter(o->o.healing),
		Codec.FLOAT.fieldOf("sat").forGetter(o->o.saturation),
		SerializeUtil.idOrKey(BuiltInRegistries.FLUID).fieldOf("base").forGetter(o->o.base)
		).apply(t, ImmutableStewInfo::new));
	public static final Codec<StewInfo> COPY_ON_WRITE_CODEC=CODEC.xmap(t->t.copy(), t->t.toImmutable());
	
	
	protected List<FloatemStack> stacks;
	protected List<ChancedEffect> effects;
	protected List<ChancedEffect> foodeffect = new ArrayList<>();
	protected int healing;
	protected float saturation;
	protected Fluid base;
	
	public StewInfo(Optional<MobEffectInstance> spice, Boolean hasSpice, Optional<Identifier> spiceName, List<FloatemStack> stacks, List<ChancedEffect> effects,
		List<ChancedEffect> foodeffect, int healing, float saturation, Fluid base) {
		super(spice, hasSpice, spiceName);
		this.stacks = stacks;
		this.effects = effects;
		this.foodeffect = foodeffect;
		this.healing = healing;
		this.saturation = saturation;
		this.base=base;
	}
	public StewInfo(List<FloatemStack> stacks, List<ChancedEffect> effects, int healing, float saturation,
		Fluid base) {
		super();
		this.stacks = stacks;
		this.effects = effects;
		this.healing = healing;
		this.saturation = saturation;
		this.base=base;
	}

	public StewInfo(Fluid fluid) {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, fluid);
	}
	public StewInfo() {
		this(new ArrayList<>(), new ArrayList<>(), 0, 0, Fluids.WATER);
	}
	public StewInfo(MobEffectInstance spice, Boolean hasSpice, Identifier spiceName, List<FloatemStack> stacks, List<ChancedEffect> effects,
		List<ChancedEffect> foodeffect, int healing, float saturation, Fluid base) {
		super(spice, hasSpice, spiceName);
		this.stacks = stacks;
		this.effects = effects;
		this.foodeffect = foodeffect;
		this.healing = healing;
		this.saturation = saturation;
		this.base=base;
	}
	public StewInfo copy() {
		return new StewInfo(spice,hasSpice,spiceName,stacks.stream().map(t->t.copy()).collect(Collectors.toList()),effects.stream().map(t->t.copy()).collect(Collectors.toList()),foodeffect.stream().map(t->t.copy()).collect(Collectors.toList()),healing,saturation,base);
	}
	public ImmutableStewInfo toImmutable() {
		return new ImmutableStewInfo(this);
	}
	
	public float getDensity() {
		return stacks.stream().map(FloatemStack::getCount).reduce(0f, Float::sum);
	}

	public boolean canAlwaysEat() {
		return healing <= 1 || getDensity() <= 0.5;
	}

	public boolean isEmpty() {
		return stacks.isEmpty() && effects.isEmpty();
	}

	public boolean canMerge(StewInfo f, float cparts, float oparts) {
		return (this.getDensity() * cparts + f.getDensity() * oparts) / (cparts + oparts) <= 3;
	}

	public boolean merge(StewInfo f, float cparts, float oparts) {
		if (!canMerge(f, cparts, oparts))
			return false;
		forceMerge(f, cparts, oparts);
		return true;
	}

	public void forceMerge(StewInfo f, float cparts, float oparts) {

		for (ChancedEffect es : f.effects) {
			boolean added = false;
			for (ChancedEffect oes : effects) {
				if (oes.merge(es, cparts, oparts)) {
					added = true;
					break;
				}
			}
			if (!added) {
				if (effects.size() < 3) {
					ChancedEffect copy=es.copy();
					copy.adjustParts(oparts, cparts);
					effects.add(copy);
				}
			}
		}
		for (ChancedEffect es : f.foodeffect) {
			boolean added = false;
			for (ChancedEffect oes : foodeffect) {
				if (oes.merge(es, oparts, cparts)) {
		
					added = true;
					break;
				}
			}
			if (!added) {
				foodeffect.add(es);
			}
		}
		for (FloatemStack fs : f.stacks) {
			this.addItem(fs.copyWithCount(fs.count * oparts / cparts));
		}
		completeAll();
	}

	public void completeAll() {
		clearSpice();
		completeData();
		completeEffects();
	}

	public void completeData() {
		stacks.removeIf(t->t.isEmpty());
		stacks.sort(Comparator.comparingInt(e -> Item.getId(e.getStack().getItem())));
		foodeffect.sort(null);
	}

	public void completeEffects() {
		effects.sort(null);
	}

	public static boolean isEffectEquals(MobEffectInstance t1, MobEffectInstance t2) {
		return t1.getEffect() == t2.getEffect() && t1.getAmplifier() == t2.getAmplifier();
	}

	public void addEffect(MobEffectInstance eff, float parts) {

		for (ChancedEffect oes : effects) {
			if (oes.add(eff, parts)) {
				return;
			}
		}
		if (effects.size() < 3) {
			effects.add(ChancedEffect.createByParts(eff,parts));
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
			if(c!=null) {
				c.onConsumeEffects().stream().<ChancedEffect>flatMap(t->{
					if(t instanceof ApplyStatusEffectsConsumeEffect eff) {
						float chance=eff.probability();
						return eff.effects().stream().map(o->new ChancedEffect(o,chance));
					}
					return Stream.empty();
				}).forEach(foodeffect::add);
			}
		}
		RecipeHolder<FluidFoodValueRecipe> ffvr = FluidFoodValueRecipe.recipes.get(this.base);
		if (ffvr != null) {
			nh += ffvr.value().heal * (1);
			ns += ffvr.value().sat * (1);
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
		if(this.healing>0)
			this.saturation = Math.max(0.7f, ns / this.healing/2);
		else
			this.saturation=0;
	}

	public void adjustParts(float oparts, float parts) {
		if (oparts == parts)
			return;
		for (FloatemStack fs : stacks) {
			fs.setCount(fs.getCount() * oparts / parts);
		}
		for (ChancedEffect es : effects) {
			
			es.adjustParts(oparts, parts);
		}
		for (ChancedEffect es : foodeffect) {
			es.adjustParts(oparts, parts);
		}
		/*float delta = 0;
		if (oparts > parts)
			delta = oparts - parts;*/
		clearSpice();
		healing = (int) (healing * oparts / parts);
		
		saturation = saturation * oparts / parts;
	}


	public void addItem(ItemStack is, float parts) {
		for (FloatemStack i : stacks) {
			if (i.equals(is)) {
				i.count += is.getCount() / parts;
				return;
			}
		}
		stacks.add(new FloatemStack(is, is.getCount() / parts));
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
		li.addAll(effects);
		if (spice != null)
			li.add(new ChancedEffect(new MobEffectInstance(spice), 1f));
		li.addAll(foodeffect);
		return null;
	}
	private Lazy<Collection<MobEffectInstance>> potionEffectsCollectionView=Lazy.of(()->new AbstractCollection<MobEffectInstance>() {
		@Override
		public Iterator<MobEffectInstance> iterator() {
			return effects.stream().map(t->t.effect).iterator();
		}
		@Override
		public int size() {
			return effects.size();
		}
		@Override
		public boolean isEmpty() {
			return effects.isEmpty();
		}
		@Override
		public Object[] toArray() {
			return effects.stream().map(t->t.effect).toArray();
		}
		@Override
		public boolean add(MobEffectInstance e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean addAll(Collection<? extends MobEffectInstance> c) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
	});
	public Collection<MobEffectInstance> getPotionEffects() {
		return potionEffectsCollectionView.get();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(base, effects, foodeffect, healing, saturation, stacks);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		StewInfo other = (StewInfo) obj;
		return Objects.equals(base, other.base) && Objects.equals(effects, other.effects) && Objects.equals(foodeffect, other.foodeffect) && healing == other.healing
			&& Float.floatToIntBits(saturation) == Float.floatToIntBits(other.saturation) && Objects.equals(stacks, other.stacks);
	}
	@Override
	public Fluid getBase() {
		return base;
	}
	public void setBase(Fluid base) {
		this.base = base;
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
		if (canAlwaysEat())
			b.alwaysEdible();
		return b;
	}

	@Override
	public Consumable.Builder getConsumable() {
		Consumable.Builder b=Consumable.builder()
		.consumeSeconds(1.6F)
		.animation(ItemUseAnimation.DRINK)
		.sound(SoundEvents.GENERIC_DRINK)
		.hasConsumeParticles(true);
		for (ChancedEffect eff : effects) {
			eff.toPossibleEffects(b);
		}
		if (spice != null)
			b.onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(spice)));
		for (ChancedEffect ef : foodeffect) {
			ef.toPossibleEffects(b);
		}
		return b;
	}
	@Override
	public void addToTooltip(TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, DataComponentGetter components) {
		FloatemStack fs = getStacks().stream()
			.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
			.orElse(null);
	if (fs != null)
		tooltipAdder.accept(Utils.translate("tooltip.caupona.main_ingredient", fs.getStack().getDisplayName()));
	Identifier rl = spiceName;
	if (rl != null)
		tooltipAdder.accept(Utils.translate("tooltip.caupona.spice",
				Utils.translate("spice." + rl.getNamespace() + "." + rl.getPath())));
	;
	Fluid base = getBase();
	if (base != null&&!getStacks().isEmpty())
		tooltipAdder.accept(Utils.translate("tooltip.caupona.base", 
				base.getFluidType().getDescription()));
	if(!getPotionEffects().isEmpty())
		PotionContents.addPotionTooltip(getPotionEffects(), tooltipAdder, 1,20);
	}


}
