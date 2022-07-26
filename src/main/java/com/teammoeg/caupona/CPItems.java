package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.blocks.DishBlock;
import com.teammoeg.caupona.items.CPItem;
import com.teammoeg.caupona.items.DishItem;
import com.teammoeg.caupona.items.IconItem;
import com.teammoeg.caupona.items.StewItem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;

public class CPItems {
	public static final String[] soups = new String[] { "acquacotta", "bisque", "borscht",
			"borscht_cream", "congee", "cream_of_meat_soup", "cream_of_mushroom_soup", "custard", "dilute_soup",
			"egg_drop_soup", "egg_tongsui", "fish_chowder", "fish_soup", "fricassee", "goji_tongsui", "goulash",
			"gruel", "hodgepodge", "meat_soup", "mushroom_soup", "nail_soup", "nettle_soup", "okroshka", "porridge",
			"poultry_soup", "pumpkin_soup", "pumpkin_soup_cream", "rice_pudding", "scalded_milk", "seaweed_soup",
			"stock", "stracciatella", "ukha", "vegetable_chowder", "vegetable_soup", "walnut_soup" };
	public static final String[] aspics=new String[] {"bisque_aspic",
			"borscht_aspic",
			"dilute_soup_aspic",
			"egg_drop_soup_aspic",
			"fish_soup_aspic",
			"goulash_aspic",
			"hodgepodge_aspic",
			"meat_soup_aspic",
			"mushroom_soup_aspic",
			"nettle_soup_aspic",
			"poultry_soup_aspic",
			"pumpkin_soup_aspic",
			"seaweed_soup_aspic",
			"stock_aspic",
			"stracciatella_aspic",
			"ukha_aspic",
			"vegetable_soup_aspic"};
	public static final String[] dishes=new String[] {"huevos_pericos"
			,"sauteed_beef"
			,"sauteed_greens"
			,"sauteed_hodgepodge"
			,"sauteed_meat"
			,"sauteed_mushrooms"
			,"sauteed_roots"
			,"sauteed_seafood"
			,"sauteed_vegetables"
			,"seared_fillet"
			,"seared_poultry"};
	public static final String[] spices=new String[] {"chives_spice_jar"
			,"garum_spice_jar"
			,"sugar_spice_jar"
			,"vinegar_spice_jar"};
	public static final String[] base_material=new String[] {"bone_gelatin"
			,"fig"
			,"lateres"
			,"walnut"
			,"wolfberries"
			,"clay_portable_brazier"};
	public static final List<Item> stews = new ArrayList<>();
	public static Item anyWater=new IconItem("water_or_stock_based");
	public static Item stock=new IconItem("stock_based");
	public static Item milk=new IconItem("milk_based");
	public static Item any=new IconItem("any_based");
	public static Item water;
	public static Item clay_pot=new CPItem("clay_cistern",new Item.Properties().tab(Main.itemGroup));
	public static Item soot=new CPItem("soot",new Item.Properties().tab(Main.itemGroup));
	public static void init() {
		for (String s : soups)
			new StewItem(s, new ResourceLocation(Main.MODID, s), createSoupProps());
		new StewItem("milk", new ResourceLocation("milk"), createSoupProps());
		water=new StewItem("water", new ResourceLocation("water"), createSoupProps());
		for(String s:aspics) 
			new CPItem(s,createProps());
		new IconItem("culinary_heat_haze");
		for(String s:spices) {
			new CPItem(s,createProps().durability(16).craftRemainder(Items.FLOWER_POT).setNoRepair());
		}
		for(String s:base_material) {
			new CPItem(s,createProps());
		}
		
	}

	static Properties createSoupProps() {
		return new Item.Properties().tab(Main.itemGroup).craftRemainder(Items.BOWL).stacksTo(1);
	}
	static Properties createProps() {
		return new Item.Properties().tab(Main.itemGroup);
	}
}