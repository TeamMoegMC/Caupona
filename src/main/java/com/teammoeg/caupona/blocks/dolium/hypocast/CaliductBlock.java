package com.teammoeg.caupona.blocks.dolium.hypocast;

import java.util.function.BiFunction;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPBaseTileBlock;
import com.teammoeg.caupona.blocks.CPHorizontalTileBlock;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CaliductBlock extends CPHorizontalTileBlock<CaliductTile> {

	public CaliductBlock(Properties blockProps) {
		super(CPTileTypes.CALIDUCT,blockProps);
		CPBlocks.caliduct.add(this);
	}

}
