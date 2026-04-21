package com.teammoeg.caupona.client.util;

import java.util.function.Function;

import org.joml.Quaternionf;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class RenderHelper {
	
	private static Function<Direction,Quaternionf> mem=Util.memoize(dir->new Quaternionf().rotationY(dir.toYRot()*Mth.DEG_TO_RAD));
	public static Quaternionf getRotation(Direction dir) {
		return mem.apply(dir);
		
	}
}
