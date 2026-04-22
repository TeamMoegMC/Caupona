package com.teammoeg.caupona.compat.jei.category;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface CPCategory<T> extends IRecipeCategory<T> {
	public IDrawable getBackground();
	@Override
	default int getWidth() {
		return getBackground().getWidth();
	}

	@Override
	default int getHeight() {
		return getBackground().getHeight();
	}
	@Override
	default void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		// TODO Auto-generated method stub
		IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
		getBackground().draw(guiGraphics);
	}



}
