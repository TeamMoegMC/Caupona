package com.teammoeg.caupona.util;

import net.minecraft.resources.Identifier;

public interface RecipeProcessor {
	RecipeHandleStatus run(Identifier id);
}
