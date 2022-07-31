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
import net.minecraft.world.item.Items;

public class CPItems {
	public static final String[] soups = new String[] { "acquacotta", "bisque", "borscht", "borscht_cream", "congee",
			"cream_of_meat_soup", "cream_of_mushroom_soup", "custard", "dilute_soup", "egg_drop_soup", "egg_tongsui",
			"fish_chowder", "fish_soup", "fricassee", "goji_tongsui", "goulash", "gruel", "hodgepodge", "meat_soup",
			"mushroom_soup", "nail_soup", "nettle_soup", "okroshka", "porridge", "poultry_soup", "pumpkin_soup",
			"pumpkin_soup_cream", "rice_pudding", "scalded_milk", "seaweed_soup", "stock", "stracciatella", "ukha",
			"vegetable_chowder", "vegetable_soup", "walnut_soup", "bone_gelatin" };
	public static final String[] aspics = new String[] { "bisque_aspic", "borscht_aspic", "dilute_soup_aspic",
			"egg_drop_soup_aspic", "fish_soup_aspic", "goulash_aspic", "hodgepodge_aspic", "meat_soup_aspic",
			"mushroom_soup_aspic", "nettle_soup_aspic", "poultry_soup_aspic", "pumpkin_soup_aspic",
			"seaweed_soup_aspic", "stock_aspic", "stracciatella_aspic", "ukha_aspic", "vegetable_soup_aspic" };
	public static final String[] dishes = new String[] { "huevos_pericos", "sauteed_beef", "sauteed_greens",
			"sauteed_hodgepodge", "sauteed_meat", "sauteed_mushrooms", "sauteed_roots", "sauteed_seafood",
			"sauteed_vegetables", "seared_fillet", "seared_poultry" };
	public static final String[] spices = new String[] { "chives_spice_jar", "garum_spice_jar", "sugar_spice_jar",
			"vinegar_spice_jar" };
	public static final List<Item> spicesItems = new ArrayList<>();
	public static final String[] food_material = new String[] { "fig", "walnut", "wolfberries" };
	public static final String[] base_material = new String[] { "lateres", "clay_portable_brazier", "vivid_charcoal" };

	public static final List<Item> stews = new ArrayList<>();
	public static Item anyWater = new IconItem("water_or_stock_based");
	public static Item stock = new IconItem("stock_based");
	public static Item milk = new IconItem("milk_based");
	public static Item any = new IconItem("any_based");
	public static Item water_bowl = new StewItem("water", new ResourceLocation("water"), createSoupProps());
	public static Item milk_bowl = new StewItem("milk", new ResourceLocation("milk"), createSoupProps());
	public static Item clay_pot = new CPItem("clay_cistern", createProps());
	public static Item soot = new CPItem("soot", createProps());
	public static Item pbrazier = new PortableBrazierItem("portable_brazier", createProps());
	public static Item gravy_boat = new CPBlockItem(CPBlocks.GRAVY_BOAT, createFoodProps().durability(5).setNoRepair(),
			"gravy_boat");
	public static Item walnut_boat = new CPBoatItem("walnut", createProps());
	public static Item chronoconis = new Chronoconis("chronoconis", createFoodProps());
	public static Item haze = new IconItem("culinary_heat_haze");
	public static Item ddish;
	public static Item acquacotta;

	public static void init() {
		for (String s : soups) {
			Item it = new StewItem(s, new ResourceLocation(Main.MODID, s), createSoupProps());
			if (s.equals("acquacotta"))
				acquacotta = it;
		}

		for (String s : aspics)
			new CPItem(s, createFoodProps());

		for (String s : spices) {
			spicesItems.add(
					new CPItem(s, createFoodProps().durability(16).craftRemainder(Items.FLOWER_POT).setNoRepair()));
		}
		for (String s : base_material) {
			new CPItem(s, createProps());
		}
		for (String s : food_material) {
			new CPItem(s,
					createFoodProps().food(new FoodProperties.Builder().nutrition(4).saturationMod(0.3f).build()));
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