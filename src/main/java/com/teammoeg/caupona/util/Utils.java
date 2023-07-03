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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Utils {

	public static final Direction[] horizontals = new Direction[] { Direction.EAST, Direction.WEST, Direction.SOUTH,
			Direction.NORTH };

	private Utils() {
	}
	public static ItemStack extractOutput(IItemHandler inv,int count) {
		ItemStack is=ItemStack.EMPTY;
		for(int i=0;i<inv.getSlots();i++) {
			is=inv.extractItem(i, count, false);
			if(!is.isEmpty())break;
		}
		return is;
	}
	public static ItemStack insertToOutput(ItemStackHandler inv, int slot, ItemStack in) {
		ItemStack is = inv.getStackInSlot(slot);
		if (is.isEmpty()) {
			inv.setStackInSlot(slot, in.split(Math.min(inv.getSlotLimit(slot), in.getMaxStackSize())));
		} else if (ItemHandlerHelper.canItemStacksStack(in, is)) {
			int limit = Math.min(inv.getSlotLimit(slot), is.getMaxStackSize());
			limit -= is.getCount();
			limit = Math.min(limit, in.getCount());
			is.grow(limit);
			in.shrink(limit);
		}
		return in;
	}
	public static MutableComponent translate(String format,Object...objects) {
		return MutableComponent.create(new TranslatableContents(format,null,objects));
	}
	public static MutableComponent translate(String format) {
		return MutableComponent.create(new TranslatableContents(format,null,new Object[0]));
	}
	public static MutableComponent string(String content) {
		return MutableComponent.create(new LiteralContents(content));
	}
	public static ResourceLocation getRegistryName(Fluid f) {
		return ForgeRegistries.FLUIDS.getKey(f);
	}
	public static ResourceLocation getRegistryName(RegistryObject<?> r) {
		return r.getId();
	}
	public static ResourceLocation getRegistryName(Item i) {
		return ForgeRegistries.ITEMS.getKey(i);
	}
	public static ResourceLocation getRegistryName(ItemStack i) {
		return getRegistryName(i.getItem());
	}
	public static ResourceLocation getRegistryName(Block b) {
		return ForgeRegistries.BLOCKS.getKey(b);
	}

	public static ResourceLocation getRegistryName(FluidStack f) {
		return getRegistryName(f.getFluid());
	}

	public static ResourceLocation getRegistryName(MobEffect effect) {
		return ForgeRegistries.MOB_EFFECTS.getKey(effect);
	}
	public static void addPotionTooltip(List<MobEffectInstance> list, List<Component> lores, float durationFactor) {
		List<Pair<Attribute, AttributeModifier>> list1 = Lists.newArrayList();
		if (!list.isEmpty()) {
			for (MobEffectInstance effectinstance : list) {
				MutableComponent iformattabletextcomponent = translate(
						effectinstance.getDescriptionId());
				MobEffect effect = effectinstance.getEffect();
				Map<Attribute, AttributeModifier> map = effect.getAttributeModifiers();
				if (!map.isEmpty()) {
					for (Entry<Attribute, AttributeModifier> entry : map.entrySet()) {
						AttributeModifier attributemodifier = entry.getValue();
						AttributeModifier attributemodifier1 = new AttributeModifier(attributemodifier.getName(),
								effect.getAttributeModifierValue(effectinstance.getAmplifier(), attributemodifier),
								attributemodifier.getOperation());
						list1.add(new Pair<>(entry.getKey(), attributemodifier1));
					}
				}
	
				if (effectinstance.getAmplifier() > 0) {
					iformattabletextcomponent = translate("potion.withAmplifier",
							iformattabletextcomponent,
							translate("potion.potency." + effectinstance.getAmplifier()));
				}
	
				if (effectinstance.getDuration() > 20) {
					iformattabletextcomponent = translate("potion.withDuration",
							iformattabletextcomponent, MobEffectUtil.formatDuration(effectinstance, durationFactor));
				}
	
				lores.add(iformattabletextcomponent.withStyle(effect.getCategory().getTooltipFormatting()));
			}
		}
	
		if (!list1.isEmpty()) {
			lores.add(Component.empty());
			lores.add((translate("potion.whenDrank")).withStyle(ChatFormatting.DARK_PURPLE));
	
			for (Pair<Attribute, AttributeModifier> pair : list1) {
				AttributeModifier attributemodifier2 = pair.getSecond();
				double d0 = attributemodifier2.getAmount();
				double d1;
				if (attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE
						&& attributemodifier2.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
					d1 = attributemodifier2.getAmount();
				} else {
					d1 = attributemodifier2.getAmount() * 100.0D;
				}
	
				if (d0 > 0.0D) {
					lores.add((translate(
							"attribute.modifier.plus." + attributemodifier2.getOperation().toValue(),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
							translate(pair.getFirst().getDescriptionId())))
									.withStyle(ChatFormatting.BLUE));
				} else if (d0 < 0.0D) {
					d1 = d1 * -1.0D;
					lores.add((translate(
							"attribute.modifier.take." + attributemodifier2.getOperation().toValue(),
							ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(d1),
							translate(pair.getFirst().getDescriptionId())))
									.withStyle(ChatFormatting.RED));
				}
			}
		}
	
	}
}
