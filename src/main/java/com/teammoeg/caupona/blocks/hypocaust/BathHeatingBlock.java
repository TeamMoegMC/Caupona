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

package com.teammoeg.caupona.blocks.hypocaust;

import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import com.teammoeg.caupona.client.CPParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.registries.DeferredHolder;

public abstract class BathHeatingBlock<V extends BathHeatingBlockEntity> extends CPHorizontalEntityBlock<V> {
	public static final VoxelShape SHAPE=Shapes.or(Block.box(0, 0, 0, 16, 3, 16), Block.box(0, 13, 0, 16, 16, 16), Block.box(1, 3, 1, 15, 13, 15));
	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@SuppressWarnings("deprecation")
	public BathHeatingBlock(DeferredHolder<BlockEntityType<?>,BlockEntityType<V>> blockEntity, Properties p_54120_) {
		super(blockEntity, p_54120_);
	}

	@Override
	protected boolean propagatesSkylightDown(BlockState state) {
		return true;
	}

	@Override
	public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
		super.animateTick(pState, pLevel, pPos, pRandom);
		if (pRandom.nextDouble() < 0.05 && pLevel.getFluidState(pPos.above()).is(FluidTags.WATER)) {
			if (pLevel.getBlockEntity(pPos) instanceof BathHeatingBlockEntity bath) {
				if (bath.getHeat() > 0) {
					pLevel.addParticle(CPParticles.STEAM.get(), pPos.getX() + pRandom.nextFloat(), pPos.getY() + 2,
							pPos.getZ() + pRandom.nextFloat(), 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}

}
