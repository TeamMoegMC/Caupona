package com.teammoeg.caupona;

import com.teammoeg.caupona.blocks.KitchenStove;
import com.teammoeg.caupona.blocks.BowlBlock;
import com.teammoeg.caupona.blocks.StewPot;
import com.teammoeg.caupona.items.CPBlockItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class CPBlocks {
	
	public static void init() {
	}

	public static Block stew_pot = new StewPot("stew_pot",
			Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
					.strength(2, 10).noOcclusion(),
					CPTileTypes.STEW_POT,
			CPBlockItem::new);
	public static Block stove1 = new KitchenStove("mud_kitchen_stove",Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
			.strength(2, 10).noOcclusion().lightLevel(s->s.getValue(KitchenStove.LIT)?7:0).isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid),
			CPTileTypes.STOVE1,
			CPBlockItem::new);
	public static Block stove2 = new KitchenStove("brick_kitchen_stove",Block.Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops()
			.strength(2, 10).noOcclusion().lightLevel(s->s.getValue(KitchenStove.LIT)?9:0).isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid),
			CPTileTypes.STOVE2,
			CPBlockItem::new);
	public static Block bowl= new BowlBlock("bowl",Block.Properties.of(Material.WOOD).sound(SoundType.WOOD)
			.strength(0,0).noOcclusion().isRedstoneConductor(CPBlocks::isntSolid).isSuffocating(CPBlocks::isntSolid).isViewBlocking(CPBlocks::isntSolid),
			CPTileTypes.BOWL,
			CPBlockItem::new);
	   private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
		      return false;
		   }
}