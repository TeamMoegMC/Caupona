package com.teammoeg.caupona.data.recipes;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.FloatemTagStack;

import net.minecraft.world.item.ItemStack;

public class PanPendingContext extends IPendingContext {

	public PanPendingContext(List<ItemStack> stacks,int parts) {
		items = new ArrayList<>(stacks.size());
		for(ItemStack is:stacks) {
			super.items.add(new FloatemTagStack(new FloatemStack(is,is.getCount()*1.0F/parts)));
			super.totalItems+=is.getCount()*1.0F/parts;
		}
	}

}
