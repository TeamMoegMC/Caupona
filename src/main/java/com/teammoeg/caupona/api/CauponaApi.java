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

package com.teammoeg.caupona.api;

import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.DishItem;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.StewInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class CauponaApi {

	private CauponaApi() {
	}

	public static StewInfo getStewInfo(ItemStack item) {
		return StewItem.getInfo(item);
	}

	public static StewInfo getStewInfo(FluidStack item) {
		return SoupFluid.getInfo(item);
	}

	public static StewInfo getStewInfo(CompoundTag nbt) {
		return new StewInfo(nbt);
	}
	public static SauteedFoodInfo getSauteedInfo(ItemStack item) {
		return DishItem.getInfo(item);
	}

	public static SauteedFoodInfo getSauteedInfo(CompoundTag nbt) {
		return new SauteedFoodInfo(nbt);
	}
	public static void setInfo(ItemStack item, StewInfo info) {
		StewItem.setInfo(item, info);
	}

	public static void setInfo(FluidStack item, StewInfo info) {
		SoupFluid.setInfo(item, info);
	}

	public static void setInfo(CompoundTag nbt, StewInfo info) {
		info.write(nbt);
	}
	public static void setInfo(ItemStack item, SauteedFoodInfo info) {
		DishItem.setInfo(item, info);
	}
	public static void setInfo(CompoundTag nbt, SauteedFoodInfo info) {
		info.write(nbt);
	}

	public static void apply(Level worldIn, LivingEntity entityLiving, IFoodInfo info) {
		if (!worldIn.isClientSide) {
			Random r = entityLiving.getRandom();
			for (Pair<Supplier<MobEffectInstance>, Float> ef : info.getEffects()) {
				if (r.nextFloat() < ef.getSecond())
					entityLiving.addEffect(ef.getFirst().get());
			}
			if (entityLiving instanceof Player player) {
				player.getFoodData().eat(info.getHealing(), info.getSaturation());
			}
		}
	}

	public static Optional<ItemStack> fillBowl(IFluidHandler handler) {
		FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
		if (stack.getAmount() == 250)
			return fillBowl(handler.drain(250, FluidAction.EXECUTE));
		return Optional.empty();
	}

	public static Optional<ItemStack> fillBowl(FluidStack stack) {
		if (stack.getAmount() != 250)
			return Optional.empty();
		BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(stack.getFluid());
		if (recipe != null) {
			ItemStack ret = recipe.handle(stack);
			return Optional.of(ret);
		}
		return Optional.empty();
	}
}
