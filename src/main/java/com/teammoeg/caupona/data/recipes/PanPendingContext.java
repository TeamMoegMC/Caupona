package com.teammoeg.caupona.data.recipes;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.world.item.ItemStack;

public class PanPendingContext extends IPendingContext {

	public PanPendingContext(List<ItemStack> stacks) {
		items = new ArrayList<>(stacks.size());
		for(ItemStack is:stacks) {
			super.items.add(new FloatemTagStack(is));
			super.totalItems+=is.getCount();
		}
	}

}
