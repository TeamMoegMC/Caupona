package com.teammoeg.caupona.blocks.foods;

import net.minecraft.world.item.ItemStack;

public interface IFoodContainer {
	ItemStack getInternal(int num);
	void setInternal(int num,ItemStack is);
	int getSlots();
	boolean accepts(int num,ItemStack is);
}
