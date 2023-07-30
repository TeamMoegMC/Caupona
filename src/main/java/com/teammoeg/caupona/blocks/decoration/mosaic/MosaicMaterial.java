package com.teammoeg.caupona.blocks.decoration.mosaic;

import net.minecraft.util.StringRepresentable;

public enum MosaicMaterial implements StringRepresentable{
	brick,
	basalt,
	pumice;

	@Override
	public String getSerializedName() {
		return this.name();
	}
}
