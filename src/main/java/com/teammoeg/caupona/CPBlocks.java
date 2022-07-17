package com.teammoeg.caupona;

import com.teammoeg.caupona.blocks.KitchenStove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.teammoeg.caupona.blocks.BowlBlock;
import com.teammoeg.caupona.blocks.CPHorizontalBlock;
import com.teammoeg.caupona.blocks.ChimneyPotBlock;
import com.teammoeg.caupona.blocks.CounterDoliumBlock;
import com.teammoeg.caupona.blocks.StewPot;
import com.teammoeg.caupona.event.RegistryEvents;
import com.teammoeg.caupona.items.CPBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class CPBlocks {
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
			Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0, 0).noOcclusion()
					.isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid)
					.isViewBlocking(CPBlocks::isntSolid),
			CPTileTypes.BOWL, CPBlockItem::new);
	public static final String[] materials_C = new String[] { "brick", "opus_incertum", "opus_latericium", "mud",
			"stone_brick" };
	public static final String[] stones = new String[] { "mixed_bricks", "opus_incertum", "opus_latericium",
			"opus_reticulatum", "felsic_tuff_bricks", "felsic_tuff" };
	public static Map<String, Block> stoneBlocks = ImmutableMap.of();
	public static List<Block> transparentBlocks = new ArrayList<>();

	public static void init() {
		stoneBlocks = new HashMap<>();
		for (String stone : stones) {
			Block base = register(stone, new Block(getStoneProps()));
			stoneBlocks.put(stone, base);
			register(stone + "_slab", new SlabBlock(getStoneProps()));
			register(stone + "_stairs", new StairBlock(base::defaultBlockState, getStoneProps()));
			register(stone + "_wall", new WallBlock(getStoneProps()));
		}
		for (String mat : materials_C) {
			register(mat + "_chimney_flue", new Block(getStoneProps()));
			transparentBlocks.add(register(mat + "_chimney_pot", new ChimneyPotBlock(getStoneProps())));
			register(mat + "_counter", new CPHorizontalBlock(getStoneProps()));
			transparentBlocks.add(register(mat + "_counter_with_dolium", new CounterDoliumBlock(getTransparentProps())));
		}
	}

	public static Block register(String name, Block bl) {
		ResourceLocation registryName = new ResourceLocation(Main.MODID, name);
		bl.setRegistryName(registryName);

		RegistryEvents.registeredBlocks.add(bl);
		Item item = new BlockItem(bl, new Item.Properties().tab(Main.itemGroup));
		if (item != null) {
			item.setRegistryName(registryName);
			RegistryEvents.registeredItems.add(item);
		}
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
}