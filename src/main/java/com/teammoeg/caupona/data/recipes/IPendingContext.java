package com.teammoeg.caupona.data.recipes;

import java.util.List;
import java.util.function.Predicate;

import com.teammoeg.caupona.util.FloatemTagStack;
import com.teammoeg.caupona.util.ResultCachingMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class IPendingContext {

	protected List<FloatemTagStack> items;
	protected float totalItems;
	private ResultCachingMap<CookIngredients, Float> numbers = new ResultCachingMap<>(e -> e.apply(this));
	private ResultCachingMap<IngredientCondition, Boolean> results = new ResultCachingMap<>(e -> e.test(this));

	public IPendingContext() {
		super();
	}

	public float compute(CookIngredients sn) {
		return numbers.compute(sn);
	}

	public boolean compute(IngredientCondition sc) {
		return results.compute(sc);
	}

	public float getOfType(ResourceLocation rl) {
		return (float) items.stream().filter(e -> e.getTags().contains(rl)).mapToDouble(FloatemTagStack::getCount)
				.sum();
	}

	public float getOfItem(Predicate<ItemStack> pred) {
		for (FloatemTagStack fs : items)
			if (pred.test(fs.getStack()))
				return fs.getCount();
		return 0f;
	}


	public float getTotalItems() {
		return totalItems;
	}

	public List<FloatemTagStack> getItems() {
		return items;
	}

}