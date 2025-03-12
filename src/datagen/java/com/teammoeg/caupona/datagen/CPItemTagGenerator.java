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

package com.teammoeg.caupona.datagen;

import static com.teammoeg.caupona.CPTags.Items.BAKED;
import static com.teammoeg.caupona.CPTags.Items.CEREALS;
import static com.teammoeg.caupona.CPTags.Items.CRUSTACEANS;
import static com.teammoeg.caupona.CPTags.Items.EGGS;
import static com.teammoeg.caupona.CPTags.Items.FISH;
import static com.teammoeg.caupona.CPTags.Items.GREENS;
import static com.teammoeg.caupona.CPTags.Items.MEAT;
import static com.teammoeg.caupona.CPTags.Items.MEATS;
import static com.teammoeg.caupona.CPTags.Items.MUSHROOMS;
import static com.teammoeg.caupona.CPTags.Items.POULTRY;
import static com.teammoeg.caupona.CPTags.Items.PUMPKIN;
import static com.teammoeg.caupona.CPTags.Items.RICE;
import static com.teammoeg.caupona.CPTags.Items.ROOTS;
import static com.teammoeg.caupona.CPTags.Items.SEAFOOD;
import static com.teammoeg.caupona.CPTags.Items.SUGAR;
import static com.teammoeg.caupona.CPTags.Items.VEGETABLES;
import static com.teammoeg.caupona.CPTags.Items.WALNUT;

import com.google.common.collect.ImmutableList;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPTags;
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
		for (String wood : CPBlocks.woods) {

			tag(ItemTags.LEAVES).add(cp(wood + "_leaves"));
			tag(ItemTags.SAPLINGS).add(cp(wood + "_sapling"));

			tag(ItemTags.DOORS).add(cp(wood + "_door"));
			tag(ItemTags.WOODEN_DOORS).add(cp(wood + "_door"));

			tag(ItemTags.FENCES).add(cp(wood + "_fence"));
			tag(ItemTags.WOODEN_FENCES).add(cp(wood + "_fence"));
			tag(ftag("fence_gates")).add(cp(wood + "_fence_gate"));
			tag(ftag("fence_gates/wooden")).add(cp(wood + "_fence_gate"));
			tag(ItemTags.TRAPDOORS).add(cp(wood + "_trapdoor"));
			tag(ItemTags.WOODEN_TRAPDOORS).add(cp(wood + "_trapdoor"));

			tag(ItemTags.WOODEN_PRESSURE_PLATES).add(cp(wood + "_pressure_plate"));
			tag(wood+"_log").add(cp(wood + "_wood")).add(cp(wood + "_log")).add(cp("stripped_" + wood + "_wood")).add(cp("stripped_" + wood + "_log"));
			tag(ItemTags.LOGS).add(cp(wood + "_wood")).add(cp(wood + "_log")).add(cp("stripped_" + wood + "_wood")).add(cp("stripped_" + wood + "_log"));

			tag(ItemTags.SLABS).add(cp(wood + "_slab"));
			tag(ItemTags.WOODEN_SLABS).add(cp(wood + "_slab"));

			tag(ItemTags.PLANKS).add(cp(wood + "_planks"));

			tag(ItemTags.STAIRS).add(cp(wood + "_stairs"));
			tag(ItemTags.WOODEN_STAIRS).add(cp(wood + "_stairs"));

			tag(ItemTags.WOODEN_BUTTONS).add(cp(wood + "_button"));

			tag(ItemTags.SIGNS).add(cp(wood + "_sign"));
		}
		for (String wood : ImmutableList.of("fig", "wolfberry")) {
			tag(ItemTags.LEAVES).add(cp(wood + "_leaves"));
			tag(ItemTags.SAPLINGS).add(cp(wood + "_sapling"));
			tag(ItemTags.LOGS).add(cp(wood + "_log"));
		}
		for (String stone : CPBlocks.stones) {
			tag(ItemTags.SLABS).add(cp(stone + "_slab"));
			tag(ItemTags.STAIRS).add(cp(stone + "_stairs"));
			tag(ItemTags.WALLS).add(cp(stone + "_wall"));
		}
		for (String s : CPItems.aspics) {
			tag("aspics").add(cp(s));
		}
		tag("fuel/woods").addTags(ItemTags.LOGS, ItemTags.PLANKS, ItemTags.WOODEN_BUTTONS, ItemTags.WOODEN_DOORS,
				ItemTags.WOODEN_FENCES, ItemTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_SLABS, ItemTags.WOODEN_STAIRS,
				ItemTags.WOODEN_TRAPDOORS, ItemTags.SAPLINGS);
		tag("fuel/charcoals").add(Items.CHARCOAL);
		tag("fuel/fossil").addTags(ItemTags.COALS);
		tag("fuel/lava").add(Items.LAVA_BUCKET);
		tag(MEATS).addTag(POULTRY).addTag(MEAT);
		tag(SEAFOOD).addTag(FISH).addTag(CRUSTACEANS);
		tag(PUMPKIN).addOptional(rl(fd + ":pumpkin_slice")).add(Items.PUMPKIN, Items.CARVED_PUMPKIN);
		tag(frl("raw_beef")).add(Items.BEEF);
		tag(WALNUT).add(cp("walnut"));
		tag(BAKED).add(Items.BREAD).addTag(ftag("pasta")).addOptional(rl(fd + ":pie_crust"));
		tag(CEREALS).addTag(RICE).addTag(ftag("grain")).addTag(BAKED).add(Items.WHEAT, Items.WHEAT_SEEDS)
				.addTag(ftag("bread"));
		tag(RICE).addTag(ftag("grain/rice"));
		tag(ROOTS).add(Items.POTATO, Items.BAKED_POTATO).addTag(ftag("rootvegetables"));
		tag(VEGETABLES).add(Items.CARROT, Items.BEETROOT, Items.PUMPKIN)
		.addTag(ftag("vegetables")).addTag(ftag("vegetable")).addTag(GREENS)
		.addTag(MUSHROOMS).addTag(ROOTS).addTag(ftag("salad_ingredients"))
		.addTag(PUMPKIN);
		
		
		tag(GREENS).addTag(ftag("vegetables/asparagus")).add(Items.FERN, Items.LARGE_FERN, Items.ALLIUM);
		tag(EGGS).add(Items.EGG).addTag(ftag("cooked_eggs"));
		tag(CRUSTACEANS).add(Items.NAUTILUS_SHELL);
		tag(FISH).addTag(atag(mcrl("fishes"))).addTag(ftag("raw_fishes"));
		tag(SEAFOOD).add(Items.KELP, Items.DRIED_KELP);
		tag(POULTRY).add(Items.CHICKEN, Items.RABBIT).addTag(ftag("raw_chicken")).addTag(ftag("raw_rabbit"))
				.addTag(ftag("bread")).addOptional(rl(sf + "raw_chicken_wings")).addOptional(rl(sf + "raw_sausage"))
				.addOptional(rl(sf + "raw_horse_meat"));
		tag(MEAT).add(Items.BEEF, Items.MUTTON, Items.PORKCHOP, Items.ROTTEN_FLESH).addTag(ftag("bacon"))
				.addTag(ftag("raw_pork")).addTag(ftag("raw_beef")).addTag(ftag("raw_mutton"))
				.addOptional(rl(fd + ":ham")).addTag(ftag("raw_bacon"));
		tag(SUGAR).add(Items.SUGAR_CANE, Items.HONEYCOMB, Items.HONEY_BOTTLE);
		tag("bone").add(Items.BONE);
		tag("ice").add(Items.ICE, Items.BLUE_ICE, Items.PACKED_ICE);
		tag(MUSHROOMS).add(Items.BROWN_MUSHROOM, Items.RED_MUSHROOM).addTag(ftag("mushrooms"));
		tag("fern").add(Items.FERN, Items.LARGE_FERN);
		tag("wolfberries").add(cp("wolfberries"));
		tag("stews").add(CPItems.stews.toArray(new Item[0]));
		tag("stoves").add(CPBlocks.stove1.get().asItem(), CPBlocks.stove2.get().asItem(), CPBlocks.stove3.get().asItem(),
				CPBlocks.stove4.get().asItem(), CPBlocks.stove5.get().asItem());
		tag("portable_brazier_fuel").add(Items.MAGMA_CREAM).add(cp("vivid_charcoal"));
		tag("garum_fish").add(Items.COD, Items.SALMON);
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
		Item i = ForgeRegistries.ITEMS.getValue(mrl(s));
		return i.asItem();// just going to cause trouble if not exists
	}
/*
	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/items/" + id.getPath() + ".json");
	}*/
}
