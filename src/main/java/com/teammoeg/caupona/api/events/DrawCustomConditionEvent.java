package com.teammoeg.caupona.api.events;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teammoeg.caupona.data.recipes.IngredientCondition;

import mezz.jei.api.helpers.IGuiHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@OnlyIn(Dist.CLIENT)
@Cancelable
public class DrawCustomConditionEvent extends Event{
	IGuiHelper guihelper;
	List<IngredientCondition> conditions;
	PoseStack stack;
	int xOffset;
	int yOffset;
	public DrawCustomConditionEvent(IGuiHelper guihelper, List<IngredientCondition> conditions, PoseStack stack,
			int xOffset, int yOffset) {
		super();
		this.guihelper = guihelper;
		this.conditions = conditions;
		this.stack = stack;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
