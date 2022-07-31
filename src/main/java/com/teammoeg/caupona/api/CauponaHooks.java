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

import java.util.List;
import java.util.Optional;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.SoupInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class CauponaHooks {

	private CauponaHooks() {
	}

	public static final ResourceLocation stew = new ResourceLocation(Main.MODID, "stews");

	public static Optional<List<FloatemStack>> getItems(ItemStack stack) {
		if (stack.getItem() instanceof StewItem) {
			return Optional.of(StewItem.getItems(stack));
		}
		LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
		if (cap.isPresent()) {
			IFluidHandlerItem data = cap.resolve().get();
			FluidStack fs = data.getFluidInTank(0);
			// TODO: CHECK STEW TAG
			return Optional.of(SoupFluid.getItems(fs));
		}
		return Optional.empty();
	}

	public static ResourceLocation getBase(ItemStack stack) {
		if (stack.getItem() instanceof StewItem) {
			return StewItem.getBase(stack);
		}
		LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
		if (cap.isPresent()) {
			IFluidHandlerItem data = cap.resolve().get();
			return SoupFluid.getBase(data.getFluidInTank(0));
		}
		return new ResourceLocation("water");
	}

	public static SoupInfo getInfo(ItemStack stack) {
		if (stack.getItem() instanceof StewItem) {
			return StewItem.getInfo(stack);
		}
		LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
		if (cap.isPresent()) {
			IFluidHandlerItem data = cap.resolve().get();
			return SoupFluid.getInfo(data.getFluidInTank(0));
		}
		return null;
	}
}
