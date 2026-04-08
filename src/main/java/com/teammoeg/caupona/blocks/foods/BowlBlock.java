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

package com.teammoeg.caupona.blocks.foods;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.item.StewItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BowlBlock extends CPRegisteredEntityBlock<BowlBlockEntity> {

	public BowlBlock(Properties blockProps, DeferredHolder<BlockEntityType<?>,BlockEntityType<BowlBlockEntity>> ste) {
		super(blockProps, ste);
	}

	static final VoxelShape shape = Block.box(2.8, 0, 2.8, 13.2, 5.2, 13.2);

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	protected List<ItemStack> getDrops(BlockState p_state, Builder p_params) {
		List<ItemStack> li=super.getDrops(p_state, p_params);
		if (p_params.getParameter(LootContextParams.BLOCK_ENTITY) instanceof BowlBlockEntity bowl) {
			li.add(bowl.getInternal());
		}
		return li;
	}



	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player,
			BlockHitResult hit) {
		InteractionResult p = super.useWithoutItem(state, worldIn, pos, player, hit);
		if (p.consumesAction())
			return p;
		if (worldIn.getBlockEntity(pos) instanceof BowlBlockEntity bowl&&bowl.getInternal() != null && bowl.getInternal().getItem() instanceof StewItem
				) {
			@Nullable Consumable fp = bowl.getInternal().get(DataComponents.CONSUMABLE);
			if(fp!=null) {
				if (bowl.isInfinite) {
					if(fp.canConsume(player, bowl.getInternal())) {
						fp.onConsume(worldIn, player, bowl.getInternal().copy());
						bowl.syncData();
					}
				} else {
					if(fp.canConsume(player, bowl.getInternal())) {
						ItemStack iout=fp.onConsume(worldIn, player, bowl.getInternal().copy());
						bowl.setInternal(iout);
						if(!bowl.getInternal().isEmpty()) {
							bowl.syncData();
						}else
							worldIn.removeBlock(pos, false);
					}
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		if (pLevel.getBlockEntity(pPos) instanceof BowlBlockEntity bowl) {
			bowl.setComponents(DataComponentMap.EMPTY);
			bowl.setInternal(pStack.copyWithCount(1));
		}
	}
	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
		if (level.getBlockEntity(pos) instanceof BowlBlockEntity bowl) {
			if (bowl.getInternal() == null)
				return ItemStack.EMPTY;
			return bowl.getInternal().copy();
		}
		return super.getCloneItemStack(level, pos, state, includeData, player);
	}


	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction dir) {
		if (pLevel.getBlockEntity(pPos) instanceof BowlBlockEntity bowl&&bowl.getInternal() != null && !bowl.getInternal().isEmpty() && bowl.getInternal().get(DataComponents.CONSUMABLE)!=null) {
			return 15;
		}
		return 0;
	}
	@Override
	public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 20;
	}

	@Override
	public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return 5;
	}
}
