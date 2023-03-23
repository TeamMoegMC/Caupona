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

package com.teammoeg.caupona.blocks.plants;

import com.teammoeg.caupona.CPMain;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FruitBlock extends CropBlock {
	public static final TagKey<Block> growable = BlockTags.create(new ResourceLocation(CPMain.MODID, "fruits_growable"));

	public FruitBlock(Properties p_52247_) {
		super(p_52247_);
	}

	static final VoxelShape shape = Block.box(4, 4, 4, 12, 16, 12);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return false;
	}

	@Override
	public int getMaxAge() {
		return 4;
	}

	/**
	 * Performs a random tick on a block.
	 */
	@SuppressWarnings("deprecation")
	@Override

	public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return; // Forge: prevent loading unloaded chunks when checking neighbor's light
		if (pLevel.getRawBrightness(pPos, 0) >= 9) {
			int i = this.getAge(pState);
			if (i < this.getMaxAge()) {
				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(pLevel, pPos, pState,
						pRandom.nextInt(17) == 0)) {
					pLevel.setBlock(pPos, this.getStateForAge(i + 1), 2);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(pLevel, pPos, pState);
				}
			}
		}

	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		return pLevel.getBlockState(pPos.above()).is(growable);
	}

	protected ItemLike getBaseSeedId() {
		return this;
	}

}
