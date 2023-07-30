package com.teammoeg.caupona.blocks.decoration.mosaic;

import net.minecraft.util.StringRepresentable;

public enum MosaicPattern implements StringRepresentable{
	corner;
	@Override
	public String getSerializedName() {
		return this.name();
	}
}
