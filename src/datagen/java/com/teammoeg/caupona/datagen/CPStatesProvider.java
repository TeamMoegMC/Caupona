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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.stove.KitchenStove;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ExistingFileHelper.ResourceType;
import net.minecraftforge.registries.ForgeRegistries;

public class CPStatesProvider extends BlockStateProvider {
	protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(),
			BlockPos.ZERO.above(2));
	protected static final ResourceType MODEL = new ResourceType(PackType.CLIENT_RESOURCES, ".json", "models");
	protected static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();
	protected final ExistingFileHelper existingFileHelper;
	String modid;

	public CPStatesProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
		super(gen, modid, exFileHelper);
		this.modid = modid;
		this.existingFileHelper = exFileHelper;
	}

	@Override
	protected void registerStatesAndModels() {
		horizontalAxisBlock(CPBlocks.stew_pot, bmf("stew_pot"));

		stove(CPBlocks.stove1);
		stove(CPBlocks.stove2);
		stove(CPBlocks.stove3);
		stove(CPBlocks.stove4);
		stove(CPBlocks.stove5);
		itemModel(CPBlocks.stew_pot, bmf("stew_pot"));
		simpleBlock(CPBlocks.bowl, bmf("bowl_of_liquid"));
		for (String stone : CPBlocks.stones) {
			for (String type : ImmutableSet.of("", "_slab", "_stairs"))
				blockItemModel(stone + type);
			blockItemModel(stone + "_wall", "_inventory");
		}
		for (String mat : CPBlocks.counters) {
			for (String type : ImmutableSet.of("_chimney_flue", "_chimney_pot", "_counter", "_counter_with_dolium"))
				blockItemModel(mat + type);
		}

		for (String str : CPBlocks.pillar_materials) {
			for (String type : ImmutableSet.of("_column_fluted_plinth", "_column_fluted_shaft", "_column_shaft",
					"_column_plinth", "_ionic_column_capital", "_tuscan_column_capital", "_acanthine_column_capital"))
				blockItemModel(str + type);
		}
		MultiPartBlockStateBuilder boat = horizontalMultipart(this.getMultipartBuilder(CPBlocks.GRAVY_BOAT),
				bmf("gravy_boat"));
		int i = 0;
		for (String s : ImmutableSet.of("_oil_0", "_oil_1", "_oil_2", "_oil_3", "_oil_4")) {
			int j = i++;
			boat = horizontalMultipart(boat, bmf("gravy_boat" + s), c -> c.condition(GravyBoatBlock.LEVEL, j));
		}
		for (String wood : CPBlocks.woods) {
			for (String type : ImmutableSet.of(

					"_fence_gate", "_leaves", "_log", "_planks", "_pressure_plate", "_slab", "_stairs", "_wood"))
				blockItemModel(wood + type);
			blockItemModel(wood + "_fence", "_inventory");
			blockItemModel(wood + "_button", "_inventory");
			blockItemModel(wood + "_fruits", "_stage_1");

			blockItemModel("stripped_" + wood + "_log");
			blockItemModel("stripped_" + wood + "_wood");
			blockItemModel(CPBlocks.STONE_PAN.getRegistryName().getPath());
			blockItemModel(CPBlocks.COPPER_PAN.getRegistryName().getPath());
			blockItemModel(CPBlocks.IRON_PAN.getRegistryName().getPath());
			// blockItemModel(wood+"_trapdoor","_top");

		}
		for (String s : CPBlocks.hypocaust_materials) {
			blockItemModel(s + "_hypocaust_firebox");
			blockItemModel(s + "_caliduct");
		}
		blockItemModel("wolf_statue", "_1");
		blockItemModel("fumarole_boulder");
		blockItemModel("fumarole_vent");
		blockItemModel("pumice");
		blockItemModel("pumice_bloom");
		for (String bush : ImmutableSet.of("wolfberry", "fig")) {
			blockItemModel(bush + "_log");
			blockItemModel(bush + "_fruits", "_stage_1");
			blockItemModel(bush + "_leaves");
		}
	}

	private Block cpblock(String name) {
		return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(this.modid, name));
	}

	protected void blockItemModel(String n) {
		itemModels().getBuilder(n).parent(bmf(n));
	}

	protected void blockItemModel(String n, String p) {
		itemModels().getBuilder(n).parent(bmf(n + p));
	}

	public void stove(Block block) {
		horizontalMultipart(
				horizontalMultipart(
						horizontalMultipart(
								horizontalMultipart(this.getMultipartBuilder(block),
										bmf(block.getRegistryName().getPath())).part()
												.modelFile(bmf("kitchen_stove_cold_ash")).addModel()
												.condition(KitchenStove.LIT, false).condition(KitchenStove.ASH, true)
												.end().part().modelFile(bmf("kitchen_stove_hot_ash")).addModel()
												.condition(KitchenStove.LIT, true).end(),
								bmf("kitchen_stove_charcoal"), i -> i.condition(KitchenStove.FUELED, 1)),
						bmf("kitchen_stove_firewoods"), i -> i.condition(KitchenStove.FUELED, 2)),
				bmf("kitchen_stove_coal"), i -> i.condition(KitchenStove.FUELED, 3));
		itemModel(block, bmf(block.getRegistryName().getPath()));

	}

	public ModelFile bmf(String name) {
		ResourceLocation rl = new ResourceLocation(this.modid, "block/" + name);
		if (!existingFileHelper.exists(rl, MODEL)) {// not exists, let's guess
			List<String> rn = Arrays.asList(name.split("_"));
			for (int i = rn.size(); i >= 0; i--) {
				List<String> rrn = new ArrayList<>(rn);
				rrn.add(i, "0");
				rl = new ResourceLocation(this.modid, "block/" + String.join("_", rrn));
				if (existingFileHelper.exists(rl, MODEL))
					break;
			}

		}
		return new ModelFile.ExistingModelFile(rl, existingFileHelper);
	}

	public void simpleBlockItem(Block b, ModelFile model) {
		simpleBlockItem(b, new ConfiguredModel(model));
	}

	protected void simpleBlockItem(Block b, ConfiguredModel model) {
		simpleBlock(b, model);
		itemModel(b, model.model);
	}

	public void horizontalAxisBlock(Block block, ModelFile mf) {
		getVariantBuilder(block).partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z).modelForState()
				.modelFile(mf).addModel().partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.X)
				.modelForState().modelFile(mf).rotationY(90).addModel();
	}

	public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block, ModelFile mf) {
		return horizontalMultipart(block, mf, UnaryOperator.identity());
	}

	public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block, ModelFile mf,
			UnaryOperator<PartBuilder> act) {
		for (Direction d : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues())
			block = act.apply(block.part().modelFile(mf).rotationY(((int) d.toYRot()) % 360).addModel()
					.condition(BlockStateProperties.HORIZONTAL_FACING, d)).end();
		return block;
	}

	protected void itemModel(Block block, ModelFile model) {
		itemModels().getBuilder(block.getRegistryName().getPath()).parent(model);
	}
}
