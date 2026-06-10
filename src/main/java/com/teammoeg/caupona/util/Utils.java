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

package com.teammoeg.caupona.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.components.ItemHoldedFluidData;
import com.teammoeg.caupona.components.SauteedFoodInfo;
import com.teammoeg.caupona.components.StewInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class Utils {

	public static final Direction[] horizontals = new Direction[] { Direction.EAST, Direction.WEST, Direction.SOUTH,
		Direction.NORTH };
	public static final String FLUID_TAG_KEY = "caupona:fluid";

	/*
	 * public static final Codec<MobEffectInstance>
	 * MOB_EFFECT_CODEC=RecordCodecBuilder.create(u->u.group(
	 * BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(o->o.
	 * getEffect()), Codec.INT.fieldOf("time").forGetter(o->o.getDuration()),
	 * Codec.INT.fieldOf("level").forGetter(o->o.getAmplifier())
	 * ).apply(u,MobEffectInstance::new)); public static final
	 * Codec<Pair<MobEffectInstance,Float>> MOB_EFFECT_FLOAT_CODEC=
	 * RecordCodecBuilder.create(u->u.group(
	 * BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(o->o.
	 * getFirst().getEffect()),
	 * Codec.INT.fieldOf("time").forGetter(o->o.getFirst().getDuration()),
	 * Codec.INT.fieldOf("level").forGetter(o->o.getFirst().getAmplifier()),
	 * Codec.FLOAT.fieldOf("chance").forGetter(o->o.getSecond())
	 * ).apply(u,(a,b,c,d)->Pair.of(new MobEffectInstance(a,b,c), d)));
	 */
	private Utils() {
	}

	public static <K, V> Codec<Pair<K, V>> pairCodec(String nkey, Codec<K> key, String nval, Codec<V> val) {
		return RecordCodecBuilder.create(t -> t.group(key.fieldOf(nkey).forGetter(Pair::getFirst), val.fieldOf(nval).forGetter(Pair::getSecond))
			.apply(t, Pair::of));
	}

	public static <K, V> Codec<Map<K, V>> mapCodec(Codec<K> keyCodec, Codec<V> valueCodec) {
		return Codec.compoundList(keyCodec, valueCodec).xmap(pl -> pl.stream().collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)),
			pl -> pl.entrySet().stream().map(ent -> Pair.of(ent.getKey(), ent.getValue())).toList());
	}

	public static ContanerContainFoodEvent contain(ItemResource its2, FluidResource fs, int amount) {
		ContanerContainFoodEvent ev = new ContanerContainFoodEvent(its2, fs, amount, false);
		NeoForge.EVENT_BUS.post(ev);
		return ev;
	}

	public static ContanerContainFoodEvent containBlock(ItemResource its2, FluidResource fs, int amount) {
		ContanerContainFoodEvent ev = new ContanerContainFoodEvent(its2, fs, amount, true);
		NeoForge.EVENT_BUS.post(ev);
		return ev;
	}

	public static FluidResource getFluidType(ItemStack stack) {
		ItemHoldedFluidData si = stack.get(CPCapability.ITEM_FLUID);
		if (si != null) {
			return si.getFluidType();
		}
		return Optional.ofNullable(stack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(stack))).map(t -> t.getResource(0)).orElse(FluidResource.EMPTY);
	}
	public static JsonElement toJson(Ingredient i) {
		return Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, i).result().orElse(JsonNull.INSTANCE);
	}

	public static void dropToWorld(Level level, ItemStack is, BlockPos pos, TransactionContext trans) {

		if (!is.isEmpty() && !level.isClientSide()) {
			WorldDropOperation wdo = new WorldDropOperation(level, pos);
			wdo.updateSnapshots(trans);
			wdo.addDrop(is);
		}
	}

	public static MutableComponent translate(String format, Object... objects) {
		return translateWithFallback(format, null, objects);
	}

	public static MutableComponent translate(String format) {
		return translate(format, new Object[0]);
	}

	public static MutableComponent translateWithFallback(String format, String fallback, Object... objects) {
		return MutableComponent.create(new TranslatableContents(format, fallback, objects));
	}

	public static MutableComponent translateWithFallback(String format, String fallback) {
		return translate(format, fallback, new Object[0]);
	}

	public static MutableComponent string(String content) {
		return MutableComponent.create(PlainTextContents.create(content));
	}

	public static Identifier getRegistryName(Fluid f) {
		return BuiltInRegistries.FLUID.getKey(f);
	}

	public static Identifier getRegistryName(DeferredHolder<?, ?> r) {
		return r.getId();
	}

	public static Identifier getRegistryName(Item i) {
		return BuiltInRegistries.ITEM.getKey(i);
	}

	public static Identifier getRegistryName(ItemStack i) {
		return getRegistryName(i.getItem());
	}

	public static Identifier getRegistryName(Block b) {
		return BuiltInRegistries.BLOCK.getKey(b);
	}

	public static Identifier getRegistryName(FluidStack f) {
		return getRegistryName(f.getFluid());
	}

	public static Identifier getRegistryName(MobEffect effect) {
		return BuiltInRegistries.MOB_EFFECT.getKey(effect);
	}

	public static Lazy<Item> itemSupplier(String name) {
		return Lazy.of(() -> BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath(CPMain.MODID, name)));

	}

	public static void addPotionTooltip(Collection<MobEffectInstance> list, Consumer<Component> lores, float durationFactor, Level pLevel) {
		if (list.isEmpty())
			PotionContents.addPotionTooltip(list, lores, durationFactor, pLevel == null ? 20.0F : pLevel.tickRateManager().tickrate());
	}

	public static void writeItemFluid(ItemStack is, Fluid f) {
		is.set(CPCapability.ITEM_FLUID, new ItemHoldedFluidData(FluidResource.of(f)));
	}

	public static StewInfo getOrCreateInfo(ItemStack stack) {
		StewInfo si = stack.get(CPCapability.STEW_INFO);
		if (si == null) {
			FluidResource type = Utils.getFluidType(stack);
			if (type.isEmpty())
				return new StewInfo();
			else
				return new StewInfo(type.getFluid());
		}
		return si;
	}
	public static StewInfo getOrCreateInfo(FluidStack stack) {
		StewInfo si = stack.get(CPCapability.STEW_INFO);
		if (si == null) {
			Fluid type = stack.getFluid();
			if (type == Fluids.EMPTY)
				return new StewInfo();
			else
				return new StewInfo(type);
		}
		return si.copy();
	}
	public static StewInfo getOrCreateInfoForRead(FluidResource stack) {
		StewInfo si = stack.get(CPCapability.STEW_INFO);
		if (si == null) {
			Fluid type = stack.getFluid();
			if (type == Fluids.EMPTY)
				return new StewInfo();
			else
				return new StewInfo(type);
		}
		return si;
	}

	public static StewInfo getOrCreateInfo(FluidResource stack) {
		StewInfo si = stack.get(CPCapability.STEW_INFO);
		if (si == null) {
			Fluid type = stack.getFluid();
			if (type == Fluids.EMPTY)
				return new StewInfo();
			else
				return new StewInfo(type);
		}
		return si.copy();
	}

	public static void setInfo(MutableDataComponentHolder out, StewInfo info) {

		out.set(CPCapability.STEW_INFO, info.toImmutable());
		out.set(DataComponents.CONSUMABLE, info.getConsumable().build());
		out.set(DataComponents.FOOD, info.getFood().build());

	}
	public static void setInfo(MutableDataComponentHolder out, SauteedFoodInfo info) {

		out.set(CPCapability.SAUTEED_INFO, info);
		out.set(DataComponents.CONSUMABLE, info.getConsumable().build());
		out.set(DataComponents.FOOD, info.getFood().build());

	}

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> getInterface(MutableDataComponentHolder stack, Class<T> componentClass) {
		return stack.getComponents().stream().map(t -> t.value()).filter(componentClass::isInstance).map(t -> (T) t).findAny();
	}
}
