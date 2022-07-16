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

package com.teammoeg.caupona.datagen;

import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.Main;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CPItemModelProvider extends ItemModelProvider {

	public CPItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator, modid, existingFileHelper);
	}


	@Override
	protected void registerModels() {
		for (String s : CPItems.soups) 
			simpleTexture(s,"soups/");
		for (String s : CPItems.aspics) 
			simpleTexture(s,"aspics/");
		simpleTexture("milk_based","bases/");
		simpleTexture("stock_based","bases/");
		simpleTexture("any_based","bases/");
		simpleTexture("water_or_stock_based","bases/");
		texture("book","vade_mecum_for_innkeepers");
		itemModel(CPItems.clay_pot,"clay_stew_pot");
		//super.withExistingParent("clay_cistern",new ResourceLocation(Main.MODID,"block/clay_cistern"));
	}
	public void itemModel(Item item,String name) {
    	super.withExistingParent(item.getRegistryName().getPath(),new ResourceLocation(Main.MODID,"block/"+name));
    }
	public void simpleTexture(String name,String par) {
		super.singleTexture(name,new ResourceLocation("minecraft", "item/generated"),"layer0",new ResourceLocation(Main.MODID,"item/"+par+name));
	}
	public void texture(String name,String par) {
		super.singleTexture(name,new ResourceLocation("minecraft", "item/generated"),"layer0",new ResourceLocation(Main.MODID,"item/"+par));
	}
}
