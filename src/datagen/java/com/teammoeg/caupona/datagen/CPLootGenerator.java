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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTable.Builder;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;

public class CPLootGenerator extends LootTableProvider {

	public CPLootGenerator(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootContextParamSet>> getTables() {
		return Arrays.asList(Pair.of(() -> new LTBuilder(), LootContextParamSets.BLOCK));
	}

	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
		map.forEach((name, table) -> LootTables.validate(validationtracker, name, table));
	}

	private static class LTBuilder extends BlockLoot {
		@Override
		protected void addTables() {
			dropSelf(CPBlocks.stew_pot);
			/*
			 * dropSelf(CPBlocks.stove1);
			 * dropSelf(CPBlocks.stove2);
			 * dropSelf(CPBlocks.stove3);
			 * dropSelf(CPBlocks.stove4);
			 * dropSelf(CPBlocks.stove5);
			 */
			for (String wood : CPBlocks.woods) {
				for (String type : ImmutableSet.of("_button",

						"_fence", "_fence_gate", "_log", "_planks", "_pressure_plate", "_sapling", "_sign", "_slab",
						"_stairs", "_trapdoor", "_wood"))
					dropSelf(cp(wood + type));
				add(cp(wood + "_door"),createDoorTable(cp(wood + "_door")));
				add(cp(wood + "_leaves"),createLeavesDrops(cp(wood + "_leaves"),cp(wood + "_sapling"),0.05F, 0.0625F, 0.083333336F, 0.1F));
				dropOther(cp(wood + "_wall_sign"), cp(wood + "_sign"));
			}

			for (String stone : CPBlocks.stones) {
				for (String type : ImmutableSet.of("", "_slab", "_stairs", "_wall"))
					dropSelf(cp(stone + type));
			}

			for (String stone : CPBlocks.counters) {
				for (String type : ImmutableSet.of("_chimney_flue", "_chimney_pot", "_counter", "_counter_with_dolium",
						"_kitchen_stove"))
					dropSelf(cp(stone + type));
			}
			for(String str:CPBlocks.pillar_materials) {
				for(String type:ImmutableSet.of("_column_fluted_plinth",
						"_column_fluted_shaft",
						"_column_shaft",
						"_column_plinth",
						"_ionic_column_capital",
						"_tuscan_column_capital",
						"_acanthine_column_capital"))
					dropSelf(cp(str+type));
			}
		}

		private Block cp(String name) {
			return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Main.MODID, name));
		}

		ArrayList<Block> added = new ArrayList<>();

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return added;
		}

		@Override
		public void dropOther(Block blockIn, ItemLike drop) {
			added.add(blockIn);
			super.dropOther(blockIn, drop);
		}

		protected void add(Block pBlock, LootTable.Builder pLootTableBuilder) {
			added.add(pBlock);
			super.add(pBlock, pLootTableBuilder);
		}

	}
}
