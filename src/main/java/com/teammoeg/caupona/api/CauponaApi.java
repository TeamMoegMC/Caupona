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

package com.teammoeg.caupona.api;

import java.util.Optional;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.util.SoupInfo;

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
	public static SoupInfo getInfo(ItemStack item) {
		return StewItem.getInfo(item);
	}
	public static SoupInfo getInfo(FluidStack item) {
		return SoupFluid.getInfo(item);
	}
	public static SoupInfo getInfo(CompoundTag nbt) {
		return new SoupInfo(nbt);
	}
	public static void setInfo(ItemStack item,SoupInfo info) {
		StewItem.setInfo(item,info);
	}
	public static void setInfo(FluidStack item,SoupInfo info) {
		SoupFluid.setInfo(item,info);
	}
	public static void setInfo(CompoundTag nbt,SoupInfo info) {
		info.write(nbt);
	}
	public static void applyStew(Level worldIn, LivingEntity entityLiving,SoupInfo info,ItemStack from,int cdm) {
		if (!worldIn.isClientSide) {
			for (MobEffectInstance eff :info.effects) {
				if (eff != null) {
					entityLiving.addEffect(eff);
				}
			}
			Random r=entityLiving.getRandom();
			for(Pair<MobEffectInstance, Float> ef:info.foodeffect) {
				if(r.nextFloat()<ef.getSecond())
					entityLiving.addEffect(ef.getFirst());
			}
			if (entityLiving instanceof Player) {
				Player player = (Player) entityLiving;
				player.getFoodData().eat(info.healing,info.saturation);
			}
		}
	}
	public static Optional<ItemStack> fillBowl(IFluidHandler handler) {
		FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
		if(stack.getAmount()==250)
			return fillBowl(handler.drain(250, FluidAction.EXECUTE));
		return Optional.empty();
	}
	public static Optional<ItemStack> fillBowl(FluidStack stack) {
		if(stack.getAmount() != 250)return Optional.empty();
		BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(stack.getFluid());
		if (recipe != null) {
			ItemStack ret = recipe.handle(stack);
			return Optional.of(ret);
		}
		return Optional.empty();
	}
}
