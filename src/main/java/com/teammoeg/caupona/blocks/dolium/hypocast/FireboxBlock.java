package com.teammoeg.caupona.blocks.dolium.hypocast;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;

public class FireboxBlock extends BathHeatingBlock<FireboxTile> {

	public FireboxBlock(Properties blockProps) {
		super(CPTileTypes.FIREBOX,blockProps);
		CPBlocks.firebox.add(this);
	}

}
