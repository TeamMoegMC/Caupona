/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.items;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.util.FloatemStack;
import com.teammoeg.caupona.util.SauteedFoodInfo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class DishItem extends EdibleBlock {
	public static final FoodProperties fakefood = new FoodProperties.Builder().nutrition(4).saturationMod(0.2f).meat()
			.build();
	public final DishBlock bl;

	public DishItem(DishBlock block, Properties props) {
		super(block, props.food(fakefood));
		bl = block;
	}

	public DishItem(DishBlock block, Properties props, String name) {
		super(block, props.food(fakefood), name);
		bl = block;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.EAT;
	}

	public static List<FloatemStack> getItems(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag soupTag = stack.getTagElement("dish");
			if (soupTag != null)
				return SauteedFoodInfo.getStacks(soupTag);
		}
		return Lists.newArrayList();
	}
	public static SauteedFoodInfo getInfo(ItemStack stack) {
		if (stack.hasTag()) {
			CompoundTag soupTag = stack.getTagElement("dish");
			if (soupTag != null)
				return new SauteedFoodInfo(soupTag);
		}
		return new SauteedFoodInfo();
	}

	public static void setInfo(ItemStack stack, SauteedFoodInfo current) {
		if (!current.isEmpty())
			stack.getOrCreateTag().put("dish", current.save());
	}

	@Override
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		SauteedFoodInfo info = DishItem.getInfo(stack);
		FloatemStack fs = info.stacks.stream()
				.max((t1, t2) -> t1.getCount() > t2.getCount() ? 1 : (t1.getCount() == t2.getCount() ? 0 : -1))
				.orElse(null);
		if (fs != null)
			tooltip.add(new TranslatableComponent("tooltip.caupona.main_ingredient", fs.getStack().getDisplayName()));
		ResourceLocation rl = info.spiceName;
		if (rl != null)
			tooltip.add(new TranslatableComponent("tooltip.caupona.spice",
					new TranslatableComponent("spice." + rl.getNamespace() + "." + rl.getPath())));
		;
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
	}

	@Override
	public FoodProperties getFoodProperties(ItemStack stack, LivingEntity entity) {
		return getInfo(stack).getFood();
		
	}
}
