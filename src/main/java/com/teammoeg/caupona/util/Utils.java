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

import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {

	public static final Direction[] horizontals = new Direction[] { Direction.EAST, Direction.WEST, Direction.SOUTH,
			Direction.NORTH };

	private Utils() {
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
		return MutableComponent.create(new TranslatableContents(format,objects));
	}
	public static MutableComponent translate(String format) {
		return MutableComponent.create(new TranslatableContents(format));
	}
	public static MutableComponent string(String content) {
		return MutableComponent.create(new LiteralContents(content));
	}
	public static ResourceLocation getRegistryName(Fluid f) {
		return ForgeRegistries.FLUIDS.getKey(f);
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
}
