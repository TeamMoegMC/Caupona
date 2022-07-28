package com.teammoeg.caupona.blocks.dolium.hypocast;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;

public class CaliductBlock extends BathHeatingBlock<CaliductTile> {

	public CaliductBlock(Properties blockProps) {
		super(CPTileTypes.CALIDUCT,blockProps);
		CPBlocks.caliduct.add(this);
	}

}
