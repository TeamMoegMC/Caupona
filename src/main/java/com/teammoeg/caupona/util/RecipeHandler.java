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

package com.teammoeg.caupona.util;

import java.util.Objects;

import com.teammoeg.caupona.data.recipes.TimedRecipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class RecipeHandler<T extends Recipe<?>> implements ContainerData{
	private int process;
	private int processMax;
	private Identifier lastRecipe;
	private boolean recipeTested=false;
	private RecipeProcessor doRecipe;
	private boolean recipeFinished=false;
	public Identifier getLastRecipe() {
		return lastRecipe;
	}
	public RecipeHandler(RecipeProcessor doRecipe) {
		this.doRecipe=doRecipe;
	}
	public void onContainerChanged() {
		//System.out.println("revalidate needed");
		recipeTested=false;
	}
	public boolean shouldTestRecipe() {
		return !recipeTested;
	}
	public boolean isRecipeFinished() {
		return recipeFinished;
	}
	public void setRecipe(RecipeHolder<T> recipe,int calculatedProcessTime) {
		//System.out.println("revalidate return "+recipe);
		if (recipe!= null) {
			if(!recipe.id().identifier().equals(lastRecipe)) {
				process=processMax=calculatedProcessTime;
				lastRecipe=recipe.id().identifier();
				recipeFinished=false;
			}
		}else {
			process=processMax=0;
			lastRecipe=null;
			recipeFinished=false;
		}
		recipeTested=true;
	}
	public boolean shouldTick() {
		return process>0;
	}
	public boolean tickProcess(int num) {
		if (process > 0) {
			process-=num;
			if(process<=0) {
				recipeFinished=true;
				RecipeHandleStatus status=doRecipe.run(lastRecipe);
				if(status.resetsProcess()) {
					process=processMax=0;
					lastRecipe=null;
					recipeTested=false;
					recipeFinished=false;
					return true;
				}else {
					process=10;
				}
			}
			return !recipeFinished;
		}
		return false;
	}
	public void resetProgress() {
		process=processMax;
		recipeFinished=false;
	}
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		process=nbt.getIntOr("process",0);
		processMax=nbt.getIntOr("processMax",0);
		recipeFinished=nbt.getBooleanOr("recipeFinished", false);
		if (!isClient) {
			lastRecipe=nbt.getString("lastRecipe").map(Identifier::parse).orElse(null);
		}

	}
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putInt("process",process);
		nbt.putInt("processMax",processMax);
		nbt.putBoolean("recipeFinished", recipeFinished);
		if (!isClient) {
			if(lastRecipe!=null)
				nbt.putString("lastRecipe", lastRecipe.toString());
		}

	}
	public int getProcess() {
		return process;
	}
	public int getProcessMax() {
		return processMax;
	}
	public int getFinishedProgress() {
		if(recipeFinished)
			return processMax;
		return processMax-process;
	}
	@Override
	public int get(int index) {
		Objects.checkIndex(index, 2);
		switch(index) {
		case 0:return recipeFinished?0:process;
		case 1:return processMax;
		}
		return -1;
	}
	@Override
	public void set(int index, int value) {
		Objects.checkIndex(index, 2);
		switch(index) {
		case 0:process=value;return;
		case 1:processMax=value;return;
		}
	}
	@Override
	public int getCount() {
		return 2;
	}

}
