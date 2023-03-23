package com.teammoeg.caupona.item;

import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.ICreativeModeTabItem;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;

public class CPSignItem extends SignItem implements ICreativeModeTabItem {
	public CPSignItem(Properties pProperties, Block pStandingBlock, Block pWallBlock, TabType tab) {
		super(pProperties, pStandingBlock, pWallBlock);
		this.tab = tab;
	}

	TabType tab;
	
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if(helper.isType(tab))helper.accept(this);
	}

}
