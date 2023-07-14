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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.decoration.BaseColumnBlock;
import com.teammoeg.caupona.blocks.decoration.CPButtonBlock;
import com.teammoeg.caupona.blocks.decoration.CPDoorBlock;
import com.teammoeg.caupona.blocks.decoration.CPPressurePlateBlock;
import com.teammoeg.caupona.blocks.decoration.CPRoadBlock;
import com.teammoeg.caupona.blocks.decoration.CPRoadSideBlock;
import com.teammoeg.caupona.blocks.decoration.CPStandingSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPTrapDoorBlock;
import com.teammoeg.caupona.blocks.decoration.CPWallSignBlock;
import com.teammoeg.caupona.blocks.decoration.ColumnCapitalBlock;
import com.teammoeg.caupona.blocks.decoration.LacunarBlock;
import com.teammoeg.caupona.blocks.decoration.SpokedFenceBlock;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlock;
import com.teammoeg.caupona.blocks.foods.BowlBlock;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleBoulderBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleVentBlock;
import com.teammoeg.caupona.blocks.fumarole.PumiceBloomBlock;
import com.teammoeg.caupona.blocks.hypocaust.CaliductBlock;
import com.teammoeg.caupona.blocks.hypocaust.FireboxBlock;
import com.teammoeg.caupona.blocks.hypocaust.WolfStatueBlock;
import com.teammoeg.caupona.blocks.pan.GravyBoatBlock;
import com.teammoeg.caupona.blocks.pan.PanBlock;
import com.teammoeg.caupona.blocks.plants.BushLogBlock;
import com.teammoeg.caupona.blocks.plants.CPStripPillerBlock;
import com.teammoeg.caupona.blocks.plants.FruitBlock;
import com.teammoeg.caupona.blocks.plants.FruitsLeavesBlock;
import com.teammoeg.caupona.blocks.plants.SilphiumBlock;
import com.teammoeg.caupona.blocks.plants.SnailBaitBlock;
import com.teammoeg.caupona.blocks.plants.SnailBlock;
import com.teammoeg.caupona.blocks.plants.WalnutFruitBlock;
import com.teammoeg.caupona.blocks.pot.StewPot;
import com.teammoeg.caupona.blocks.stove.ChimneyPotBlock;
import com.teammoeg.caupona.blocks.stove.KitchenStove;
import com.teammoeg.caupona.blocks.stove.KitchenStoveBlockEntity;
import com.teammoeg.caupona.item.CPBlockItem;
import com.teammoeg.caupona.item.CPSignItem;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.util.MaterialType;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.worldgen.DefaultTreeGrower;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CPMain.MODID);
	// static string data
	public static final String[] woods = new String[] { "walnut" };
	// Dynamic block types
	public static final MaterialType[] all_materials = new MaterialType[] {
			new MaterialType("mud").makeCounter(1),
			new MaterialType("stone_brick").makeCounter(2).makeHypocaust(),
			new MaterialType("stone").setBase(()->Blocks.STONE.defaultBlockState()).makePillar().makeRoad(),
			new MaterialType("sandstone").setBase(()->Blocks.SANDSTONE.defaultBlockState()).makeRoad(),
			new MaterialType("brick").makeCounter(2).makeHypocaust(),
			new MaterialType("mixed_bricks").makeDecoration(),
			new MaterialType("opus_incertum").makeCounter(2).makeDecoration().makeHypocaust(),
			new MaterialType("opus_latericium").makeCounter(2).makeDecoration().makeHypocaust(),
			new MaterialType("opus_reticulatum").makeDecoration(),
			new MaterialType("felsic_tuff_bricks").makeDecoration(),
			new MaterialType("felsic_tuff").makeDecoration().makePillar().makeRoad(),
			new MaterialType("quartz").makePillar(),
			new MaterialType("calcite").makePillar() };
	// Block Lists for use in other registries
	public static final List<RegistryObject<KitchenStove>> stoves = new ArrayList<>();
	public static final List<Block> signs = new ArrayList<>();
	public static final Map<String, RegistryObject<Block>> stoneBlocks = new HashMap<>();
	public static final List<Block> chimney = new ArrayList<>();
	public static final List<Block> dolium = new ArrayList<>();
	public static final List<Block> dishes = new ArrayList<>();
	public static final List<Block> caliduct = new ArrayList<>();
	public static final List<Block> firebox = new ArrayList<>();
	public static final List<RegistryObject<Block>> leaves = new ArrayList<>();

	// Other useful blocks
	public static final RegistryObject<FumaroleBoulderBlock> FUMAROLE_BOULDER = decoblock("fumarole_boulder",
			() -> new FumaroleBoulderBlock(getStoneProps().isViewBlocking(CPBlocks::isntSolid).noOcclusion()
					.isSuffocating(CPBlocks::isntSolid)));
	public static final RegistryObject<FumaroleVentBlock> FUMAROLE_VENT = maindecoblock("fumarole_vent",
			() -> new FumaroleVentBlock(getStoneProps().strength(4.5f, 10).isViewBlocking(CPBlocks::isntSolid)
					.noOcclusion().isSuffocating(CPBlocks::isntSolid)));
	public static final RegistryObject<Block> PUMICE = block("pumice", getStoneProps(),TabType.DECORATION);
	public static final RegistryObject<PumiceBloomBlock> PUMICE_BLOOM = maindecoblock("pumice_bloom",
			() -> new PumiceBloomBlock(getStoneProps().noOcclusion()));
	public static final RegistryObject<GravyBoatBlock> GRAVY_BOAT = BLOCKS.register("gravy_boat",
			() -> new GravyBoatBlock(Block.Properties.of().sound(SoundType.GLASS).instabreak().noOcclusion()
					.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid)));
	public static final BlockSetType WALNUT_TYPE = new BlockSetType("walnut");
	public static final WoodType WALNUT = WoodType.register(new WoodType("caupona:walnut", WALNUT_TYPE));
	public static final RegistryObject<WolfStatueBlock> WOLF = maindecoblock("wolf_statue",
			() -> new WolfStatueBlock(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER)
					.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion()));
	public static final RegistryObject<PanBlock> STONE_PAN = mainblock("stone_griddle", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(3.5f, 10).noOcclusion()));
	public static final RegistryObject<PanBlock> COPPER_PAN = mainblock("copper_frying_pan", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(3.5f, 10).noOcclusion()));
	public static final RegistryObject<PanBlock> IRON_PAN = mainblock("iron_frying_pan", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).noOcclusion()));
	public static final RegistryObject<PanBlock> LEAD_PAN = mainblock("lead_frying_pan", () -> new PanBlock(
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).noOcclusion()));
	public static final RegistryObject<DishBlock> DISH = BLOCKS.register("dish",
			() -> new DishBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid)));
	public static final RegistryObject<StewPot> STEW_POT = mainblock("stew_pot",
			() -> new StewPot(
					Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.STONE)
							.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion(),
					CPBlockEntityTypes.STEW_POT));
	public static final RegistryObject<StewPot> STEW_POT_LEAD = mainblock("lead_stew_pot",
			() -> new StewPot(
					Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.STONE)
							.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion(),
					CPBlockEntityTypes.STEW_POT));

	public static final RegistryObject<BowlBlock> BOWL = BLOCKS.register("bowl",
			() -> new BowlBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid), CPBlockEntityTypes.BOWL));
	public static final RegistryObject<SilphiumBlock> SILPHIUM = mainblock("silphium_block",
			() -> new SilphiumBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollission()
					.instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava()
					.pushReaction(PushReaction.DESTROY)));
	public static final RegistryObject<Block> WALNUT_FRUIT = mainblock("walnut_fruits", () -> new WalnutFruitBlock(BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).noCollission().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).ignitedByLava()));
	public static final RegistryObject<Block> SNAIL_MUCUS=block("snail_mucus",BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).randomTicks().instabreak().sound(SoundType.CROP).noOcclusion().isViewBlocking(CPBlocks::isntSolid),TabType.MAIN_AND_DECORATION);
	
	public static final RegistryObject<SnailBlock> SNAIL = baseblock("snail_block", ()->new SnailBlock(BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).noCollission().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).isViewBlocking(CPBlocks::isntSolid)),x->new CPBlockItem(x,CPItems.createProps(),TabType.MAIN));
	public static final RegistryObject<SnailBaitBlock> SNAIL_BAIT = baseblock("snail_bait", ()->new SnailBaitBlock(BlockBehaviour.Properties.of()
			.mapColor(MapColor.PLANT).noCollission().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).isViewBlocking(CPBlocks::isntSolid)),x->new CPBlockItem(x,CPItems.createProps(),TabType.MAIN));
	public static final RegistryObject<Block> LEAD_BLOCK=block("lead_block",BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).requiresCorrectToolForDrops(),TabType.DECORATION);
	// Bulk register blocks
	static {

		for (MaterialType type : all_materials) {
			String name = type.getName();
			if (type.isDecorationMaterial()) {
				RegistryObject<Block> base = block(name, getStoneProps(),TabType.DECORATION);
				stoneBlocks.put(name, base);
				type.setBase(()->base.get().defaultBlockState());
				decoblock(name + "_slab", () -> new SlabBlock(getStoneProps()));
				decoblock(name + "_stairs", () -> new StairBlock(type.getBase(), getStoneProps()));
				decoblock(name + "_wall", () -> new WallBlock(getStoneProps()));
			}
			if (type.isCounterMaterial()) {
				stove(name + "_kitchen_stove", getStoveProps(),
						type.getCounterGrade() == 1 ? CPBlockEntityTypes.STOVE_T1 : CPBlockEntityTypes.STOVE_T2);
				block(name + "_chimney_flue", getTransparentProps(),TabType.MAIN_AND_DECORATION);
				maindecoblock(name + "_chimney_pot", () -> new ChimneyPotBlock(getTransparentProps()));
				decoblock(name + "_counter", () -> new CPHorizontalBlock(getStoneProps()));
				maindecoblock(name + "_counter_with_dolium", () -> new CounterDoliumBlock(getTransparentProps()));
			}
			if (type.isHypocaustMaterial()) {
				mainblock(name + "_caliduct", () -> new CaliductBlock(getTransparentProps()));
				mainblock(name + "_hypocaust_firebox", () -> new FireboxBlock(getTransparentProps()));
			}
			if (type.isPillarMaterial()) {
				decoblock(name + "_column_fluted_plinth",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_column_fluted_shaft",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), false));
				decoblock(name + "_column_shaft",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), false));
				decoblock(name + "_column_plinth",
						() -> new BaseColumnBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_ionic_column_capital",
						() -> new ColumnCapitalBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_tuscan_column_capital",
						() -> new ColumnCapitalBlock(getTransparentProps().strength(2f, 6f), false));
				decoblock(name + "_acanthine_column_capital",
						() -> new ColumnCapitalBlock(getTransparentProps().strength(2f, 6f), true));
				decoblock(name + "_lacunar_tile",()->new LacunarBlock(getTransparentProps().strength(2f, 6f)
						.isViewBlocking(CPBlocks::isntSolid)));
				decoblock(name+"_spoked_fence",()->new SpokedFenceBlock(getTransparentProps().strength(2f, 6f)));
			}
			if(type.isRoadMaterial()) {
				decoblock(name+"_road_side",()->new CPRoadSideBlock(getTransparentProps().isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).strength(2f, 6f)));
				decoblock(name+"_road",()->new CPRoadBlock(getTransparentProps().isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).strength(2f, 6f)));
			}
		}

		registerWood("walnut", WALNUT, DefaultTreeGrower.supply(CPWorldGen.WALNUT),WALNUT_FRUIT);
		registerBush("fig", DefaultTreeGrower.supply(CPWorldGen.FIG));
		registerBush("wolfberry", DefaultTreeGrower.supply(CPWorldGen.WOLFBERRY));
		for (String s : CPItems.dishes) {
			baseblock(s,
				() -> new DishBlock(Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
							.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
							.isViewBlocking(CPBlocks::isntSolid)),
					b -> new DishItem(b, CPItems.createSoupProps()));

		}
	}
	// Convenient block registry wrapper

	// create a bush
	private static void registerBush(String wood, Supplier<AbstractTreeGrower> growth) {
		decoblock(wood + "_log", () -> new BushLogBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
				.strength(2.0F).noOcclusion().sound(SoundType.WOOD)));
		RegistryObject<Block> a = decoblock(wood + "_fruits", () -> new FruitBlock(BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT).offsetType(OffsetType.XZ).noCollission().randomTicks().instabreak().sound(SoundType.CROP)));
		leaves.add(
				CPCommonBootStrap.asCompositable(decoblock(wood + "_leaves", () -> leaves(SoundType.GRASS, a)), 0.3F));
		CPCommonBootStrap.asCompositable(
				maindecoblock(wood + "_sapling", () -> new SaplingBlock(growth.get(), BlockBehaviour.Properties.of()
						.mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS))),
				0.3F);

	}

	// create a wood
	private static void registerWood(String wood, WoodType wt, Supplier<AbstractTreeGrower> growth,RegistryObject<Block> f) {
		RegistryObject<Block> planks = block(wood + "_planks",
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava(),TabType.DECORATION);
		decoblock(wood + "_button",
				() -> new CPButtonBlock(
						BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD).ignitedByLava(), WALNUT_TYPE,
						30, true));
		decoblock(wood + "_door", () -> new CPDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
				.strength(3.0F).sound(SoundType.WOOD).noOcclusion().ignitedByLava(), WALNUT_TYPE));
		decoblock(wood + "_fence", () -> new FenceBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()));
		decoblock(wood + "_fence_gate", () -> new FenceGateBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava(),
				WALNUT));
		
		leaves.add(CPCommonBootStrap.asCompositable(decoblock(wood + "_leaves", () -> leaves(SoundType.GRASS, f)), 0.3F));
		RegistryObject<Block> sl = decoblock("stripped_" + wood + "_log", () -> log(null));
		decoblock(wood + "_log", () -> log(sl));

		decoblock(wood + "_pressure_plate",
				() -> new CPPressurePlateBlock(
						PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.of()
								.mapColor(MapColor.WOOD).noCollission().strength(0.5F).sound(SoundType.WOOD).ignitedByLava(),
						WALNUT_TYPE));
		CPCommonBootStrap.asCompositable(
				maindecoblock(wood + "_sapling", () -> new SaplingBlock(growth.get(), BlockBehaviour.Properties.of()
						.mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).ignitedByLava())),
				0.3F);
		RegistryObject<Block> s = BLOCKS.register(wood + "_sign",
				() -> new CPStandingSignBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollission()
						.strength(1.0F).sound(SoundType.WOOD).ignitedByLava(), wt));
		RegistryObject<Block> ws = BLOCKS.register(wood + "_wall_sign",
				() -> new CPWallSignBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollission()
						.strength(1.0F).sound(SoundType.WOOD).ignitedByLava(), wt));
		CPItems.ITEMS.register(wood + "_sign",
				() -> new CPSignItem((new Item.Properties()).stacksTo(16), s.get(), ws.get(), TabType.DECORATION));
		decoblock(wood + "_slab", () -> new SlabBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava()));
		decoblock(wood + "_stairs", () -> new StairBlock(planks.get()::defaultBlockState,
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
		decoblock(wood + "_trapdoor", () -> new CPTrapDoorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
				.strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(CPBlocks::never).ignitedByLava(), WALNUT_TYPE));
		RegistryObject<Block> sw = decoblock("stripped_" + wood + "_wood", () -> new RotatedPillarBlock(
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava()));
		decoblock(wood + "_wood", () -> new CPStripPillerBlock(sw,
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava()));
	}

	// create a stove
	static RegistryObject<KitchenStove> stove(String name, Properties props,
			RegistryObject<BlockEntityType<KitchenStoveBlockEntity>> tile) {
		RegistryObject<KitchenStove> bl = BLOCKS.register(name, () -> new KitchenStove(props, tile));
		stoves.add(bl);

		CPItems.ITEMS.register(name, () -> new CPBlockItem(bl.get(), CPItems.createProps(), TabType.MAIN_AND_DECORATION));
		return bl;
	}

	// register any block to caupona registry
	static <T extends Block> RegistryObject<T> mainblock(String name, Supplier<T> bl) {
		RegistryObject<T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), TabType.MAIN));
		return blx;
	}
	static <T extends Block> RegistryObject<T> decoblock(String name, Supplier<T> bl) {
		RegistryObject<T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), TabType.DECORATION));
		return blx;
	}
	static <T extends Block> RegistryObject<T> maindecoblock(String name, Supplier<T> bl) {
		RegistryObject<T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), TabType.MAIN_AND_DECORATION));
		return blx;
	}

	// register any block to caupona registry with custom item factory
	static <T extends Block> RegistryObject<T> baseblock(String name, Supplier<T> bl, Function<T, Item> toitem) {
		RegistryObject<T> blx = BLOCKS.register(name, bl);
		CPItems.ITEMS.register(name, () -> toitem.apply(blx.get()));
		return blx;
	}

	// register basic block to caupona registry
	static RegistryObject<Block> block(String name, Properties props,TabType tab) {
		RegistryObject<Block> blx = BLOCKS.register(name, () -> new Block(props));
		CPItems.ITEMS.register(name, () -> new CPBlockItem(blx.get(), CPItems.createProps(), tab));
		return blx;
	}

	// Make leaves block
	private static LeavesBlock leaves(SoundType p_152615_, RegistryObject<Block> fruit) {
		return new FruitsLeavesBlock(BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F)
				.randomTicks().sound(p_152615_).noOcclusion().isValidSpawn(CPBlocks::ocelotOrParrot)
				.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid).ignitedByLava(), fruit);
	}

	// Make log block
	private static RotatedPillarBlock log(RegistryObject<Block> st) {
		if (st == null)
			return new RotatedPillarBlock(
					BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
		return new CPStripPillerBlock(st,
				BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava());
	}

	// Property functions
	private static Properties getStoneProps() {
		return Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
				.strength(2.0f, 6);
	}

	private static Properties getStoveProps() {
		return Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
				.strength(3.5f, 10).noOcclusion().lightLevel(s -> s.getValue(KitchenStove.LIT) ? 9 : 0)
				.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid);
	}

	private static Properties getTransparentProps() {
		return Block.Properties.of().sound(SoundType.STONE).requiresCorrectToolForDrops().strength(3.5f, 10)
				.noOcclusion();
	}

	private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
		return false;
	}

	private static Boolean never(BlockState p_50779_, BlockGetter p_50780_, BlockPos p_50781_, EntityType<?> p_50782_) {
		return (boolean) false;
	}

	private static Boolean ocelotOrParrot(BlockState p_50822_, BlockGetter p_50823_, BlockPos p_50824_,
			EntityType<?> p_50825_) {
		return p_50825_ == EntityType.OCELOT || p_50825_ == EntityType.PARROT;
	}
}