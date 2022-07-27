package com.teammoeg.caupona.items;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class CPGravyBoatItem extends CPBlockItem {

	public CPGravyBoatItem(Block block, Properties props) {
		super(block, props);
	}

	public CPGravyBoatItem(Block block, Properties props, String name) {
		super(block, props, name);
	}

	@Override
	public void fillItemCategory(CreativeModeTab pGroup, NonNullList<ItemStack> pItems) {
		super.fillItemCategory(pGroup, pItems);
		if(this.allowdedIn(pGroup)) {
			ItemStack is=new ItemStack(this);
			is.setDamageValue(this.getMaxDamage(is));
			pItems.add(is);
		}
	}

}
