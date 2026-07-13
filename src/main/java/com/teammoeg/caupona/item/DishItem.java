/*
 * Copyright (c) 2024 TeamMoeg
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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.item;

import org.jspecify.annotations.Nullable;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.components.SauteedFoodInfo;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public class DishItem extends EdibleBlock {
	public final DishBlock bl;

	public DishItem(DishBlock block, Properties props) {
		super(block, props);
		CPItems.dish.add(this);
		bl = block;
	}
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		if (helper.isFoodTab()) {
			ItemStack is = new ItemStack(this);
			Utils.setInfo(is, new SauteedFoodInfo());
			super.addCreativeHints(is);
			helper.accept(is);
		}
	}
	@Override
	public @Nullable ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
		return new ItemStackTemplate(Items.BOWL);
	}

	@Override
	public int getUseDuration(ItemStack stack,LivingEntity ent) {
		return 32;
	}
}
