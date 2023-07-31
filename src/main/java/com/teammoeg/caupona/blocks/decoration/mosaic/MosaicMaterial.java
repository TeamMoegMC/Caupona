package com.teammoeg.caupona.blocks.decoration.mosaic;

import net.minecraft.util.StringRepresentable;

public enum MosaicMaterial implements StringRepresentable{
	brick("t"),
	basalt("b"),
	pumice("p");
	public final String shortName;
	private MosaicMaterial(String shortName) {
		this.shortName = shortName;
	}
	@Override
	public String getSerializedName() {
		return this.name();
	}
}
