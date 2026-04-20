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

package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.blocks.CPFlamableBlock;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.CPSaplingBlock;
import com.teammoeg.caupona.blocks.decoration.BaseColumnBlock;
import com.teammoeg.caupona.blocks.decoration.CPButtonBlock;
import com.teammoeg.caupona.blocks.decoration.CPCeilingHangingSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPDoorBlock;
import com.teammoeg.caupona.blocks.decoration.CPPressurePlateBlock;
import com.teammoeg.caupona.blocks.decoration.CPRoadBlock;
import com.teammoeg.caupona.blocks.decoration.CPRoadSideBlock;
import com.teammoeg.caupona.blocks.decoration.CPStandingSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPTrapDoorBlock;
import com.teammoeg.caupona.blocks.decoration.CPWallHangingSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPWallSignBlock;
import com.teammoeg.caupona.blocks.decoration.CPWoodFenceBlock;
import com.teammoeg.caupona.blocks.decoration.CPWoodFenceGateBlock;
import com.teammoeg.caupona.blocks.decoration.CPWoodRotatedPillarBlock;
import com.teammoeg.caupona.blocks.decoration.CPWoodSlabBlock;
import com.teammoeg.caupona.blocks.decoration.CPWoodStairBlock;
import com.teammoeg.caupona.blocks.decoration.ChimneyFluteBlock;
import com.teammoeg.caupona.blocks.decoration.ColumnCapitalBlock;
import com.teammoeg.caupona.blocks.decoration.KitchenRailBlock;
import com.teammoeg.caupona.blocks.decoration.LacunarBlock;
import com.teammoeg.caupona.blocks.decoration.SpokedFenceBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicBlock;
import com.teammoeg.caupona.blocks.decoration.mosaic.MosaicItem;
import com.teammoeg.caupona.blocks.decoration.mosaic.TessellationWorkBenchBlock;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlock;
import com.teammoeg.caupona.blocks.foods.BowlBlock;
import com.teammoeg.caupona.blocks.foods.DishBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleBoulderBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleVentBlock;
import com.teammoeg.caupona.blocks.fumarole.PumiceBloomBlock;
import com.teammoeg.caupona.blocks.hypocaust.CaliductBlock;
import com.teammoeg.caupona.blocks.hypocaust.FireboxBlock;
import com.teammoeg.caupona.blocks.hypocaust.WolfStatueBlock;
import com.teammoeg.caupona.blocks.loaf.LoafBlock;
import com.teammoeg.caupona.blocks.loaf.LoafDoughBlock;
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
import com.teammoeg.caupona.item.CPHangingSignItem;
import com.teammoeg.caupona.item.CPSignItem;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.item.LoafDishItem;
import com.teammoeg.caupona.util.MaterialType;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CPBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CPMain.MODID);
	// static string data
	public static final String[] woods = new String[] { "walnut" };
	// Dynamic block types
	public static final MaterialType[] all_materials = new MaterialType[] {
			new MaterialType("loaf_heap").makeDecoration(),
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
			new MaterialType("polished_felsic_tuff").makeDecoration(),
			new MaterialType("quartz").makePillar(),
			new MaterialType("calcite").makePillar() };
	// Block Lists for use in other registries
	public static final List<DeferredHolder<Block,KitchenStove>> stoves = new ArrayList<>();
	public static final List<Block> signs = new ArrayList<>();
	public static final List<Block> hanging_signs = new ArrayList<>();
	public static final Map<String, DeferredHolder<Block,Block>> stoneBlocks = new HashMap<>();
	public static final List<Block> chimney = new ArrayList<>();
	public static final List<Block> dolium = new ArrayList<>();
	public static final List<Block> dishes = new ArrayList<>();
	public static final List<Block> caliduct = new ArrayList<>();
	public static final List<Block> firebox = new ArrayList<>();
	public static final List<DeferredHolder<Block,Block>> leaves = new ArrayList<>();
	// Other useful blocks
	public static final DeferredHolder<Block,FumaroleBoulderBlock> FUMAROLE_BOULDER = decoblock("fumarole_boulder",
			getStoneProps().isViewBlocking(CPBlocks::isntSolid).noOcclusion()
		.isSuffocating(CPBlocks::isntSolid),FumaroleBoulderBlock::new);
	public static final DeferredHolder<Block,FumaroleVentBlock> FUMAROLE_VENT = maindecoblock("fumarole_vent",
			(getStoneProps().strength(4.5f, 10).isViewBlocking(CPBlocks::isntSolid)
					.noOcclusion().isSuffocating(CPBlocks::isntSolid)),FumaroleVentBlock::new);
	public static final DeferredHolder<Block,Block> PUMICE = block("pumice", getStoneProps(),TabType.DECORATION);
	public static final DeferredHolder<Block,PumiceBloomBlock> PUMICE_BLOOM = maindecoblock("pumice_bloom",
			(getStoneProps().noOcclusion()), PumiceBloomBlock::new);
	public static final DeferredHolder<Block,FumaroleBoulderBlock> LITHARGE_FUMAROLE_BOULDER = decoblock("litharge_fumarole_boulder",
		getStoneProps().isViewBlocking(CPBlocks::isntSolid).noOcclusion()
				.isSuffocating(CPBlocks::isntSolid), FumaroleBoulderBlock::new);
	public static final DeferredHolder<Block,FumaroleVentBlock> LITHARGE_FUMAROLE_VENT = maindecoblock("litharge_fumarole_vent",
		(getStoneProps().strength(4.5f, 10).isViewBlocking(CPBlocks::isntSolid)
				.noOcclusion().isSuffocating(CPBlocks::isntSolid)), FumaroleVentBlock::new);
	public static final DeferredHolder<Block,PumiceBloomBlock> LITHARGE_BLOOM = maindecoblock("litharge_bloom",
		(getStoneProps().noOcclusion()), PumiceBloomBlock::new);
	
	public static final DeferredHolder<Block,GravyBoatBlock> GRAVY_BOAT = BLOCKS.registerBlock("gravy_boat", GravyBoatBlock::new,
		()->Block.Properties.of().sound(SoundType.GLASS).instabreak().noOcclusion()
		.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid));
	public static final BlockSetType WALNUT_TYPE = new BlockSetType("walnut");
	public static final WoodType WALNUT = WoodType.register(new WoodType("caupona:walnut", WALNUT_TYPE));
	public static final DeferredHolder<Block,WolfStatueBlock> WOLF = maindecoblock("wolf_statue",
			(Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER)
					.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion()), WolfStatueBlock::new);
	public static final DeferredHolder<Block,KitchenRailBlock> KITCHEN_RAIL = maindecoblock("kitchen_rail",
		(Block.Properties.of().sound(SoundType.WOOD).strength(2f, 3f).noOcclusion()), KitchenRailBlock::new);
	public static final DeferredHolder<Block,PanBlock> STONE_PAN = mainblock("stone_griddle",
			Block.Properties.of().mapColor(MapColor.STONE).sound(SoundType.STONE).strength(3.5f, 10).noOcclusion(), PanBlock::new);
	public static final DeferredHolder<Block,TessellationWorkBenchBlock> T_BENCH= mainblock("tessellation_workbench",
			Block.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.STONE).strength(3.5f, 10).noOcclusion().requiresCorrectToolForDrops(), TessellationWorkBenchBlock::new);
	public static final DeferredHolder<Block,PanBlock> COPPER_PAN = mainblock("copper_frying_pan",
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.COPPER).strength(3.5f, 10).noOcclusion(), PanBlock::new);
	public static final DeferredHolder<Block,PanBlock> IRON_PAN = mainblock("iron_frying_pan",
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).noOcclusion(), PanBlock::new);
	public static final DeferredHolder<Block,PanBlock> LEAD_PAN = mainblock("lead_frying_pan",
			Block.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).noOcclusion(), PanBlock::new);
	public static final DeferredHolder<Block,DishBlock> DISH = BLOCKS.registerBlock("dish", DishBlock::new,
			()->Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid));
	public static final DeferredHolder<Block,StewPot> STEW_POT = mainblock("stew_pot",
			
					Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.STONE)
							.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion(),p -> new StewPot(p,CPBlockEntityTypes.STEW_POT));
	public static final DeferredHolder<Block,StewPot> STEW_POT_LEAD = mainblock("lead_stew_pot",
					Block.Properties.of().mapColor(MapColor.COLOR_ORANGE).sound(SoundType.STONE)
							.requiresCorrectToolForDrops().strength(3.5f, 10).noOcclusion(),p -> new StewPot(p,CPBlockEntityTypes.STEW_POT));

	public static final DeferredHolder<Block,BowlBlock> BOWL = foodblock("bowl",
			Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid),p -> new BowlBlock(p, CPBlockEntityTypes.BOWL));
	public static final Pair<DeferredItem<CPBlockItem>, DeferredHolder<Block, Block>> LOAF_BOWL = 
		
		loafblock("loaf_bowl",Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
				.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
				.isViewBlocking(CPBlocks::isntSolid),p -> new BowlBlock(p,  CPBlockEntityTypes.BOWL));
	public static final DeferredHolder<Block,MosaicBlock> MOSAIC = baseblock("mosaic",
			getStoneProps(), MosaicBlock::new, MosaicItem::new);
	public static final DeferredHolder<Block,SilphiumBlock> SILPHIUM = mainblock("silphium_block",
			BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).replaceable().noCollision()
					.instabreak().sound(SoundType.GRASS).offsetType(BlockBehaviour.OffsetType.XZ).ignitedByLava()
					.pushReaction(PushReaction.DESTROY), SilphiumBlock::new);
	public static final DeferredHolder<Block,Block> WALNUT_FRUIT = mainblock("walnut_fruits",
			BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).ignitedByLava(), WalnutFruitBlock::new);
	public static final DeferredHolder<Block,Block> SNAIL_MUCUS=block("snail_mucus",
			BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().instabreak().sound(SoundType.CROP).noOcclusion().isViewBlocking(CPBlocks::isntSolid),TabType.MAIN_AND_DECORATION);

	public static final DeferredHolder<Block,SnailBlock> SNAIL = baseblock("snail_block",
			BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).isViewBlocking(CPBlocks::isntSolid), SnailBlock::new, (x,y)->new CPBlockItem(x,y,TabType.MAIN));
	public static final DeferredHolder<Block,SnailBaitBlock> SNAIL_BAIT = baseblock("snail_bait",
			BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().randomTicks().offsetType(OffsetType.XZ).instabreak().sound(SoundType.CROP).isViewBlocking(CPBlocks::isntSolid), SnailBaitBlock::new, (x,y)->new CPBlockItem(x,y,TabType.MAIN));
	public static final DeferredHolder<Block,Block> LEAD_BLOCK=block("lead_block",
			BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(3.5f, 10).requiresCorrectToolForDrops(),TabType.DECORATION);
	public static final DeferredHolder<Block,LoafDoughBlock> LOAF_DOUGH=foodblock("loaf_dough",
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOL).instabreak().noOcclusion()
		.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
		.isViewBlocking(CPBlocks::isntSolid), LoafDoughBlock::new);
	public static final DeferredHolder<Block,SlabBlock> LOAF=foodblock("loaf",
			BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).sound(SoundType.WOOL).instabreak().noOcclusion()
		.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
		.isViewBlocking(CPBlocks::isntSolid), LoafBlock::new);

	// Bulk register blocks
	static {

		for (MaterialType type : all_materials) {
			String name = type.getName();
			if (type.isDecorationMaterial()) {
				DeferredHolder<Block,Block> base = block(name, getStoneProps(),TabType.DECORATION);
				stoneBlocks.put(name, base);
				type.setBase(()->base.get().defaultBlockState());
				decoblock(name + "_slab", getStoneProps(), SlabBlock::new);
				decoblock(name + "_stairs",getStoneProps(), p -> new StairBlock(type.getBase().get(), p));
				if(name.equals("loaf_heap"))
					hiddenblock(name + "_wall", getStoneProps(), WallBlock::new);
				else
					decoblock(name + "_wall", getStoneProps(), WallBlock::new);
			}
			if (type.isCounterMaterial()) {
				stove(name + "_kitchen_stove", getStoveProps(),
						type.getCounterGrade() == 1 ? CPBlockEntityTypes.STOVE_T1 : CPBlockEntityTypes.STOVE_T2);
				maindecoblock(name + "_chimney_flue", getTransparentProps(), ChimneyFluteBlock::new);
				maindecoblock(name + "_chimney_pot", getTransparentProps(), ChimneyPotBlock::new);
				decoblock(name + "_counter", getStoneProps(), CPHorizontalBlock::new);
				maindecoblock(name + "_counter_with_dolium", getTransparentProps(), CounterDoliumBlock::new);
			}
			if (type.isHypocaustMaterial()) {
				mainblock(name + "_caliduct", getTransparentProps(), CaliductBlock::new);
				mainblock(name + "_hypocaust_firebox", getTransparentProps(), FireboxBlock::new);
			}
			if (type.isPillarMaterial()) {
				decoblock(name + "_column_fluted_plinth",
						(getTransparentProps().strength(2f, 6f)), p -> new BaseColumnBlock(p, true));
				decoblock(name + "_column_fluted_shaft",
						(getTransparentProps().strength(2f, 6f)), p -> new BaseColumnBlock(p, false));
				decoblock(name + "_column_shaft",
						(getTransparentProps().strength(2f, 6f)), p -> new BaseColumnBlock(p, false));
				decoblock(name + "_column_plinth",
						(getTransparentProps().strength(2f, 6f)), p -> new BaseColumnBlock(p, true));
				decoblock(name + "_ionic_column_capital",
						(getTransparentProps().strength(2f, 6f)), p -> new ColumnCapitalBlock(p, true));
				decoblock(name + "_tuscan_column_capital",
						(getTransparentProps().strength(2f, 6f)), p -> new ColumnCapitalBlock(p, false));
				decoblock(name + "_acanthine_column_capital",
						(getTransparentProps().strength(2f, 6f)), p -> new ColumnCapitalBlock(p, true));
				decoblock(name + "_lacunar_tile",
						(getTransparentProps().strength(2f, 6f).isViewBlocking(CPBlocks::isntSolid)), LacunarBlock::new);
				decoblock(name+"_spoked_fence",
						(getTransparentProps().strength(2f, 6f)), SpokedFenceBlock::new);
			}
			if(type.isRoadMaterial()) {
				decoblock(name+"_road_side",
						(getTransparentProps().isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).strength(2f, 6f)), CPRoadSideBlock::new);
				decoblock(name+"_road",
						(getTransparentProps().isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).strength(2f, 6f)), CPRoadBlock::new);
			}
		}

		registerWood("walnut", WALNUT,()-> new TreeGrower("walnut",Optional.empty(),Optional.of(CPWorldGen.WALNUT),Optional.empty()),WALNUT_FRUIT);
		registerBush("fig", ()-> new TreeGrower("fig",Optional.empty(),Optional.of(CPWorldGen.FIG),Optional.empty()));
		registerBush("wolfberry", ()-> new TreeGrower("wolfberry",Optional.empty(),Optional.of(CPWorldGen.WOLFBERRY),Optional.empty()));
		for (String s : CPItems.dishes) {
			baseblock(s,
					Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
							.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
							.isViewBlocking(CPBlocks::isntSolid),CPItems.createSoupProps().get(),
							DishBlock::new,DishItem::new);
			baseblock(s+"_loaf",
					Block.Properties.of().sound(SoundType.WOOD).instabreak().noOcclusion()
							.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
							.isViewBlocking(CPBlocks::isntSolid),CPItems.createLoafSoupProps().get(),
							DishBlock::new,LoafDishItem::new);
		}
	}
	// Convenient block registry wrapper

	// create a bush
	private static void registerBush(String wood, Supplier<TreeGrower> growth) {
		decoblock(wood + "_log", BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
				.strength(2.0F).noOcclusion().sound(SoundType.WOOD),BushLogBlock::new);
		DeferredHolder<Block,Block> a = decoblock(wood + "_fruits", BlockBehaviour.Properties.of()
				.mapColor(MapColor.PLANT).offsetType(OffsetType.XZ).noCollision().randomTicks().instabreak().sound(SoundType.CROP),
				FruitBlock::new);
		leaves.add(
				CPCommonBootStrap.asCompositable(decoblock(wood + "_leaves",BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F)
					.randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(CPBlocks::ocelotOrParrot)
					.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid).ignitedByLava(),
					f -> new FruitsLeavesBlock(f, a)), 0.3F));

		
		CPCommonBootStrap.asCompositable(
				maindecoblock(wood + "_sapling", BlockBehaviour.Properties.of()
						.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.GRASS),
						p -> new CPSaplingBlock(growth.get(),p, 5,5)),
				0.3F);

	}

	// create a wood
	private static void registerWood(String wood, WoodType wt, Supplier<TreeGrower> growth,DeferredHolder<Block,Block> f) {
		DeferredHolder<Block,Block> planks = decoblock(wood + "_planks",
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava(),
		    props -> new CPFlamableBlock(props, 5, 20));
		decoblock(wood + "_button",
		    BlockBehaviour.Properties.of().noCollision().strength(0.5F).sound(SoundType.WOOD).ignitedByLava(),
		    props -> new CPButtonBlock(props, WALNUT_TYPE, 30));
		decoblock(wood + "_door",
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(3.0F).sound(SoundType.WOOD).noOcclusion().ignitedByLava(),
		    props -> new CPDoorBlock(props, WALNUT_TYPE));
		decoblock(wood + "_fence",
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava(),
		    CPWoodFenceBlock::new);
		decoblock(wood + "_fence_gate",
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava(),
		    props -> new CPWoodFenceGateBlock(WALNUT, props));
		leaves.add(CPCommonBootStrap.asCompositable(decoblock(wood + "_leaves",BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.2F)
			.randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(CPBlocks::ocelotOrParrot)
			.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid).ignitedByLava(),
			fp -> new FruitsLeavesBlock(fp, f)), 0.3F));
		DeferredHolder<Block,Block> sl =decoblock("stripped_" + wood + "_log",BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava(), CPWoodRotatedPillarBlock::new);
		decoblock(wood + "_log",BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava(), p -> new CPStripPillerBlock(sl,p));

		decoblock(wood + "_pressure_plate",
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollision().strength(0.5F).sound(SoundType.WOOD).ignitedByLava(),
		    props -> new CPPressurePlateBlock(props, WALNUT_TYPE));
		CPCommonBootStrap.asCompositable(
		    maindecoblock(wood + "_sapling",
		        BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.GRASS).ignitedByLava(),
		        props -> new CPSaplingBlock(growth.get(), props, 5, 5)),
		    0.3F);
		DeferredHolder<Block,Block> s = BLOCKS.registerBlock(wood + "_sign",
				p -> new CPStandingSignBlock(p, wt),()->BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollision()
				.strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
		
		DeferredHolder<Block,Block> ws = BLOCKS.registerBlock(wood + "_wall_sign",
				p -> new CPWallSignBlock(p, wt),()->BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollision()
				.strength(1.0F).sound(SoundType.WOOD).ignitedByLava());
		 
		DeferredHolder<Block,Block> hs = BLOCKS.registerBlock(wood + "_hanging_sign",
			p -> new CPCeilingHangingSignBlock(
	            wt,
	            p
	        ),()->BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .noCollision()
            .strength(1.0F)
            .ignitedByLava());
		DeferredHolder<Block,Block> whs = BLOCKS.registerBlock(wood + "_wall_hanging_sign",
			p -> new CPWallHangingSignBlock(
	            wt,p
	           
	        ),()-> BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .forceSolidOn()
            .instrument(NoteBlockInstrument.BASS)
            .noCollision()
            .strength(1.0F)
            .ignitedByLava());
		

		CPItems.ITEMS.registerItem(wood + "_sign",
				p -> new CPSignItem(p, s.get(), ws.get(), TabType.DECORATION),()->(new Item.Properties()).stacksTo(16));
		CPItems.ITEMS.registerItem(wood + "_hanging_sign",
			p -> new CPHangingSignItem(hs.get(), whs.get(),p, TabType.DECORATION),()->(new Item.Properties()).stacksTo(16));
		
		decoblock(wood + "_slab", 
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD).ignitedByLava(),
		    CPWoodSlabBlock::new);
		decoblock(wood + "_stairs", 
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD),
		    props -> new CPWoodStairBlock(planks.get().defaultBlockState(), props));
		decoblock(wood + "_trapdoor", 
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(CPBlocks::never).ignitedByLava(),
		    props -> new CPTrapDoorBlock(props, WALNUT_TYPE));
		DeferredHolder<Block,Block> sw = decoblock("stripped_" + wood + "_wood", 
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava(),
		    CPWoodRotatedPillarBlock::new);
		decoblock(wood + "_wood", 
		    BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).sound(SoundType.WOOD).ignitedByLava(),
		    props -> new CPStripPillerBlock(sw, props));
	}

	// create a stove
	static DeferredHolder<Block,KitchenStove> stove(String name, Properties props,
			DeferredHolder<BlockEntityType<?>,BlockEntityType<KitchenStoveBlockEntity>> tile) {
		DeferredHolder<Block,KitchenStove> bl = BLOCKS.registerBlock(name, p -> new KitchenStove(p, tile),()->props);
		stoves.add(bl);
		CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(bl.get(), prop, TabType.MAIN_AND_DECORATION), CPItems::createProps);
		return bl;
	}

	// register any block to caupona registry
	static <T extends Block> DeferredHolder<Block,T> mainblock(String name,Properties p, Function<Properties,T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->p);
		CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(blx.get(), prop, TabType.MAIN), CPItems::createProps);
		return blx;
	}
	// register any block to caupona registry
	static <T extends Block> DeferredHolder<Block,T> foodblock(String name,Properties p, Function<Properties,T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->p);
		CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(blx.get(), prop, TabType.FOODS), CPItems::createProps);
		return blx;
	}
	static <T extends Block> Pair<DeferredItem<CPBlockItem>,DeferredHolder<Block,T>> loafblock(String name,Properties p, Function<Properties,T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->p);
		DeferredItem<CPBlockItem> it=CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(blx.get(), prop, TabType.FOODS), CPItems::createProps);
		return Pair.of(it, blx);
	}
	static <T extends Block> DeferredHolder<Block,T> decoblock(String name,Properties p, Function<Properties,T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->p);
		CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(blx.get(), prop, TabType.DECORATION), CPItems::createProps);
		return blx;
	}
	static <T extends Block> DeferredHolder<Block,T> hiddenblock(String name,Properties p, Function<Properties,T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->p);
		CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(blx.get(), prop,TabType.HIDDEN), CPItems::createProps);
		return blx;
	}
	static <T extends Block> DeferredHolder<Block,T> maindecoblock(String name,Properties p, Function<Properties,T> bl) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->p);
		CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(blx.get(), prop, TabType.MAIN_AND_DECORATION), CPItems::createProps);
		return blx;
	}

	// register any block to caupona registry with custom item factory
	static <T extends Block> DeferredHolder<Block,T> baseblock(String name,Properties prop, Function<Properties,T> bl, BiFunction<T,Item.Properties, Item> toitem) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->prop);
		CPItems.ITEMS.registerItem(name, p -> toitem.apply(blx.get(),p),CPItems::createProps);
		return blx;
	}
	static <T extends Block> DeferredHolder<Block,T> baseblock(String name,Properties prop,Item.Properties iprop, Function<Properties,T> bl, BiFunction<T,Item.Properties, Item> toitem) {
		DeferredHolder<Block,T> blx = BLOCKS.registerBlock(name, bl,()->prop);
		CPItems.ITEMS.registerItem(name, p -> toitem.apply(blx.get(),p),()->iprop);
		return blx;
	}
	// register basic block to caupona registry
	static DeferredHolder<Block,Block> block(String name, Properties props,TabType tab) {
		DeferredHolder<Block,Block> blx = BLOCKS.registerBlock(name, prop -> new Block(prop),()->props);
		CPItems.ITEMS.registerItem(name, prop -> new CPBlockItem(blx.get(), prop, tab), CPItems::createProps);

		return blx;
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