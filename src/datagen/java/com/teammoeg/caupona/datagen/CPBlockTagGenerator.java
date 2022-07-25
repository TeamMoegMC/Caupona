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

import java.nio.file.Path;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.ChimneyPotBlock;
import com.teammoeg.caupona.blocks.CounterDoliumBlock;
import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;

import static com.teammoeg.caupona.datagen.CPRecipeProvider.*;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.data.tags.TagsProvider.TagAppender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPBlockTagGenerator extends TagsProvider<Block> {

	public CPBlockTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Registry.BLOCK, modId, existingFileHelper);
	}


	@Override
	protected void addTags() {

		tag("stoves").add(CPBlocks.stove1,CPBlocks.stove2,CPBlocks.stove3,CPBlocks.stove4,CPBlocks.stove5);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CPBlocks.stove1,CPBlocks.stove2,CPBlocks.stove3,CPBlocks.stove4,CPBlocks.stove5);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(CPBlocks.stew_pot);
		for(String wood:CPBlocks.woods) {
			for(String type:ImmutableSet.of(
					"_button",
					"_door",
					"_fence",
					"_fence_gate",
					"_log",
					"_planks",
					"_pressure_plate",
					"_sapling",
					"_sign",
					"_wall_sign",
					"_slab",
					"_stairs",
					"_trapdoor",
					"_wood"))
				tag(BlockTags.MINEABLE_WITH_AXE).add(cp(wood+type));
			tag(BlockTags.MINEABLE_WITH_HOE).add(cp(wood+"_leaves"));
			tag(BlockTags.LEAVES).add(cp(wood+"_leaves"));
			tag(BlockTags.SAPLINGS).add(cp(wood+"_sapling"));
			tag(BlockTags.DOORS).add(cp(wood+"_door"));
			tag(BlockTags.WOODEN_DOORS).add(cp(wood+"_door"));
			tag(BlockTags.FENCES).add(cp(wood+"_fence"));
			tag(BlockTags.WOODEN_FENCES).add(cp(wood+"_fence"));
			tag(BlockTags.TRAPDOORS).add(cp(wood+"_trapdoor"));
			tag(BlockTags.PRESSURE_PLATES).add(cp(wood+"_pressure_plate"));
			tag(BlockTags.WOODEN_PRESSURE_PLATES).add(cp(wood+"_pressure_plate"));
			tag(BlockTags.WALL_POST_OVERRIDE).add(cp(wood+"_pressure_plate"));
			tag(BlockTags.LAVA_POOL_STONE_CANNOT_REPLACE).add(cp(wood+"_log")).add(cp(wood+"_wood")).add(cp(wood+"leaves"));
			tag(BlockTags.LOGS_THAT_BURN).add(cp(wood+"_wood")).add(cp(wood+"_log"));
			tag(BlockTags.LOGS).add(cp(wood+"_wood")).add(cp(wood+"_log"));
			tag(BlockTags.PARROTS_SPAWNABLE_ON).add(cp(wood+"_log")).add(cp(wood+"_wood")).add(cp(wood+"leaves"));
			tag(BlockTags.SLABS).add(cp(wood+"_slab"));
			tag(BlockTags.WOODEN_SLABS).add(cp(wood+"_slab"));
			tag(BlockTags.PLANKS).add(cp(wood+"_planks"));
			tag(BlockTags.STAIRS).add(cp(wood+"_stairs"));
			tag(BlockTags.WOODEN_STAIRS).add(cp(wood+"_stairs"));
			tag(BlockTags.SIGNS).add(cp(wood+"_sign")).add(cp(wood+"_wall_sign"));
			tag(BlockTags.STANDING_SIGNS).add(cp(wood+"_sign"));
			tag(BlockTags.WALL_SIGNS).add(cp(wood+"_wall_sign"));
		}
		TagAppender<Block> pickaxe=tag(BlockTags.MINEABLE_WITH_PICKAXE);
		for(String str:CPBlocks.pillar_materials) {
			for(String type:ImmutableSet.of("_column_fluted_plinth",
					"_column_fluted_shaft",
					"_column_shaft",
					"_column_plinth",
					"_ionic_column_capital",
					"_tuscan_column_capital",
					"_acanthine_column_capital"))
				pickaxe.add(cp(str+type));
		}
		for (String stone : CPBlocks.stones) {
			pickaxe.add(cp(stone),cp(stone + "_slab"),cp(stone + "_stairs"),cp(stone + "_wall"));
			tag(BlockTags.SLABS).add(cp(stone + "_slab"));
			tag(BlockTags.STAIRS).add(cp(stone + "_stairs"));
			tag(BlockTags.WALLS).add(cp(stone + "_wall"));
		}
		for (String mat : CPBlocks.counters) {
			pickaxe.add(cp(mat + "_chimney_flue"),cp(mat + "_chimney_pot"),cp(mat + "_counter"),cp(mat + "_counter_with_dolium"));
		}
	}

	private TagAppender<Block> tag(String s) {
		return this.tag(BlockTags.create(mrl(s)));
	}
	private Block cp(String s) {
		Block bl=ForgeRegistries.BLOCKS.getValue(mrl(s));
		bl.asItem();//just going to cause trouble if not exists
		return bl;
	}
	private TagAppender<Block> tag(ResourceLocation s) {
		return this.tag(BlockTags.create(s));
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


	private ResourceLocation mcrl(String s) {
		return new ResourceLocation(s);
	}

	@Override
	public String getName() {
		return Main.MODID + " block tags";
	}

	@Override
	protected Path getPath(ResourceLocation id) {
		return this.generator.getOutputFolder()
				.resolve("data/" + id.getNamespace() + "/tags/blocks/" + id.getPath() + ".json");
	}
}
