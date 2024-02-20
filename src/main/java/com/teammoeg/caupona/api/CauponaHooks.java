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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class CauponaHooks {

	private CauponaHooks() {
	}

	public static final ResourceLocation stew = new ResourceLocation(CPMain.MODID, "stews");
	private static final LinkedHashMap<String,Function<ItemStack,IFoodInfo>> foodInfoHooks=new LinkedHashMap<>();
	static {
		foodInfoHooks.put("caupona:stew_item",stack->{
			if (stack.getItem() instanceof StewItem) {
				return StewItem.getInfo(stack);
			}
			return null;
		});
		foodInfoHooks.put("caupona:soup_fluid_handler",stack->{
			LazyOptional<IFluidHandlerItem> cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
			if (cap.isPresent()) {
				IFluidHandlerItem data = cap.resolve().get();
				return SoupFluid.getInfoOrNull(data.getFluidInTank(0));
			}
			return null;
		});
		foodInfoHooks.put("caupona:dish_item",stack->{
			if(stack.getItem() instanceof DishItem)
				return DishItem.getInfo(stack);
			return null;
		});
	}
	public static void registerInfoProvider(String name,Function<ItemStack,IFoodInfo> funcs) {
		foodInfoHooks.put(name, funcs);
	}
	
	public static Optional<List<FloatemStack>> getItems(ItemStack stack) {
		return getInfo(stack).map(t->t.getStacks());
	}

	public static ResourceLocation getBase(ItemStack stack) {
		LazyOptional<IFluidHandlerItem> cap = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
		if (cap.isPresent()) {
			IFluidHandlerItem data = cap.resolve().get();
			return SoupFluid.getBase(data.getFluidInTank(0));
		}else if(Utils.getFluidType(stack)!=Fluids.EMPTY) {
			return StewItem.getBase(stack);
		}
		return new ResourceLocation("water");
	}

	public static Optional<IFoodInfo> getInfo(ItemStack stack) {
		return foodInfoHooks.values().stream().map(t->t.apply(stack)).filter(t->t!=null).findFirst();
	}
	
}
