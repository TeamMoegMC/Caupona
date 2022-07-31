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

import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPBlockTagGenerator extends TagsProvider<Block> {

	public CPBlockTagGenerator(DataGenerator dataGenerator, String modId, ExistingFileHelper existingFileHelper) {
		super(dataGenerator, Registry.BLOCK, modId, existingFileHelper);
	}

	@Override
	protected void addTags() {
		TagAppender<Block> pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
		tag("stoves").add(CPBlocks.stove1, CPBlocks.stove2, CPBlocks.stove3, CPBlocks.stove4, CPBlocks.stove5);
		pickaxe.add(CPBlocks.stove1, CPBlocks.stove2, CPBlocks.stove3, CPBlocks.stove4, CPBlocks.stove5,
				CPBlocks.stew_pot);
		for (String wood : CPBlocks.woods) {
			for (String type : ImmutableSet.of("_button", "_door", "_fence", "_fence_gate", "_log", "_planks",
					"_pressure_plate", "_sapling", "_sign", "_wall_sign", "_slab", "_stairs", "_trapdoor", "_wood"))
				tag(BlockTags.MINEABLE_WITH_AXE).add(cp(wood + type));
			tag(BlockTags.MINEABLE_WITH_AXE).add(cp("stripped_" + wood + "_log"), cp("stripped_" + wood + "_wood"));
			tag(BlockTags.MINEABLE_WITH_HOE).add(cp(wood + "_leaves")).add(cp(wood + "_fruits"));
			tag(BlockTags.LEAVES).add(cp(wood + "_leaves"));
			tag(BlockTags.SAPLINGS).add(cp(wood + "_sapling"));
			tag(BlockTags.DOORS).add(cp(wood + "_door"));
			tag(BlockTags.WOODEN_DOORS).add(cp(wood + "_door"));
			tag(BlockTags.FENCES).add(cp(wood + "_fence"));
			tag(BlockTags.WOODEN_FENCES).add(cp(wood + "_fence"));
			tag(BlockTags.TRAPDOORS).add(cp(wood + "_trapdoor"));
			tag(BlockTags.PRESSURE_PLATES).add(cp(wood + "_pressure_plate"));
			tag(BlockTags.WOODEN_PRESSURE_PLATES).add(cp(wood + "_pressure_plate"));
			tag(BlockTags.WALL_POST_OVERRIDE).add(cp(wood + "_pressure_plate"));
			tag(BlockTags.LOGS_THAT_BURN).add(cp(wood + "_wood")).add(cp(wood + "_log"),
					cp("stripped_" + wood + "_log"), cp("stripped_" + wood + "_wood"));
			tag(BlockTags.LOGS).add(cp(wood + "_wood")).add(cp(wood + "_log"), cp("stripped_" + wood + "_log"),
					cp("stripped_" + wood + "_wood"));
			tag(BlockTags.SLABS).add(cp(wood + "_slab"));
			tag(BlockTags.WOODEN_SLABS).add(cp(wood + "_slab"));
			tag(BlockTags.PLANKS).add(cp(wood + "_planks"));
			tag(BlockTags.STAIRS).add(cp(wood + "_stairs"));
			tag(BlockTags.WOODEN_STAIRS).add(cp(wood + "_stairs"));
			tag(BlockTags.SIGNS).add(cp(wood + "_sign")).add(cp(wood + "_wall_sign"));
			tag(BlockTags.STANDING_SIGNS).add(cp(wood + "_sign"));
			tag(BlockTags.WALL_SIGNS).add(cp(wood + "_wall_sign"));
			tag(BlockTags.FENCE_GATES).add(cp(wood + "_fence_gate"));
			tag(BlockTags.UNSTABLE_BOTTOM_CENTER).add(cp(wood + "_fence_gate"));

			tag("fruits_growable").add(cp(wood + "_leaves"));
			tag(frl("fence_gates")).add(cp(wood + "_fence_gate"));
			tag(frl("fence_gates/wooden")).add(cp(wood + "_fence_gate"));
		}

		for (String str : CPBlocks.pillar_materials) {
			for (String type : ImmutableSet.of("_column_fluted_plinth", "_column_fluted_shaft", "_column_shaft",
					"_column_plinth", "_ionic_column_capital", "_tuscan_column_capital", "_acanthine_column_capital"))
				pickaxe.add(cp(str + type));
		}
		for (String stone : CPBlocks.stones) {
			pickaxe.add(cp(stone), cp(stone + "_slab"), cp(stone + "_stairs"), cp(stone + "_wall"));
			tag(BlockTags.SLABS).add(cp(stone + "_slab"));
			tag(BlockTags.STAIRS).add(cp(stone + "_stairs"));
			tag(BlockTags.WALLS).add(cp(stone + "_wall"));
		}
		for (String mat : CPBlocks.counters) {
			pickaxe.add(cp(mat + "_chimney_flue"), cp(mat + "_chimney_pot"), cp(mat + "_counter"),
					cp(mat + "_counter_with_dolium"));
			tag("counter").add(cp(mat + "_chimney_flue"), cp(mat + "_chimney_pot"), cp(mat + "_counter"),
					cp(mat + "_counter_with_dolium"));
			tag("chimney").add(cp(mat + "_chimney_flue"));
			tag("chimney_pot").add(cp(mat + "_chimney_pot"));
		}
		tag("pans").add(CPBlocks.STONE_PAN, CPBlocks.COPPER_PAN, CPBlocks.IRON_PAN);
		tag("chimney_ignore")
				.addTags(otag("pans"), BlockTags.SIGNS, BlockTags.BUTTONS, BlockTags.LEAVES, BlockTags.BANNERS,
						BlockTags.CANDLES, BlockTags.WALL_SIGNS, BlockTags.STANDING_SIGNS, BlockTags.CANDLES,
						BlockTags.CORAL_PLANTS, BlockTags.FENCES, BlockTags.WALLS, BlockTags.TRAPDOORS, BlockTags.DOORS,
						BlockTags.FLOWER_POTS, BlockTags.WALL_POST_OVERRIDE, BlockTags.FLOWERS)
				.add(Blocks.AIR, Blocks.VINE, Blocks.CAVE_VINES, CPBlocks.stew_pot, CPBlocks.WOLF);
		tag("fumarole_hot").add(Blocks.MAGMA_BLOCK);
		tag("fumarole_very_hot").add(Blocks.LAVA);
		for (String bush : ImmutableSet.of("wolfberry", "fig")) {
			tag(BlockTags.LOGS).add(cp(bush + "_log"));
			tag(BlockTags.LOGS_THAT_BURN).add(cp(bush + "_log"));
			tag(BlockTags.LEAVES).add(cp(bush + "_leaves"));
			tag("fruits_growable").add(cp(bush + "_leaves"));
			tag(BlockTags.SAPLINGS).add(cp(bush + "_sapling"));
			tag(BlockTags.MINEABLE_WITH_AXE).add(cp(bush + "_log"));
			tag(BlockTags.MINEABLE_WITH_HOE).add(cp(bush + "_leaves")).add(cp(bush + "_fruits"));
		}
		pickaxe.add(CPBlocks.PUMICE_BLOOM, CPBlocks.FUMAROLE_BOULDER, CPBlocks.FUMAROLE_VENT, CPBlocks.PUMICE);
		for (String s : CPBlocks.hypocaust_materials) {
			tag("caliducts").add(cp(s + "_caliduct"));
			tag("heat_conductor").add(cp(s + "_hypocaust_firebox"));
			tag("chimney_ignore").add(cp(s + "_hypocaust_firebox"));
			pickaxe.add(cp(s + "_caliduct")).add(cp(s + "_hypocaust_firebox"));
		}
		pickaxe.add(CPBlocks.WOLF, CPBlocks.STONE_PAN, CPBlocks.COPPER_PAN, CPBlocks.IRON_PAN);
		tag(BlockTags.NEEDS_STONE_TOOL).add(CPBlocks.WOLF, CPBlocks.COPPER_PAN, CPBlocks.IRON_PAN);
		tag("heat_conductor").addTag(otag("caliducts"));

	}

	private TagAppender<Block> tag(String s) {
		return this.tag(BlockTags.create(mrl(s)));
	}

	private Block cp(String s) {
		Block bl = ForgeRegistries.BLOCKS.getValue(mrl(s));
		bl.asItem();// just going to cause trouble if not exists
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

	private TagKey<Block> otag(String s) {
		return BlockTags.create(mrl(s));
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
