package com.teammoeg.caupona;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.teammoeg.caupona.blocks.BowlBlock;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.ChimneyPotBlock;
import com.teammoeg.caupona.blocks.DishBlock;
import com.teammoeg.caupona.blocks.GravyBoatBlock;
import com.teammoeg.caupona.blocks.dolium.CounterDoliumBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleBoulderBlock;
import com.teammoeg.caupona.blocks.fumarole.FumaroleVentBlock;
import com.teammoeg.caupona.blocks.fumarole.PumiceBloomBlock;
import com.teammoeg.caupona.blocks.others.CPStandingSignBlock;
import com.teammoeg.caupona.blocks.others.CPWallSignBlock;
import com.teammoeg.caupona.blocks.plants.BushLogBlock;
import com.teammoeg.caupona.blocks.plants.CPStripPillerBlock;
import com.teammoeg.caupona.blocks.plants.FruitBlock;
import com.teammoeg.caupona.blocks.plants.FruitsLeavesBlock;
import com.teammoeg.caupona.blocks.pot.StewPot;
import com.teammoeg.caupona.blocks.stove.KitchenStove;
import com.teammoeg.caupona.items.CPBlockItem;
import com.teammoeg.caupona.items.DishItem;
import com.teammoeg.caupona.worldgen.FigTreeGrower;
import com.teammoeg.caupona.worldgen.WalnutTreeGrower;
import com.teammoeg.caupona.worldgen.WolfberryTreeGrower;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.WoodButtonBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class CPBlocks {
	// static string data 
	public static final String[] counters = new String[] { "brick", "opus_incertum", "opus_latericium", "mud",
			"stone_brick" };
	public static final String[] stones = new String[] { "mixed_bricks", "opus_incertum", "opus_latericium",
			"opus_reticulatum", "felsic_tuff_bricks", "felsic_tuff" };
	public static final String[] woods = new String[] { "walnut" };
	public static final String[] pillar_materials = new String[] { "stone", "quartz", "felsic_tuff", "calcite" };
	
	// Dynamic block types
	public static final List<Block> signs = new ArrayList<>();
	public static final Map<String, Block> stoneBlocks = new HashMap<>();
	public static final List<Block> transparentBlocks = new ArrayList<>();
	public static final List<Block> chimney = new ArrayList<>();
	public static final List<Block> dolium = new ArrayList<>();
	public static final List<Block> dishes = new ArrayList<>();

	// useful blocks
	public static Block stew_pot = new StewPot("stew_pot", Block.Properties.of(Material.STONE).sound(SoundType.STONE)
			.requiresCorrectToolForDrops().strength(2, 10).noOcclusion(), CPTileTypes.STEW_POT, CPBlockItem::new);
	public static Block stove1 = new KitchenStove("mud_kitchen_stove", getStoveProps(), CPTileTypes.STOVE1,
			CPBlockItem::new);
	public static Block stove2 = new KitchenStove("brick_kitchen_stove", getStoveProps(), CPTileTypes.STOVE2,
			CPBlockItem::new);
	public static Block stove3 = new KitchenStove("opus_incertum_kitchen_stove", getStoveProps(), CPTileTypes.STOVE2,
			CPBlockItem::new);
	public static Block stove4 = new KitchenStove("opus_latericium_kitchen_stove", getStoveProps(), CPTileTypes.STOVE2,
			CPBlockItem::new);
	public static Block stove5 = new KitchenStove("stone_brick_kitchen_stove", getStoveProps(), CPTileTypes.STOVE2,
			CPBlockItem::new);
	public static Block bowl = new BowlBlock("bowl",
			Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid),
			CPTileTypes.BOWL, null);

	public static Block WALNUT_LOG;
	public static Block WALNUT_LEAVE;
	public static Block WALNUT_PLANKS;
	public static Block WALNUT_SAPLINGS;
	public static Block FIG_LOG;
	public static Block FIG_LEAVE;
	public static Block FIG_SAPLINGS;
	public static Block WOLFBERRY_LOG;
	public static Block WOLFBERRY_LEAVE;
	public static Block WOLFBERRY_SAPLINGS;
	public static final Block FUMAROLE_BOULDER = register("fumarole_boulder", transparent(new FumaroleBoulderBlock(
			getStoneProps().isViewBlocking(CPBlocks::isntSolid).noOcclusion().isSuffocating(CPBlocks::isntSolid))));
	public static final Block FUMAROLE_VENT = transparent(new FumaroleVentBlock("fumarole_vent",
			getStoneProps().isViewBlocking(CPBlocks::isntSolid).noOcclusion().isSuffocating(CPBlocks::isntSolid),
			CPBlockItem::new));
	public static final Block PUMICE = register("pumice", new Block(getStoneProps()));
	public static final Block PUMICE_BLOOM = register("pumice_bloom", transparent(new PumiceBloomBlock(getStoneProps().noOcclusion())));
	public static final GravyBoatBlock GRAVY_BOAT=registerBlock("gravy_boat",new GravyBoatBlock(Block.Properties.of(Material.GLASS).sound(SoundType.GLASS).instabreak().noOcclusion().isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid)));
		
	public static final WoodType WALNUT = WoodType.register(WoodType.create("caupona:walnut"));
	

	public static void init() {
		for (String stone : stones) {
			Block base = register(stone, new Block(getStoneProps()));
			stoneBlocks.put(stone, base);
			register(stone + "_slab", new SlabBlock(getStoneProps()));
			register(stone + "_stairs", new StairBlock(base::defaultBlockState, getStoneProps()));
			register(stone + "_wall", new WallBlock(getStoneProps()));
		}
		for (String mat : counters) {
			transparentBlocks.add(register(mat + "_chimney_flue", new Block(getTransparentProps())));
			transparentBlocks.add(register(mat + "_chimney_pot", new ChimneyPotBlock(getTransparentProps())));
			register(mat + "_counter", new CPHorizontalBlock(getStoneProps()));
			transparentBlocks.add(register(mat + "_counter_with_dolium", new CounterDoliumBlock(getTransparentProps())));
		}
		
		for (String pil : pillar_materials) {
			transparentBlocks.add(register(pil+"_column_fluted_plinth", new Block(getTransparentProps())));
			transparentBlocks.add(register(pil+"_column_fluted_shaft", new Block(getTransparentProps())));
			transparentBlocks.add(register(pil+"_column_shaft", new Block(getTransparentProps())));
			transparentBlocks.add(register(pil+"_column_plinth", new Block(getTransparentProps())));
			transparentBlocks.add(register(pil+"_ionic_column_capital", new CPHorizontalBlock(getTransparentProps())));
			transparentBlocks.add(register(pil+"_tuscan_column_capital", new CPHorizontalBlock(getTransparentProps())));
			transparentBlocks.add(register(pil+"_acanthine_column_capital", new CPHorizontalBlock(getTransparentProps())));
		}
		registerWood("walnut", WALNUT, WalnutTreeGrower::new, l -> WALNUT_PLANKS = l, l -> WALNUT_LOG = l,
				l -> WALNUT_LEAVE = l, l -> WALNUT_SAPLINGS = l);
		registerBush("fig",  FigTreeGrower::new, l -> FIG_LOG = l,
				l -> FIG_LEAVE = l, l -> FIG_SAPLINGS = l);
		registerBush("wolfberry",  WolfberryTreeGrower::new, l -> WOLFBERRY_LOG = l,
				l -> WOLFBERRY_LEAVE = l, l -> WOLFBERRY_SAPLINGS = l);
		for(String s:CPItems.dishes) {
			new DishItem(new DishBlock(s,Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).instabreak().noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid)),CPItems.createSoupProps(),s);
		}
	}
	private static void registerBush(String wood, Supplier<AbstractTreeGrower> growth,
			 Consumer<Block> glog, Consumer<Block> gleave, Consumer<Block> gsap) {
		glog.accept(register(wood + "_log",new BushLogBlock(BlockBehaviour.Properties.of(Material.WOOD, (p_152624_) ->MaterialColor.WOOD).strength(2.0F).noOcclusion().sound(SoundType.WOOD))));
		gleave.accept(
		register(wood + "_leaves",leaves(SoundType.GRASS,
		transparent(register(wood + "_fruits",new FruitBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)))))));
		Block sapling = register(wood + "_sapling", new SaplingBlock(growth.get(), BlockBehaviour.Properties
				.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
		transparentBlocks.add(sapling);
		gsap.accept(sapling);
		
	}
	private static void registerWood(String wood, WoodType wt, Supplier<AbstractTreeGrower> growth,
			Consumer<Block> gplank, Consumer<Block> glog, Consumer<Block> gleave, Consumer<Block> gsap) {
		Block planks = register(wood + "_planks", new Block(BlockBehaviour.Properties
				.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
		gplank.accept(planks);
		register(wood + "_button", new WoodButtonBlock(
				BlockBehaviour.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.WOOD)));
		register(wood + "_door", new DoorBlock(BlockBehaviour.Properties
				.of(Material.WOOD, planks.defaultMaterialColor()).strength(3.0F).sound(SoundType.WOOD).noOcclusion()));
		register(wood + "_fence", new FenceBlock(BlockBehaviour.Properties
				.of(Material.WOOD, planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
		register(wood + "_fence_gate", new FenceGateBlock(BlockBehaviour.Properties
				.of(Material.WOOD, planks.defaultMaterialColor()).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
		gleave.accept(
				register(wood + "_leaves", leaves(SoundType.GRASS,
				transparent(register(wood + "_fruits",new FruitBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP)))))));
		glog.accept(register(wood + "_log", log(MaterialColor.WOOD, MaterialColor.PODZOL,
				register("stripped_" + wood + "_log", log(MaterialColor.WOOD, MaterialColor.WOOD, null)))));

		register(wood + "_pressure_plate",
				new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
						BlockBehaviour.Properties.of(Material.WOOD, planks.defaultMaterialColor()).noCollission()
								.strength(0.5F).sound(SoundType.WOOD)));
		Block sapling = register(wood + "_sapling", new SaplingBlock(growth.get(), BlockBehaviour.Properties
				.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS)));
		transparentBlocks.add(sapling);
		gsap.accept(sapling);
		register(wood + "_sign", new SignItem((new Item.Properties()).stacksTo(16).tab(Main.itemGroup),
				registerBlock(wood + "_sign",
						new CPStandingSignBlock(BlockBehaviour.Properties.of(Material.WOOD).noCollission()
								.strength(1.0F).sound(SoundType.WOOD), wt)),
				registerBlock(wood + "_wall_sign", new CPWallSignBlock(
						BlockBehaviour.Properties.of(Material.WOOD).noCollission().strength(1.0F).sound(SoundType.WOOD),
						wt))));
		register(wood + "_slab", new SlabBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
				.strength(2.0F, 3.0F).sound(SoundType.WOOD)));
		register(wood + "_stairs", new StairBlock(planks::defaultBlockState, BlockBehaviour.Properties.copy(planks)));
		register(wood + "_trapdoor", new TrapDoorBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
				.strength(3.0F).sound(SoundType.WOOD).noOcclusion().isValidSpawn(CPBlocks::never)));
		register(wood + "_wood", new CPStripPillerBlock(
				register("stripped_" + wood + "_wood",
						new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD)
								.strength(2.0F).sound(SoundType.WOOD))),
				BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).strength(2.0F).sound(SoundType.WOOD)));
	}

	private static LeavesBlock leaves(SoundType p_152615_,Block fruit) {
		return transparent(new FruitsLeavesBlock(BlockBehaviour.Properties.of(Material.LEAVES).strength(0.2F).randomTicks()
				.sound(p_152615_).noOcclusion().isValidSpawn(CPBlocks::ocelotOrParrot)
				.isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid),fruit));
	}

	private static RotatedPillarBlock log(MaterialColor pTopColor, MaterialColor pBarkColor, Block st) {
		if (st == null)
			return new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, (p_152624_) -> {
				return p_152624_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? pTopColor : pBarkColor;
			}).strength(2.0F).sound(SoundType.WOOD));
		return new CPStripPillerBlock(st, BlockBehaviour.Properties.of(Material.WOOD, (p_152624_) -> {
			return p_152624_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? pTopColor : pBarkColor;
		}).strength(2.0F).sound(SoundType.WOOD));
	}

	public static <T extends Item> T register(String name, T item) {

		ResourceLocation registryName = new ResourceLocation(Main.MODID, name);
		item.setRegistryName(registryName);
		RegistryEvents.registeredItems.add(item);
		return item;

	}

	public static <T extends Block> T register(String name, T bl) {
		ResourceLocation registryName = new ResourceLocation(Main.MODID, name);
		bl.setRegistryName(registryName);

		RegistryEvents.registeredBlocks.add(bl);
		Item item = new BlockItem(bl, new Item.Properties().tab(Main.itemGroup));
		item.setRegistryName(registryName);
		RegistryEvents.registeredItems.add(item);
		return bl;
	}

	public static <T extends Block> T transparent(T bl) {
		transparentBlocks.add(bl);
		return bl;
	}

	public static <T extends Block> T registerBlock(String name, T bl) {
		ResourceLocation registryName = new ResourceLocation(Main.MODID, name);
		bl.setRegistryName(registryName);

		RegistryEvents.registeredBlocks.add(bl);
		return bl;
	}

	private static Properties getStoneProps() {
		return Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops();
	}

	private static Properties getStoveProps() {
		return Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2, 10)
				.noOcclusion().lightLevel(s -> s.getValue(KitchenStove.LIT) ? 9 : 0)
				.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid);
	}

	private static Properties getTransparentProps() {
		return Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2, 10)
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