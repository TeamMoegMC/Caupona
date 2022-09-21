package com.teammoeg.caupona.util;

import java.util.List;

import net.minecraft.world.food.FoodProperties;

public interface IFoodInfo {
	List<FloatemStack> getStacks();
	int getHealing();
	float getSaturation();
	FoodProperties getFood();
}
