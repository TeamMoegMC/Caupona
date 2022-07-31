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

package com.teammoeg.caupona.patchouli;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.api.GameTranslation;
import com.teammoeg.caupona.data.recipes.FryingRecipe;
import com.teammoeg.caupona.data.recipes.IConditionalRecipe;
import com.teammoeg.caupona.data.recipes.IngredientCondition;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;

public class AllowenceTooltip implements ICustomComponent {
	boolean allow;
	int x, y, w, h;
	IVariable recipe;
	transient List<Component> allowence;

	public AllowenceTooltip() {
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		recipe = lookup.apply(recipe);
		ResourceLocation out = new ResourceLocation(recipe.asString());
		IConditionalRecipe cr = StewCookingRecipe.recipes.get(ForgeRegistries.FLUIDS.getValue(out));
		if (cr == null) {
			cr = FryingRecipe.recipes.get(ForgeRegistries.ITEMS.getValue(out));
		}
		if (cr != null) {
			List<IngredientCondition> conds;
			if (allow)
				conds = cr.getAllow();
			else
				conds = cr.getDeny();
			if (conds != null)
				allowence = conds.stream().map(e -> e.getTranslation(GameTranslation.get())).map(TextComponent::new)
						.collect(Collectors.toList());
			if (allowence != null && !allowence.isEmpty()) {
				if (allow)
					allowence.add(0, new TranslatableComponent("recipe.caupona.allow"));
				else
					allowence.add(0, new TranslatableComponent("recipe.caupona.deny"));
			}
		}
	}

	@Override
	public void build(int componentX, int componentY, int pageNum) {
	}

	@Override
	public void render(PoseStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		if (context.isAreaHovered(mouseX, mouseY, x, y, w, h))
			if (allowence != null && !allowence.isEmpty())
				context.setHoverTooltipComponents(allowence);
	}

}
