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

package com.teammoeg.caupona.item;

import java.util.function.Supplier;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.components.ItemHoldedFluidData;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public class StewItem extends EdibleBlock{



	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isFoodTab()) {
			ItemStack is = new ItemStack(this);
			FluidStack fs=new FluidStack(fluid.get(),250);
			Utils.setInfo(fs, new StewInfo().toImmutable());
			is.applyComponents(fs.getComponentsPatch());
			is.set(CPCapability.ITEM_FLUID, new ItemHoldedFluidData(FluidResource.of(fs)));
			super.addCreativeHints(is);
			helper.accept(is);
		}
	}

	Supplier<Fluid> fluid;
	public StewItem(Block block,Supplier<Fluid> fluid, Properties properties) {
		super(block, properties);
		CPItems.stews.add(this);
		this.fluid = fluid;
	}

}
