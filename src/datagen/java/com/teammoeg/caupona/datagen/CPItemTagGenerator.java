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

import static com.teammoeg.caupona.datagen.CPRecipeProvider.baked;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.cereals;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.crustaceans;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.eggs;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.fish;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.meat;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.meats;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.mushrooms;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.poultry;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.pumpkin;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.rice;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.roots;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.seafood;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.sugar;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.vegetables;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.walnut;
import static com.teammoeg.caupona.datagen.CPRecipeProvider.greens;

import java.nio.file.Path;

import com.google.common.collect.ImmutableList;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.Main;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPItemTagGenerator extends TagsProvider<Item> {

	public CPItemTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Registry.ITEM, modId, existingFileHelper);
	}

	static final String fd = "farmersdelight";
	static final String sf = "simplefarming:";

	@Override
	protected void addTags() {
		/*
		 * Builder<Item>
		 * i=this.getOrCreateBuilder(ItemTags.createOptional(mrl("cookable"))).add(Items
		 * .EGG);
		 * for(Item it:ForgeRegistries.ITEMS.getValues()) {
		 * if(it.isFood()) {
		 * if(it.getRegistryName().getNamespace().equals("minecraft"))
		 * i.add(it);
		 * else
		 * i.addOptional(it.getRegistryName());
		 * }
		 * }
		 */
		for(String wood:CPBlocks.woods) {

	
			tag(ItemTags.LEAVES).add(cp(wood+"_leaves"));
			tag(ItemTags.SAPLINGS).add(cp(wood+"_sapling"));
			
			tag(ItemTags.DOORS).add(cp(wood+"_door"));
			tag(ItemTags.WOODEN_DOORS).add(cp(wood+"_door"));
			
			tag(ItemTags.FENCES).add(cp(wood+"_fence"));
			tag(ItemTags.WOODEN_FENCES).add(cp(wood+"_fence"));
			tag(ftag("fence_gates")).add(cp(wood+"_fence_gate"));
			tag(ftag("fence_gates/wooden")).add(cp(wood+"_fence_gate"));
			tag(ItemTags.TRAPDOORS).add(cp(wood+"_trapdoor"));
			tag(ItemTags.WOODEN_TRAPDOORS).add(cp(wood+"_trapdoor"));
			
			tag(ItemTags.WOODEN_PRESSURE_PLATES).add(cp(wood+"_pressure_plate"));
			
			tag(ItemTags.LOGS).add(cp(wood+"_wood")).add(cp(wood+"_log"));
			
			tag(ItemTags.SLABS).add(cp(wood+"_slab"));
			tag(ItemTags.WOODEN_SLABS).add(cp(wood+"_slab"));
			
			tag(ItemTags.PLANKS).add(cp(wood+"_planks"));
			
			tag(ItemTags.STAIRS).add(cp(wood+"_stairs"));
			tag(ItemTags.WOODEN_STAIRS).add(cp(wood+"_stairs"));
			
			tag(ItemTags.WOODEN_BUTTONS).add(cp(wood+"_button"));
			
			
			tag(ItemTags.SIGNS).add(cp(wood+"_sign"));
		}
		for(String wood:ImmutableList.of("fig","wolfberry")) {
			tag(ItemTags.LEAVES).add(cp(wood+"_leaves"));
			tag(ItemTags.SAPLINGS).add(cp(wood+"_sapling"));
			tag(ItemTags.LOGS).add(cp(wood+"_log"));
		}
		for (String stone : CPBlocks.stones) {
			tag(ItemTags.SLABS).add(cp(stone + "_slab"));
			tag(ItemTags.STAIRS).add(cp(stone + "_stairs"));
			tag(ItemTags.WALLS).add(cp(stone + "_wall"));
		}
		for(String s:CPItems.aspics) {
			tag("aspics").add(cp(s));
		}
		tag("fuel/woods").addTags(ItemTags.LOGS,ItemTags.PLANKS,ItemTags.WOODEN_BUTTONS,ItemTags.WOODEN_DOORS,ItemTags.WOODEN_FENCES,
				ItemTags.WOODEN_PRESSURE_PLATES,ItemTags.WOODEN_SLABS,ItemTags.WOODEN_STAIRS,ItemTags.WOODEN_TRAPDOORS,ItemTags.SAPLINGS);
		tag("fuel/charcoals").add(Items.CHARCOAL);
		tag("fuel/fossil").addTags(ItemTags.COALS);
		tag("fuel/lava").add(Items.LAVA_BUCKET);
		tag(meats).addTag(atag(poultry)).addTag(atag(meat));
		tag(seafood).addTag(atag(fish)).addTag(atag(crustaceans));
		tag(pumpkin).addOptional(rl(fd + ":pumpkin_slice")).add(Items.PUMPKIN, Items.CARVED_PUMPKIN);
		tag(vegetables).addTag(atag(mushrooms)).addTag(atag(roots)).addTag(ftag("salad_ingredients"))
				.addTag(atag(pumpkin));
		tag(frl("raw_beef")).add(Items.BEEF);
		tag(walnut).add(cp("walnut"));
		tag(baked).add(Items.BREAD).addTag(ftag("pasta"))
				.addOptional(rl(fd + ":pie_crust"));
		tag(cereals).addTag(atag(rice)).addTag(ftag("grain")).addTag(atag(baked)).add(Items.WHEAT, Items.WHEAT_SEEDS)
				.addTag(ftag("bread"));
		tag(rice).addTag(ftag("grain/rice"));
		tag(roots).add(Items.POTATO, Items.BAKED_POTATO).addTag(ftag("rootvegetables"));
		tag(vegetables).add(Items.CARROT, Items.BEETROOT, Items.PUMPKIN).addTag(atag(mushrooms))
				.addTag(ftag("vegetables")).addTag(ftag("vegetable"));
		tag(greens).addTag(ftag("vegetables/asparagus")).add(Items.FERN, Items.LARGE_FERN,Items.ALLIUM);
		tag(eggs).add(Items.EGG).addTag(ftag("cooked_eggs"));
		tag(crustaceans).add(Items.NAUTILUS_SHELL);
		tag(fish).addTag(atag(mcrl("fishes"))).addTag(ftag("raw_fishes"));
		tag(seafood).add(Items.KELP, Items.DRIED_KELP);
		tag(poultry).add(Items.CHICKEN, Items.RABBIT).addTag(ftag("raw_chicken")).addTag(ftag("raw_rabbit"))
				.addTag(ftag("bread")).addOptional(rl(sf+"raw_chicken_wings")).addOptional(rl(sf+"raw_sausage")).addOptional(rl(sf+"raw_horse_meat"));
		tag(meat).add(Items.BEEF, Items.MUTTON, Items.PORKCHOP, Items.ROTTEN_FLESH).addTag(ftag("bacon"))
				.addTag(ftag("raw_pork")).addTag(ftag("raw_beef")).addTag(ftag("raw_mutton"))
				.addOptional(rl(fd + ":ham")).addTag(ftag("raw_bacon"));
		tag(sugar).add(Items.SUGAR_CANE, Items.HONEYCOMB, Items.HONEY_BOTTLE);
		tag("bone").add(Items.BONE);
		tag("ice").add(Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE);
		tag(mushrooms).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM).addTag(ftag("mushrooms"));
		tag("fern").add(Items.FERN, Items.LARGE_FERN);
		tag("wolfberries").add(cp("wolfberries"));
		tag("stews").add(CPItems.stews.toArray(new Item[0]));
		tag("stoves").add(CPBlocks.stove1.asItem(),CPBlocks.stove2.asItem(),CPBlocks.stove3.asItem(),CPBlocks.stove4.asItem(),CPBlocks.stove5.asItem());
		tag("portable_brazier_fuel").add(Items.MAGMA_CREAM).add(cp("vivid_charcoal"));
		tag("garum_fish").add(Items.COD,Items.SALMON);
		tag("vinegar_fruits").add(Items.APPLE).add(cp("fig"));
		tag("vinegar_fruits_small").add(Items.SWEET_BERRIES).add(cp("wolfberries"));
		
	}

	private TagAppender<Item> tag(String s) {
		return this.tag(ItemTags.create(mrl(s)));
	}

	private TagAppender<Item> tag(ResourceLocation s) {
		return this.tag(ItemTags.create(s));
	}

	private ResourceLocation rl(RegistryObject<Item> it) {
		return it.getId();
	}

	private ResourceLocation rl(String r) {
		return new ResourceLocation(r);
	}

	private TagKey<Item> otag(String s) {
		return ItemTags.create(mrl(s));
	}

	private TagKey<Item> atag(ResourceLocation s) {
		return ItemTags.create(s);
	}

	private ResourceLocation mrl(String s) {
		return new ResourceLocation(Main.MODID, s);
	}

	private ResourceLocation frl(String s) {
		return new ResourceLocation("forge", s);
	}

	private TagKey<Item> ftag(String s) {
		TagKey<Item> tag = ItemTags.create(new ResourceLocation("forge", s));
		this.tag(tag);
		return tag;
	}

	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return Main.MODID + " item tags";
	}
	private Item cp(String s) {
		Item i=ForgeRegistries.ITEMS.getValue(mrl(s));
		return i.asItem();//just going to cause trouble if not exists
	}
	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/items/" + id.getPath() + ".json");
	}
}
