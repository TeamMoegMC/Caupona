package com.teammoeg.caupona.blocks.dolium.hypocast;

import java.util.function.BiFunction;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPBaseTileBlock;
import com.teammoeg.caupona.blocks.CPHorizontalTileBlock;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FireboxBlock extends CPHorizontalTileBlock<FireboxTile> {

	public FireboxBlock(Properties blockProps) {
		super(CPTileTypes.FIREBOX,blockProps);
		CPBlocks.firebox.add(this);
	}

}
