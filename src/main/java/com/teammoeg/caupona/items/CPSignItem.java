package com.teammoeg.caupona.items;

import com.teammoeg.caupona.TabType;
import com.teammoeg.caupona.util.CreativeItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;

import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;

public class CPSignItem extends SignItem implements ICreativeModeTabItem {
	public CPSignItem(Properties pProperties, Block pStandingBlock, Block pWallBlock, TabType tab) {
		super(pProperties, pStandingBlock, pWallBlock);
		this.tab = tab;
	}

	TabType tab;
	
	@Override
	public void fillItemCategory(CreativeItemHelper helper) {
		if(helper.isType(tab))helper.accept(this);
	}

}
