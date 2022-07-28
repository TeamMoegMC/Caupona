package com.teammoeg.caupona.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class Utils {

	private Utils() {
	}
	public static ItemStack insertToOutput(ItemStackHandler inv,int slot,ItemStack in) {
		ItemStack is=inv.getStackInSlot(slot);
		if(is.isEmpty()) {
			inv.setStackInSlot(slot, in.split(Math.min(inv.getSlotLimit(slot),in.getMaxStackSize())));
		}else if(ItemHandlerHelper.canItemStacksStack(in, is)){
			int limit=Math.min(inv.getSlotLimit(slot),is.getMaxStackSize());
			limit-=is.getCount();
			limit=Math.min(limit, in.getCount());
			is.grow(limit);
			in.shrink(limit);
		}
		return in;
	}
}
