/*
 * Copyright (c) 2024 TeamMoeg
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.decoration.SpokedFenceBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicMaterial;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicPattern;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.plants.FruitBlock;
import com.teammoeg.caupona.util.MaterialType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator.Empty;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
public class CPStatesProvider extends BlockModelGenerators {
	protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(),
		BlockPos.ZERO.above(2));
	protected static final Map<Identifier, String> generatedParticleTextures = new HashMap<>();
	String modid;
	ResourceManager input;
	public CPStatesProvider(ResourceManager input,Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput, String modid) {
		super(blockStateOutput, itemModelOutput, modelOutput);
		this.modid = modid;
		this.input=input;
	}

	@Override
	protected void registerStatesAndModels() {
		horizontalAxisBlock(CPBlocks.STEW_POT.get(), bmf("stew_pot"));
		horizontalAxisBlock(CPBlocks.STEW_POT_LEAD.get(), bmf("lead_stew_pot"));
		this.blockStateOutput.accept(horizontalMultipart(this.getMultipartBuilder(CPBlocks.T_BENCH.get()), bmf("tessellation_workbench")));
		blockItemModel("tessellation_workbench");
		CPBlocks.stoves.forEach(e -> stove(e.get()));
		itemModels().basicItem(CPBlocks.STEW_POT.get().asItem());
		itemModels().basicItem(CPBlocks.STEW_POT_LEAD.get().asItem());
		this.blockStateOutput.accept(createSimpleBlock(CPBlocks.BOWL.get(), bmf("bowl_of_liquid")));
		this.blockStateOutput.accept(this.horizontalMultipart(this.getMultipartBuilder(CPBlocks.KITCHEN_RAIL.get()), bmf("kitchen_rail")));
		blockItemModel("kitchen_rail");
		this.blockStateOutput.accept(createSimpleBlock(CPBlocks.SNAIL_BAIT.get(), createRotatedVariants(bmfs("snail_bait"))));
		this.blockStateOutput.accept(
		this.getVariantBuilder(CPBlocks.SNAIL.get()).with(PropertyDispatch.initial(FruitBlock.AGE)
			.select(0, super.createRotatedVariants(bmfs("snail_stage_1")))
			.select(1, super.createRotatedVariants(bmfs("snail_stage_2")))
			.select(2, super.createRotatedVariants(bmfs("snail_stage_3")))
			.select(3, super.createRotatedVariants(bmfs("snail_stage_4")))
			.select(4, super.createRotatedVariants(bmfs("snail_stage_5")))
			.select(5, super.createRotatedVariants(bmfs("snail_stage_5")))
			.select(6, super.createRotatedVariants(bmfs("snail_stage_5")))
			.select(7, super.createRotatedVariants(bmfs("snail_stage_5")))
			));
		this.blockStateOutput.accept(
		this.getVariantBuilder(CPBlocks.LOAF_DOUGH.get())
		.with(PropertyDispatch.initial(SlabBlock.TYPE)
			.select(SlabType.TOP, bmf("loaf_dough_top"))
			.select(SlabType.BOTTOM, bmf("loaf_dough_bottom"))
			.select(SlabType.DOUBLE, bmf("loaf_dough_top_bottom"))
			));
		blockItemModel(CPBlocks.LOAF_DOUGH, bmf("loaf_dough"));
		this.blockStateOutput.accept(
		this.getVariantBuilder(CPBlocks.LOAF.get())
		.with(PropertyDispatch.initial(SlabBlock.TYPE)
			.select(SlabType.TOP, bmf("loaf_top"))
			.select(SlabType.BOTTOM, bmf("loaf_bottom"))
			.select(SlabType.DOUBLE, bmf("loaf_top_bottom"))
			));
		blockItemModel(CPBlocks.LOAF, bmf("loaf"));
		TextureSlot[] slots= new TextureSlot[]{TextureSlot.create("0"),TextureSlot.create("1")};
		ModelTemplate[] models=new ModelTemplate[] {
			new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/template_mosaic_tile_0" )),Optional.empty(),slots[0],TextureSlot.PARTICLE),
			new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/template_mosaic_tile_1" )),Optional.empty(),slots[1],TextureSlot.PARTICLE)
		};
		MultiPartGenerator mosaic = this.getMultipartBuilder(CPBlocks.MOSAIC.get());
		for (MosaicMaterial m : MosaicMaterial.values())
			for (MosaicPattern p : MosaicPattern.values())
				for (int i : new int[] { 0, 1 }) {
					Material mat=new Material(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/mosaic/components/mosaic_" + p + "_" + m.shortName + "_" + i));
					TextureMapping tm=new TextureMapping().put(slots[i], mat)
					.put(TextureSlot.PARTICLE, mat);
					;
					mosaic=this.horizontalMultipart(mosaic,bmf(models[i].create(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/mosaic/mosaic_" + p + "_" + m.shortName + "_" + i), null, modelOutput)), b -> b.term(MosaicBlock.MATERIAL[i], m).term(MosaicBlock.PATTERN, p));
				}
		this.blockStateOutput.accept(mosaic);

		for(String s:CPItems.dishes) {
			this.blockStateOutput.accept(this.getMultipartBuilder(cpblock(s))
			.with(bmf("dish"))
			.with(bmf("plate_dishes/"+s)));
			this.blockStateOutput.accept(this.getMultipartBuilder(cpblock(s+"_loaf"))
			.with(bmf("bread_bowl"))
			.with(bmf("bread_bowl_dishes/"+s)));
		}
		// itemModels().getBuilder("mosaic").parent(new
		// UncheckedModelFile(Identifier.fromNamespaceAndPath("builtin/entity")));
		/*
		 * this.getVariantBuilder(CPBlocks.MOSAIC.get()).forAllStates(t->{ MosaicPattern
		 * p=t.getValue(MosaicBlock.PATTERN); MosaicMaterial
		 * m1=t.getValue(MosaicBlock.MATERIAL_1); MosaicMaterial
		 * m2=t.getValue(MosaicBlock.MATERIAL_2); return
		 * ConfiguredModel.builder().modelFile(bmf("mosaic/mosaic_"+p+"_"+m1.shortName+
		 * "_"+m2.shortName)).build(); });
		 */
		// super.stairsBlock(null, modid, null, null, null);
		for (MaterialType rtype : CPBlocks.all_materials) {
			String stone = rtype.getName();
			if (rtype.isDecorationMaterial()) {
				for (String type : ImmutableSet.of("", "_slab", "_stairs"))
					blockItemModel(stone + type);
				blockItemModel(stone + "_wall", "_inventory");
			}
			if (rtype.isCounterMaterial()) {
				for (String type : ImmutableSet.of("_chimney_flue", "_chimney_pot", "_counter", "_counter_with_dolium"))
					blockItemModel(stone + type);
			}

			if (rtype.isPillarMaterial()) {
				for (String type : ImmutableSet.of("_column_fluted_plinth", "_column_fluted_shaft", "_column_shaft",
					"_column_plinth", "_ionic_column_capital", "_tuscan_column_capital",
					"_acanthine_column_capital"))
					blockItemModel(stone + type);
				simpleBlockItem(cpblock(stone + "_lacunar_tile"), bmf(stone + "_lacunar_tile"));
				itemModel(cpblock(stone + "_spoked_fence"), bmf(stone + "_spoked_fence_inventory"));
				this.getMultipartBuilder(cpblock(stone + "_spoked_fence"))
					.part().modelFile(bmf(stone + "_spoked_fence_side")).rotationY(270)
					.addModel().condition(SpokedFenceBlock.WEST_WALL, true).end()
					.part().modelFile(bmf(stone + "_spoked_fence_side")).rotationY(0)
					.addModel().condition(SpokedFenceBlock.NORTH_WALL, true).end()
					.part().modelFile(bmf(stone + "_spoked_fence_side")).rotationY(90)
					.addModel().condition(SpokedFenceBlock.EAST_WALL, true).end()
					.part().modelFile(bmf(stone + "_spoked_fence_side")).rotationY(180)
					.addModel().condition(SpokedFenceBlock.SOUTH_WALL, true).end()
					.part().modelFile(bmf(stone + "_spoked_fence_post"))
					.addModel().end();
			}
			if (rtype.isHypocaustMaterial()) {
				blockItemModel(stone + "_hypocaust_firebox");
				blockItemModel(stone + "_caliduct");
			}
			if (rtype.isRoadMaterial()) {
				roadBlock(stone);

			}
		}
		MultiPartBlockStateBuilder boat = horizontalMultipart(this.getMultipartBuilder(CPBlocks.GRAVY_BOAT.get()),
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
			blockItemModelBuilder(wood + "_fruits", "_stage_3").transforms().transform(ItemDisplayContext.GUI).scale(1f)
				.rotation(0, 0.1f, 0).translation(0, 0, 0).end().end();

			blockItemModel("stripped_" + wood + "_log");
			blockItemModel("stripped_" + wood + "_wood");
	        super.models().sign(wood+"_hanging_sign",modLoc("block/"+wood+"_planks"));
	        super.models().sign(wood+"_wall_hanging_sign",modLoc("block/"+wood+"_planks"));
			// blockItemModel(wood+"_trapdoor","_top")

		}
		blockItemModel(Utils.getRegistryName(CPBlocks.STONE_PAN).getPath());
		blockItemModel(Utils.getRegistryName(CPBlocks.COPPER_PAN).getPath());
		blockItemModel(Utils.getRegistryName(CPBlocks.IRON_PAN).getPath());
		blockItemModel(Utils.getRegistryName(CPBlocks.LEAD_PAN).getPath());
		blockItemModel("wolf_statue", "_1");
		blockItemModel("fumarole_boulder");
		blockItemModel("fumarole_vent");
		blockItemModel("litharge_fumarole_boulder");
		blockItemModel("litharge_fumarole_vent");
		blockItemModel("litharge_bloom");
		blockItemModel("pumice");
		blockItemModel("pumice_bloom");
		blockItemModel("lead_block");
		blockItemModel("snail_bait");
		blockItemModel("snail_mucus");
		simpleBlock(CPBlocks.SNAIL_MUCUS.get(), bmf("snail_mucus"));
		simpleBlock(CPBlocks.LEAD_BLOCK.get(), bmf("lead_block"));
		simpleBlock(CPBlocks.LOAF_BOWL.get(),bmf("bread_bowl"));
		// itemModels().getBuilder("snail_block").parent(bmf("snail_stage_5")).transforms().transform(ItemDisplayContext.GUI).scale(1.5f).rotation(0,
		// 45, 180).translation(0, 4, 0).end().end();

		for (String bush : ImmutableSet.of("wolfberry", "fig")) {
			blockItemModel(bush + "_log");
			blockItemModelBuilder(bush + "_fruits", "_stage_3").transforms().transform(ItemDisplayContext.GUI).scale(1f)
				.rotation(0, 45, 0).translation(0, 1, 0).end().end();
			blockItemModel(bush + "_leaves");
		}

	}
	protected Empty getVariantBuilder(Block blk) {
		return MultiVariantGenerator.dispatch(blk);
	}

	public void roadBlock(String name) {

		itemModels().getBuilder(name + "_road_side").parent(bmf("roads/" + name + "_road_side"));
		
		itemModels().getBuilder(name + "_road").parent(bmf("roads/" + name + "_road"));
		getVariantBuilder(cpblock(name + "_road_side")).forAllStates(state -> {
			Direction facing = state.getValue(StairBlock.FACING);
			StairsShape shape = state.getValue(StairBlock.SHAPE);
			int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees
																// clockwise for some reason
			if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
				yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
			}
			yRot %= 360;
			Builder<?> builder = null;
			String ext = shape == StairsShape.STRAIGHT ? "_side"
				: shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? "_outer_corner"
					: "_inner_corner";
			int i = 0;
			while (true) {
				Identifier rl = Identifier.fromNamespaceAndPath(this.modid, "block/roads/" + name + "_road" + ext + "_" + i);
				if (!existingFileHelper.exists(rl, MODEL))
					break;
				if (builder == null)
					builder = ConfiguredModel.builder();
				else
					builder = builder.nextModel();
				builder = builder.modelFile(new ModelFile.ExistingModelFile(rl, existingFileHelper)).rotationY(yRot);
				i++;

			}
			return builder.build();

		});
		Builder<?> builder = null;
		int i = 0;
		while (true) {
			Identifier rl = Identifier.fromNamespaceAndPath(this.modid, "block/roads/" + name + "_road_" + i);
			if (!existingFileHelper.exists(rl, MODEL))
				break;
			i++;
			if (builder == null)
				builder = ConfiguredModel.builder();
			else
				builder = builder.nextModel();
			builder = builder.modelFile(new ModelFile.ExistingModelFile(rl, existingFileHelper));
		}
		this.getVariantBuilder(cpblock(name + "_road")).partialState().addModels(builder.build());
	}

	private Block cpblock(String name) {
		return BuiltInRegistries.BLOCK.get(Identifier.fromNamespaceAndPath(this.modid, name));
	}

	protected void blockItemModel(String n) {
		blockItemModel(n, "");
	}

	protected void blockItemModel(String n, String p) {
		if (this.existingFileHelper.exists(Identifier.fromNamespaceAndPath(CPMain.MODID, "textures/item/" + n + p + ".png"),
			PackType.CLIENT_RESOURCES)) {
			itemModels().basicItem(Identifier.fromNamespaceAndPath(CPMain.MODID, n));
		} else {
			itemModels().getBuilder(n).parent(bmf(n + p));
		}
	}

	protected void blockItemModel(Holder<Block> n, ModelFile p) {

		itemModels().getBuilder(n.getRegisteredName()).parent(p);
	}

	protected ItemModelBuilder blockItemModelBuilder(String n, String p) {
		return itemModels().getBuilder(n).parent(bmf(n + p));
	}

	public void stove(Block block) {
		horizontalMultipart(
			horizontalMultipart(this.getMultipartBuilder(block),
				bmf(Utils.getRegistryName(block).getPath())),
			bmf("kitchen_stove_fuel"), i -> i);
		itemModel(block, bmf(Utils.getRegistryName(block).getPath()));

	}
	public boolean existsFile(Identifier id){
		return input.getResource(id.withPrefix("models").withSuffix(".json")).isPresent();
		
	}
	public MultiVariant bmf(String name) {
		return super.variant(bmfs(name));
	}
	public Variant bmfs(String name) {
		Identifier orl = Identifier.fromNamespaceAndPath(this.modid, "block/" + name);
		Identifier rl = orl;
		
		if (!existsFile(rl)) {// not exists, let's guess
			List<String> rn = Arrays.asList(name.split("_"));
			for (int i = rn.size(); i >= 0; i--) {
				List<String> rrn = new ArrayList<>(rn);
				rrn.add(i, "0");
				rl = Identifier.fromNamespaceAndPath(this.modid, "block/" + String.join("_", rrn));
				if (existsFile(rl))
					return super.plainModel(rl);
			}

		}
		CPMain.logger.warn("Model file "+orl+" not exists, using unchecked");
		return super.plainModel(rl);
	}
	public MultiVariant bmf(Identifier name) {
		return super.variant(bmfs(name));
	}
	public Variant bmfs(Identifier rl) {

		return super.plainModel(rl);
	}
	public void simpleBlockItem(String name) {
		simpleBlockItem(cpblock(name), bmf(name));
	}
	public void simpleBlockItem(Block b, ModelFile model) {
		simpleBlockItem(b, new ConfiguredModel(model));
	}

	protected void simpleBlockItem(Block b, ConfiguredModel model) {
		simpleBlock(b, model);
		itemModel(b, model.model);
	}

	public void horizontalAxisBlock(Block block, MultiVariant mf) {

		this.blockStateOutput
        .accept(getVariantBuilder(block).with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_AXIS)
			.select(Axis.Z, mf)
			.select(Axis.X, mf.with(Y_ROT_90))));

	}

	public MultiPartGenerator horizontalMultipart(MultiPartGenerator generator, MultiVariant variant) {
		forEachHorizontalDirection((direction, rotation) -> generator.with(condition(BlockStateProperties.HORIZONTAL_FACING, direction), variant.with(rotation)));
		return generator;
	}

	public MultiPartGenerator horizontalMultipart(MultiPartGenerator generator, MultiVariant variant,
		UnaryOperator<ConditionBuilder> act) {
		forEachHorizontalDirection((direction, rotation) -> generator.with(act.apply(condition(BlockStateProperties.HORIZONTAL_FACING, direction)), variant.with(rotation)));
		
		return generator;
	}
	protected MultiPartGenerator getMultipartBuilder(Block block) {
		return MultiPartGenerator.multiPart(block);
	}
	protected void itemModel(Block block, Model model) {
		itemModels().getBuilder(Utils.getRegistryName(block).getPath()).parent(model);
	}

}
