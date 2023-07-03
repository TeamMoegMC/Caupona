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

import com.teammoeg.caupona.item.CPBlockItem;
import com.teammoeg.caupona.item.CPBoatItem;
import com.teammoeg.caupona.item.CPItem;
import com.teammoeg.caupona.item.Chronoconis;
import com.teammoeg.caupona.item.IconItem;
import com.teammoeg.caupona.item.PortableBrazierItem;
import com.teammoeg.caupona.item.SitulaItem;
import com.teammoeg.caupona.item.SkimmerItem;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.util.FoodMaterialInfo;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPItems {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CPMain.MODID);
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
			"vinegar_spice_jar", "sapa_spice_jar", "asafoetida_spice_jar" };
	public static final List<RegistryObject<Item>> spicesItems = new ArrayList<>();
	public static final FoodMaterialInfo[] food_material = new FoodMaterialInfo[] { 
			new FoodMaterialInfo("fig",4,0.3f,0.6f), 
			new FoodMaterialInfo("walnut",4,0.3f,0.6f),
			new FoodMaterialInfo("wolfberries",4,0.3f,0.6f),
			new FoodMaterialInfo("snail",2,0.3f,0.6f).food(c->c.effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F)),
			new FoodMaterialInfo("plump_snail",3,0.3f,0.7f).food(c->c.effect(new MobEffectInstance(MobEffects.HUNGER, 600, 0), 0.3F))};
	public static final String[] base_material = new String[] { "lateres", "clay_portable_brazier", "vivid_charcoal", "silphium", "asafoetida", "leaden_walnut", "litharge_cake", "lead_ingot", "lead_nugget" };

	public static final List<Item> stews = new ArrayList<>();
	
	public static RegistryObject<Item> anyWater = icon("water_or_stock_based");
	public static RegistryObject<Item> stock = icon("stock_based");
	public static RegistryObject<Item> milk = icon("milk_based");
	public static RegistryObject<Item> any = icon("any_based");
	public static RegistryObject<Item> water_bowl = stew("water", new ResourceLocation("water"), createSoupProps());
	public static RegistryObject<Item> milk_bowl = stew("milk", new ResourceLocation("milk"), createSoupProps());
	public static RegistryObject<Item> clay_pot = item("clay_cistern", createProps(),TabType.MAIN);
	public static RegistryObject<Item> soot = item("soot", createProps(),TabType.MAIN);
	public static RegistryObject<PortableBrazierItem> pbrazier = ITEMS.register("portable_brazier",()->new PortableBrazierItem( createProps()));
	public static RegistryObject<Item> situla = ITEMS.register("situla",()->new SitulaItem( createProps().stacksTo(1)));
	public static RegistryObject<Item> redstone_ladle= item("redstone_ladle",createProps(),TabType.MAIN);
	public static RegistryObject<Item> scraps= CPCommonBootStrap.asCompositable(item("scraps",createProps(),TabType.MAIN),0.7f);
	
	public static RegistryObject<SkimmerItem> b_skimmer = ITEMS.register("bamboo_skimmer",()->new SkimmerItem( createProps().durability(20)));
	public static RegistryObject<SkimmerItem> i_skimmer = ITEMS.register("iron_skimmer",()->new SkimmerItem( createProps().durability(200)));
	static{
		for (String s : soups) {
			stew(s, new ResourceLocation(CPMain.MODID, s), createSoupProps());
		}

		for (String s : aspics) {
			CPCommonBootStrap.asCompositable(item(s, createProps(),TabType.FOODS),1f);
		}
		for (String s : spices) {
			spicesItems.add(
					item(s, createProps().durability(16).craftRemainder(Items.FLOWER_POT).setNoRepair(),TabType.FOODS));
		}
		for (String s : base_material) {
			item(s, createProps(),TabType.MAIN);
		}
		for (FoodMaterialInfo s : food_material) {
			Properties props=createProps();
			if(s.food!=null)
				props.food(s.food.build());
				
			RegistryObject<Item> item=item(s.name,props,TabType.FOODS);
			if(s.composite!=0)
				CPCommonBootStrap.asCompositable(item,s.composite);
		}
	}
	public static RegistryObject<CPBlockItem> gravy_boat = ITEMS.register("gravy_boat",()->new CPBlockItem(CPBlocks.GRAVY_BOAT.get(), createProps().durability(5).setNoRepair(),TabType.FOODS));
	public static RegistryObject<CPBoatItem> walnut_boat = ITEMS.register("walnut_boat", ()->new CPBoatItem("walnut", createProps()));
	public static RegistryObject<Chronoconis> chronoconis = ITEMS.register("chronoconis",()->new Chronoconis( createProps()));
	//public static Item haze = icon("culinary_heat_haze");
	public static RegistryObject<Item> icon(String name){
		return ITEMS.register(name,IconItem::new);
	}
	public static RegistryObject<Item> item(String name,Properties props,TabType tab){
		return ITEMS.register(name,()->new CPItem(props,tab));
	}
	public static RegistryObject<Item> stew(String name,ResourceLocation base,Properties props){
		return ITEMS.register(name,()->new StewItem(base,props));
	}


	private static Properties createSoupProps() {
		return new Item.Properties().craftRemainder(Items.BOWL).stacksTo(1);
	}

	static Properties createProps() {
		return new Item.Properties();
	}
}