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

package com.teammoeg.caupona.compat.jei.category;

import java.util.List;

import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

class BaseCallback implements IRecipeSlotTooltipCallback{
	ResourceLocation base;
	float dense;
	public BaseCallback(ResourceLocation base, float density) {
		super();
		this.base=base;
		this.dense=density;
	}

	@Override
	public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
		if(base!=null)
			tooltip.add(new TranslatableComponent("recipe.caupona.base",new TranslatableComponent(ForgeRegistries.FLUIDS.getValue(base).getAttributes().getTranslationKey())));
		if(dense!=0)
			tooltip.add(new TranslatableComponent("recipe.caupona.density",dense));
	}
	
}