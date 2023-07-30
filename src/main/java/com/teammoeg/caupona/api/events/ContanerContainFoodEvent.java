package com.teammoeg.caupona.api.events;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fluids.FluidStack;

public class ContanerContainFoodEvent extends Event {
	public ItemStack origin;
	public ItemStack out=ItemStack.EMPTY;
	public FluidStack fs;
	public int drainAmount;
	public boolean isSimulated;
	public ContanerContainFoodEvent(ItemStack origin, FluidStack fs,boolean isSimulated) {
		super();
		this.origin = origin;
		this.fs = fs;
		this.drainAmount = fs.getAmount();
		this.isSimulated=isSimulated;
	}
	public boolean isAllowed() {
		return this.getResult()==Result.ALLOW&&!out.isEmpty();
	}
}
