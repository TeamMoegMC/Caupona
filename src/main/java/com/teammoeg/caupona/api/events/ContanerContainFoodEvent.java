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

package com.teammoeg.caupona.api.events;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class ContanerContainFoodEvent extends Event implements ICancellableEvent{
	@Deprecated
	public final ItemResource origin;
	private ItemResource out=ItemResource.EMPTY;
	@Deprecated
	public final FluidResource fs;
	@Deprecated
	public final int drainAmount;
	@Deprecated
	public final boolean isBlockAccess;

	public ContanerContainFoodEvent(ItemResource origin, FluidResource fluidIn, int drainAmount, boolean isBlockAccess) {
		super();
		this.origin = origin;
		this.fs = fluidIn;
		this.drainAmount = drainAmount;
		this.isBlockAccess = isBlockAccess;
	}
	public void setOutput(ItemResource item) {
		this.out=item;
	}
	public ItemStack createInputStack() {
		return origin.toStack();
	}
	public ItemResource getInput() {
		return origin;
	}
	public ItemResource getOutput() {
		return out;
	}
	public FluidResource getInputFluid() {
		return fs;
	}
	public FluidStack createStack() {
		return fs.toStack(drainAmount);
	}
	public boolean isBlockAccess() {
		return isBlockAccess;
	}
	public int getInputFluidAmount() {
		return drainAmount;
	}
	public boolean isAllowed() {
		return !out.isEmpty()&&!ICancellableEvent.super.isCanceled();
	}
	@Override
	public void setCanceled(boolean canceled) {
		ICancellableEvent.super.setCanceled(canceled);
	}
}
