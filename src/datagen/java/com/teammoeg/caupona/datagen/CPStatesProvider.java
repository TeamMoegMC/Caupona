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

import org.joml.Matrix4f;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.math.Transformation;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.decoration.SpokedFenceBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicMaterial;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicPattern;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.plants.FruitBlock;
import com.teammoeg.caupona.client.model.MosaicModel;
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
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelInstance;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class CPStatesProvider extends BlockModelGenerators {
	protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(),
		BlockPos.ZERO.above(2));
	protected static final Map<Identifier, String> generatedParticleTextures = new HashMap<>();
	String modid;
	ResourceManager input;

	public CPStatesProvider(ResourceManager input, Consumer<BlockModelDefinitionGenerator> blockStateOutput, ItemModelOutput itemModelOutput, BiConsumer<Identifier, ModelInstance> modelOutput,
		String modid) {
		super(blockStateOutput, itemModelOutput, modelOutput);
		this.modid = modid;
		this.input = input;
	}

	@Override
	public void run() {
		horizontalAxisBlock(CPBlocks.STEW_POT.get(), bmf("stew_pot"));
		horizontalAxisBlock(CPBlocks.STEW_POT_LEAD.get(), bmf("lead_stew_pot"));
		this.blockStateOutput.accept(horizontalMultipart(this.getMultipartBuilder(CPBlocks.T_BENCH.get()), bmf("tessellation_workbench")));
		blockItemModel("tessellation_workbench");
		CPBlocks.stoves.forEach(e -> stove(e.get()));
		blockItemModel(CPBlocks.STEW_POT.getId().getPath());
		blockItemModel(CPBlocks.STEW_POT_LEAD.getId().getPath());
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
				.select(7, super.createRotatedVariants(bmfs("snail_stage_5")))));
		this.blockStateOutput.accept(
			this.getVariantBuilder(CPBlocks.LOAF_DOUGH.get())
				.with(PropertyDispatch.initial(SlabBlock.TYPE)
					.select(SlabType.TOP, bmf("loaf_dough_top"))
					.select(SlabType.BOTTOM, bmf("loaf_dough_bottom"))
					.select(SlabType.DOUBLE, bmf("loaf_dough_top_bottom"))));
		//blockItemModel(CPBlocks.LOAF_DOUGH.get(), CPMain.rl("loaf_dough"));
		this.blockStateOutput.accept(
			this.getVariantBuilder(CPBlocks.LOAF.get())
				.with(PropertyDispatch.initial(SlabBlock.TYPE)
					.select(SlabType.TOP, bmf("loaf_top"))
					.select(SlabType.BOTTOM, bmf("loaf_bottom"))
					.select(SlabType.DOUBLE, bmf("loaf_top_bottom"))));
		//blockItemModel(CPBlocks.LOAF.get(), CPMain.rl("loaf"));
		TextureSlot[] slots = new TextureSlot[] { TextureSlot.create("0"), TextureSlot.create("1") };
		ModelTemplate[] models = new ModelTemplate[] {
			new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/template_mosaic_tile_0")), Optional.empty(), slots[0], TextureSlot.PARTICLE),
			new ModelTemplate(Optional.of(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/template_mosaic_tile_1")), Optional.empty(), slots[1], TextureSlot.PARTICLE)
		};
		MultiPartGenerator mosaic = this.getMultipartBuilder(CPBlocks.MOSAIC.get());
		for (MosaicMaterial m : MosaicMaterial.values())
			for (MosaicPattern p : MosaicPattern.values())
				for (int i : new int[] { 0, 1 }) {
					Material mat = new Material(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/mosaic/components/mosaic_" + p + "_" + m.shortName + "_" + i));
					TextureMapping tm = new TextureMapping().put(slots[i], mat)
						.put(TextureSlot.PARTICLE, mat);
					;
					mosaic = this.horizontalMultipart(mosaic,
						bmf(models[i].create(Identifier.fromNamespaceAndPath(CPMain.MODID, "block/mosaic/mosaic_" + p + "_" + m.shortName + "_" + i), tm, modelOutput)),
						b -> b.term(MosaicBlock.MATERIAL[i], m).term(MosaicBlock.PATTERN, p));
				}
		this.blockStateOutput.accept(mosaic);

		for (String s : CPItems.dishes) {
			this.blockStateOutput.accept(this.getMultipartBuilder(cpblock(s))
				.with(bmf("dish"))
				.with(bmf("plate_dishes/" + s)));
			this.blockStateOutput.accept(this.getMultipartBuilder(cpblock(s + "_loaf"))
				.with(bmf("bread_bowl"))
				.with(bmf("bread_bowl_dishes/" + s)));
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
				simpleBlockItem(cpblock(stone + "_lacunar_tile"), CPMain.rl(stone + "_lacunar_tile"));
				blockItemModel(stone + "_spoked_fence", "_inventory");
				this.blockStateOutput.accept(this.getMultipartBuilder(cpblock(stone + "_spoked_fence"))
					.with(bmf(stone + "_spoked_fence_post"))
					.with(condition(SpokedFenceBlock.WEST_WALL, true), bmf(stone + "_spoked_fence_side").with(Y_ROT_270))
					.with(condition(SpokedFenceBlock.NORTH_WALL, true), bmf(stone + "_spoked_fence_side"))
					.with(condition(SpokedFenceBlock.EAST_WALL, true), bmf(stone + "_spoked_fence_side").with(Y_ROT_90))
					.with(condition(SpokedFenceBlock.SOUTH_WALL, true), bmf(stone + "_spoked_fence_side").with(Y_ROT_180)));

			}
			if (rtype.isHypocaustMaterial()) {
				blockItemModel(stone + "_hypocaust_firebox");
				blockItemModel(stone + "_caliduct");
			}
			if (rtype.isRoadMaterial()) {
				roadBlock(stone);

			}
		}
		MultiPartGenerator boat = horizontalMultipart(this.getMultipartBuilder(CPBlocks.GRAVY_BOAT.get()),
			bmf("gravy_boat"));
		int i = 0;
		for (String s : ImmutableSet.of("_oil_0", "_oil_1", "_oil_2", "_oil_3", "_oil_4")) {
			int j = i++;
			boat = horizontalMultipart(boat, bmf("gravy_boat" + s), c -> c.term(GravyBoatBlock.LEVEL, j));
		}
		this.blockStateOutput.accept(boat);
		for (String wood : CPBlocks.woods) {
			for (String type : ImmutableSet.of(

				"_fence_gate", "_leaves", "_log", "_planks", "_pressure_plate", "_slab", "_stairs", "_wood"))
				blockItemModel(wood + type);
			blockItemModel(wood + "_fence", "_inventory");
			blockItemModel(wood + "_button", "_inventory");
			this.itemModelOutput.accept(cpblock(wood + "_fruits").asItem(),
				new CuboidItemModelWrapper.Unbaked(CPMain.rl("block/"+wood + "_fruits_stage_3"), Optional.of(
					new Transformation(new Matrix4f().scale(1f).rotationY(Mth.DEG_TO_RAD * 0.1f).translation(0, 0, 0))), List.of()));

			blockItemModel("stripped_" + wood + "_log");
			blockItemModel("stripped_" + wood + "_wood");
			this.createHangingSign(cpblock(wood + "_planks"), cpblock(wood + "_hanging_sign"), cpblock(wood + "_wall_hanging_sign"));

			// blockItemModel(wood+"_trapdoor","_top")

		}
		this.itemModelOutput.accept(CPBlocks.MOSAIC.get().asItem(),MosaicModel.Unbaked.INSTANCE);

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
		blockItemModel(CPBlocks.SILPHIUM.get(),CPMain.rl("silphium"));
		blockItemModel("walnut_trapdoor","_bottom");
		this.blockStateOutput.accept(createSimpleBlock(CPBlocks.SNAIL_MUCUS.get(), bmf("snail_mucus")));
		this.blockStateOutput.accept(createSimpleBlock(CPBlocks.LEAD_BLOCK.get(), bmf("lead_block")));
		this.blockStateOutput.accept(createSimpleBlock(CPBlocks.LOAF_BOWL.getSecond().get(), bmf("bread_bowl")));
		// itemModels().getBuilder("snail_block").parent(bmf("snail_stage_5")).transforms().transform(ItemDisplayContext.GUI).scale(1.5f).rotation(0,
		// 45, 180).translation(0, 4, 0).end().end();

		for (String bush : ImmutableSet.of("wolfberry", "fig")) {
			blockItemModel(bush + "_log");
			this.itemModelOutput.accept(cpblock(bush + "_fruits").asItem(),
				new CuboidItemModelWrapper.Unbaked(CPMain.rl("block/"+bush + "_fruits_stage_3"), Optional.of(
					new Transformation(new Matrix4f().scale(1f).rotationY(Mth.DEG_TO_RAD * 45).translation(0, 1, 0))), List.of()));

			blockItemModel(bush + "_leaves");
		}

	}

	protected Empty getVariantBuilder(Block blk) {
		return MultiVariantGenerator.dispatch(blk);
	}

	public void roadBlock(String name) {
		

		
		this.blockStateOutput.accept
		(getVariantBuilder(cpblock(name + "_road_side")).with(PropertyDispatch.initial(StairBlock.FACING, StairBlock.SHAPE)
			.generate((facing, shape) -> {
				int yRot = (int) facing.getClockWise().toYRot(); // Stairs model is rotated 90 degrees
				// clockwise for some reason
				if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT) {
					yRot += 270; // Left facing stairs are rotated 90 degrees clockwise
				}
				while (yRot < 0)
					yRot += 360;
				yRot %= 360;
				String ext = shape == StairsShape.STRAIGHT ? "_side"
					: shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? "_outer_corner"
						: "_inner_corner";
				int i = 0;
				List<Variant> variants = new ArrayList<>();
				while (true) {
					Identifier rl = Identifier.fromNamespaceAndPath(this.modid, "block/roads/" + name + "_road" + ext + "_" + i);
					if (!existsModel(rl))
						break;
					variants.add(bmfs(rl));
					i++;
				}
				VariantMutator vm = null;

				switch (yRot) {
				case 90:
					vm = Y_ROT_90;
					break;
				case 180:
					vm = Y_ROT_180;
					break;
				case 270:
					vm = Y_ROT_270;
					break;
				}
				if (vm == null)
					return variants(variants.toArray(Variant[]::new));
				else
					return variants(variants.toArray(Variant[]::new)).with(vm);
			})));
		List<Variant> list = new ArrayList<>();
		int i = 0;
		while (true) {
			Identifier rl = Identifier.fromNamespaceAndPath(this.modid, "block/roads/" + name + "_road_" + i);
			if (!existsModel(rl))
				break;
			i++;
			list.add(bmfs(rl));
		}
		blockItemModel(cpblock(name + "_road"), CPMain.rl("roads/" + name + "_road"));
		
		this.blockStateOutput.accept(createSimpleBlock(cpblock(name + "_road"), variants(list.toArray(Variant[]::new))));
		blockItemModel(cpblock(name + "_road_side"),  CPMain.rl("roads/" + name + "_road_side"));
	}

	private Block cpblock(String name) {
		return BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath(this.modid, name));
	}

	protected void blockItemModel(String n) {
		blockItemModel(n, "");
	}
	public void simpleTexture(String name, String par) {
		this.itemModelOutput.accept(BuiltInRegistries.ITEM.getValue(CPMain.rl(name)),
		ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(CPMain.rl("item/" + name),new TextureMapping().put(TextureSlot.LAYER0, new Material(CPMain.rl("item/" + par + name),false)), this.modelOutput))
		);

	}
	public void simpleTexture(Item item) {
		this.itemModelOutput.accept(item,
		ItemModelUtils.plainModel(this.createFlatItemModel(item))
		);
	}
	public void texture(String name) {
		texture(name, name);
	}
	public void texture(Item name, String par) {
		this.itemModelOutput.accept(name,
			ItemModelUtils.plainModel(ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(name), TextureMapping.layer0(new Material(Identifier.fromNamespaceAndPath(CPMain.MODID, "item/"+par))), this.modelOutput)
				));
	}
	public void texture(String name, String par) {
		texture(BuiltInRegistries.ITEM.getValue(CPMain.rl(name)),par);
	}

	protected void blockItemModel(String n, String p) {
		if (input.getResource(Identifier.fromNamespaceAndPath(CPMain.MODID, "textures/item/" + n + p + ".png")).isPresent()) {

			texture(n, n + p);
		} else {
			blockItemModel(cpblock(n), CPMain.rl(n + p));
		}
	}

	protected void blockItemModel(Block n, Identifier p) {
		Identifier blockModelId=p.withPrefix("block/");
		String name=p.getPath();
		if(existsModel(blockModelId)) {

			this.itemModelOutput.accept(n.asItem(), ItemModelUtils.plainModel(blockModelId));
		}else {
			List<String> rn = Arrays.asList(name.split("_"));
			for (int i = rn.size(); i >= 0; i--) {
				List<String> rrn = new ArrayList<>(rn);
				rrn.add(i, "0");
				blockModelId = Identifier.fromNamespaceAndPath(this.modid, "block/" + String.join("_", rrn));
				if (existsModel(blockModelId)) {
					this.itemModelOutput.accept(n.asItem(), ItemModelUtils.plainModel(blockModelId));
					return;
				}
			}
			

			throw new IllegalArgumentException("model does not exists: "+p);
		}
	}

	public void stove(Block block) {
		this.blockStateOutput.accept(
		
			horizontalMultipart(this.getMultipartBuilder(block),
				bmf(Utils.getRegistryName(block).getPath())));
		blockItemModel(block, Utils.getRegistryName(block));

	}

	public boolean existsModel(Identifier id) {
		return input.getResource(id.withPrefix("models/").withSuffix(".json")).isPresent();

	}

	public MultiVariant bmf(String name) {
		return super.variant(bmfs(name));
	}
	
	public Variant bmfs(String name) {
		Identifier orl = Identifier.fromNamespaceAndPath(this.modid, "block/" + name);
		Identifier rl = orl;

		if (!existsModel(rl)) {// not exists, let's guess
			List<String> rn = Arrays.asList(name.split("_"));
			for (int i = rn.size(); i >= 0; i--) {
				List<String> rrn = new ArrayList<>(rn);
				rrn.add(i, "0");
				rl = Identifier.fromNamespaceAndPath(this.modid, "block/" + String.join("_", rrn));
				if (existsModel(rl))
					return super.plainModel(rl);
			}

		}
		CPMain.logger.warn("Model file " + orl + " not exists, using unchecked");
		return super.plainModel(rl);
	}

	public MultiVariant bmf(Identifier name) {
		return super.variant(bmfs(name));
	}

	public Variant bmfs(Identifier orl) {
		return super.plainModel(orl);
	}

	protected void simpleBlockItem(Block b, Identifier model) {
		this.blockStateOutput.accept(createSimpleBlock(b, bmf(model.withPrefix("block/"))));
		blockItemModel(b, model);
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

}
