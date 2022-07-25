package com.teammoeg.caupona.util;

import com.teammoeg.caupona.Main;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChimneyHelper {
	public static final TagKey<Block> chimney=BlockTags.create(new ResourceLocation(Main.MODID,"chimney"));
	public static final TagKey<Block> chimney_pot=BlockTags.create(new ResourceLocation(Main.MODID,"chimney_pot"));
	public static final TagKey<Block> chimney_ignore=BlockTags.create(new ResourceLocation(Main.MODID,"chimney_ignore"));
	public ChimneyHelper() {
	}
	public static BlockPos getNearestChimney(Level l,BlockPos from) {
		ChunkAccess c=l.getChunk(from);
		BlockPos start=from.above();
		BlockState bs=c.getBlockState(start);
		for(int i=0;i<2;i++) {
			if(bs.isAir()||bs.is(chimney_ignore)) {
				start=start.above();
				bs=c.getBlockState(start);
			}
		}
		if(!bs.is(chimney))return null;
		while(bs.is(chimney)) {
			start=start.above();
			bs=c.getBlockState(start);
		}
		if(bs.is(chimney_pot)||bs.isAir()||bs.is(chimney_ignore))return start;
		return null;
	}
}
