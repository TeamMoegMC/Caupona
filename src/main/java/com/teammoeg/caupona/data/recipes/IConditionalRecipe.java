package com.teammoeg.caupona.data.recipes;

import java.util.List;

public interface IConditionalRecipe {

	List<IngredientCondition> getAllow();

	List<IngredientCondition> getDeny();

}