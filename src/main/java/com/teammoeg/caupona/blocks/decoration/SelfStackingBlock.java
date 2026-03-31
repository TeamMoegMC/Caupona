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

package com.teammoeg.caupona.blocks.decoration;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.BlockHitResult;

public class SelfStackingBlock extends SlabBlock {

	public SelfStackingBlock(Properties properties) {
		super(properties);
	}
	public ReplacableType checkPlacable(BlockState s) {
		if(s.isEmpty())
			return ReplacableType.EMPTY;
		if(s.getBlock().asItem()==this.asItem()) {
			if(s.getValue(TYPE)==SlabType.DOUBLE)
				return ReplacableType.FULL;
			return ReplacableType.ALLOW;
		}
		return ReplacableType.DENY;
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if(state.getValue(TYPE)!=SlabType.DOUBLE){
			player.getInventory().placeItemBackInInventory( new ItemStack(this.asItem()));
			level.removeBlock(pos, false);
			return InteractionResult.SUCCESS;
		}
		for(int i=1;;i++) {
			BlockPos newPos=pos.below(i);
			BlockState bs=level.getBlockState(newPos);
			ReplacableType type=checkPlacable(bs);
			if(type.shouldSkip())
				break;
			if(type.isAllowed()) {
				if(type==ReplacableType.ALLOW)
					level.removeBlock(newPos, false);
				else
					level.setBlock(newPos.above(), this.defaultBlockState().setValue(TYPE, SlabType.TOP), UPDATE_ALL);
				player.getInventory().placeItemBackInInventory(new ItemStack(this.asItem()));
				return InteractionResult.SUCCESS;
			}
		}
		for(int i=1;;i++) {
			BlockPos newPos=pos.above(i);
			BlockState bs=level.getBlockState(newPos);
			ReplacableType type=checkPlacable(bs);
			if(type.isAllowed()||type==ReplacableType.DENY) {
				if(type==ReplacableType.ALLOW)
					level.removeBlock(newPos, false);
				else
					level.setBlock(newPos.below(), this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM), UPDATE_ALL);
				player.getInventory().placeItemBackInInventory(new ItemStack(this.asItem()));
				return InteractionResult.SUCCESS;
			}
		}

		//return super.useWithoutItem(state, level, pos, player, hitResult);
	}
	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if(stack.is(this.asItem())) {
			if(state.getValue(TYPE)!=SlabType.DOUBLE){
				if(!player.getAbilities().instabuild)
					stack.shrink(1);
				level.setBlock(pos, state.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
				return InteractionResult.SUCCESS;
			}
			for(int i=1;;i++) {
				BlockPos newPos=pos.above(i);
				BlockState bs=level.getBlockState(newPos);
				ReplacableType type=checkPlacable(bs);
				if(type.shouldSkip())
					break;
				if(type.isAllowed()) {
					if(type==ReplacableType.ALLOW)
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
					else
						level.setBlock(newPos, this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM), UPDATE_ALL);
					return InteractionResult.SUCCESS;
				}
			}
			for(int i=1;;i++) {
				BlockPos newPos=pos.below(i);
				BlockState bs=level.getBlockState(newPos);
				ReplacableType type=checkPlacable(bs);
				if(type.shouldSkip())
					break;
				if(type.isAllowed()) {
					if(type==ReplacableType.ALLOW)
						level.setBlock(newPos, bs.setValue(TYPE, SlabType.DOUBLE), UPDATE_ALL);
					else
						level.setBlock(newPos, this.defaultBlockState().setValue(TYPE, SlabType.TOP), UPDATE_ALL);
					return InteractionResult.SUCCESS;
				}
			}
			return InteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return (level.getBlockState(pos.below()).isSolid()&&state.getValue(TYPE)!=SlabType.TOP)||(level.getBlockState(pos.above()).isSolid()&&state.getValue(TYPE)!=SlabType.BOTTOM);
	}
    @Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos currentPos, Direction facing, BlockPos facingPos, BlockState facingState,
		RandomSource random) {
    	return facing.getAxis()!=Axis.Y||canSurvive(state,level,currentPos)
            ? super.updateShape(facingState, level, ticks, currentPos, facing, facingPos, state, random)
            : Blocks.AIR.defaultBlockState();
    }

    
}
