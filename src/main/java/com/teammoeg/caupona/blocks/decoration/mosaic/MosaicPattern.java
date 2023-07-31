package com.teammoeg.caupona.blocks.decoration.mosaic;

import net.minecraft.util.StringRepresentable;

public enum MosaicPattern implements StringRepresentable{
	crowstep,
	dentil,
	guilloche_corner,
	guilloche,
	imbrication,
	meander,
	meander_corner,
	right_angle,
	round_angle,
	split,
	wave;
	@Override
	public String getSerializedName() {
		return this.name();
	}
}
