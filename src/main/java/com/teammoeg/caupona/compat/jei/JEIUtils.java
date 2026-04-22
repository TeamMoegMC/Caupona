package com.teammoeg.caupona.compat.jei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

public class JEIUtils {

	public JEIUtils() {
		// TODO Auto-generated constructor stub
	}
	public static List<FluidStack> unpack(FluidIngredient ps,int amount) {
		List<FluidStack> sl = new ArrayList<>();
		for (Holder<Fluid> is : ps.fluids())
			sl.add(new FluidStack(is.value(),amount));
		return sl;
	}
	public static List<ItemStack> unpack(Ingredient ps,int amount) {
		List<ItemStack> sl = new ArrayList<>();
		for (Holder<Item> is : ps.getValues())
			sl.add(new ItemStack(is.value(),amount));
		return sl;
	}
}
