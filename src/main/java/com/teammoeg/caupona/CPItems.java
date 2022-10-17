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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.items.CPBlockItem;
import com.teammoeg.caupona.items.CPBoatItem;
import com.teammoeg.caupona.items.CPItem;
import com.teammoeg.caupona.items.Chronoconis;
import com.teammoeg.caupona.items.IconItem;
import com.teammoeg.caupona.items.PortableBrazierItem;
import com.teammoeg.caupona.items.StewItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.Items;

public class CPItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
	public static final String[] soups = new String[] { "acquacotta", "bisque", "borscht", "borscht_cream", "congee",
			"cream_of_meat_soup", "cream_of_mushroom_soup", "custard", "dilute_soup", "egg_drop_soup", "egg_tongsui",
			"fish_chowder", "fish_soup", "fricassee", "goji_tongsui", "goulash", "gruel", "hodgepodge", "meat_soup",
			"mushroom_soup", "nail_soup", "nettle_soup", "okroshka", "porridge", "poultry_soup", "pumpkin_soup",
			"pumpkin_soup_cream", "rice_pudding", "scalded_milk", "seaweed_soup", "stock", "stracciatella", "ukha",
			"vegetable_chowder", "vegetable_soup", "walnut_soup", "bone_gelatin" };
	public static final String[] aspics = new String[] { "bisque_aspic", "borscht_aspic", "dilute_soup_aspic",
			"egg_drop_soup_aspic", "fish_soup_aspic", "goulash_aspic", "hodgepodge_aspic", "meat_soup_aspic",
			"mushroom_soup_aspic", "nettle_soup_aspic", "poultry_soup_aspic", "pumpkin_soup_aspic",
			"seaweed_soup_aspic", "stock_aspic", "stracciatella_aspic", "vegetable_soup_aspic" };
	public static final String[] dishes = new String[] { "huevos_pericos", "sauteed_beef", "sauteed_greens",
			"sauteed_hodgepodge", "sauteed_meat", "sauteed_mushrooms", "sauteed_roots", "sauteed_seafood",
			"sauteed_vegetables", "seared_fillet", "seared_poultry" };
	public static final String[] spices = new String[] { "chives_spice_jar", "garum_spice_jar", "sugar_spice_jar",
			"vinegar_spice_jar" };
	public static final List<RegistryObject<Item>> spicesItems = new ArrayList<>();
	public static final String[] food_material = new String[] { "fig", "walnut", "wolfberries" };
	public static final String[] base_material = new String[] { "lateres", "clay_portable_brazier", "vivid_charcoal" };

	public static final List<Item> stews = new ArrayList<>();
	
	public static RegistryObject<Item> anyWater = icon("water_or_stock_based");
	public static RegistryObject<Item> stock = icon("stock_based");
	public static RegistryObject<Item> milk = icon("milk_based");
	public static RegistryObject<Item> any = icon("any_based");
	public static RegistryObject<Item> water_bowl = stew("water", new ResourceLocation("water"), createSoupProps());
	public static RegistryObject<Item> milk_bowl = stew("milk", new ResourceLocation("milk"), createSoupProps());
	public static RegistryObject<Item> clay_pot = item("clay_cistern", createProps());
	public static RegistryObject<Item> soot = item("soot", createProps());
	public static RegistryObject<PortableBrazierItem> pbrazier = ITEMS.register("portable_brazier",()->new PortableBrazierItem( createProps()));
	public static RegistryObject<CPBlockItem> gravy_boat = ITEMS.register("gravy_boat",()->new CPBlockItem(CPBlocks.GRAVY_BOAT.get(), createFoodProps().durability(5).setNoRepair()));
	public static RegistryObject<CPBoatItem> walnut_boat = ITEMS.register("walnut_boat", ()->new CPBoatItem("walnut", createProps()));
	public static RegistryObject<Chronoconis> chronoconis = ITEMS.register("chronoconis",()->new Chronoconis( createFoodProps()));
	//public static Item haze = icon("culinary_heat_haze");
	public static RegistryObject<Item> icon(String name){
		return ITEMS.register(name,IconItem::new);
	}
	public static RegistryObject<Item> item(String name,Properties props){
		return ITEMS.register(name,()->new CPItem(props));
	}
	public static RegistryObject<Item> stew(String name,ResourceLocation base,Properties props){
		return ITEMS.register(name,()->new StewItem(base,props));
	}
	static{
		for (String s : soups) {
			RegistryObject<Item> it = stew(s, new ResourceLocation(Main.MODID, s), createSoupProps());
		}

		for (String s : aspics)
			item(s, createFoodProps());

		for (String s : spices) {
			spicesItems.add(
					item(s, createFoodProps().durability(16).craftRemainder(Items.FLOWER_POT).setNoRepair()));
		}
		for (String s : base_material) {
			item(s, createProps());
		}
		for (String s : food_material) {
			item(s,createFoodProps().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()));
		}
	}

	static Properties createSoupProps() {
		return new Item.Properties().tab(Main.foodGroup).craftRemainder(Items.BOWL).stacksTo(1)
				.craftRemainder(Items.BOWL);
	}

	static Properties createFoodProps() {
		return new Item.Properties().tab(Main.foodGroup);
	}

	static Properties createProps() {
		return new Item.Properties().tab(Main.mainGroup);
	}
}