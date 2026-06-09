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

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.util.WorldDropOperation;

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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class DishBlock extends CPRegisteredEntityBlock<DishBlockEntity> {
	boolean loaf;
	public DishBlock(Properties blockProps,boolean isLoaf) {
		super(blockProps, CPBlockEntityTypes.DISH);
		CPBlocks.dishes.add(this);
		this.loaf=isLoaf;
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
	}


	static final VoxelShape shape = Block.box(0, 0, 0, 16, 3, 16);

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	protected List<ItemStack> getDrops(BlockState p_state, Builder p_params) {
		List<ItemStack> li=super.getDrops(p_state, p_params);
		if (p_params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof DishBlockEntity bowl) {
			ItemResource ir=bowl.getInternal().getResource(0);
			li.add(ir.toStack(bowl.getInternal().getAmountAsInt(0)));
		}
		return li;
	}



	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player,
			BlockHitResult hit) {
		InteractionResult p = super.useWithoutItem(state, worldIn, pos, player, hit);
		if (p.consumesAction())
			return p;
		if (worldIn.getBlockEntity(pos) instanceof DishBlockEntity bowl) {
			ItemResource ir=bowl.getInternal().getResource(0);
			ItemStack stack=ir.toStack();
			@Nullable Consumable fp = ir.get(DataComponents.CONSUMABLE);
			if(fp!=null) {
				
				if (bowl.isInfinite) {
					if(fp.canConsume(player, stack)) {
						fp.onConsume(worldIn, player, stack);
						bowl.syncData();
					}
				} else {
					if(fp.canConsume(player, stack)) {
						try(Transaction trans=Transaction.openRoot()){
							if(bowl.getInternal().extract(ir, 1, trans)>0) {
								ItemStack iout=fp.onConsume(worldIn, player, stack);
								int count=iout.getCount();
								if(!iout.isEmpty()) {
									ItemResource toOut=bowl.getInternal().getResourceFrom(iout);
									count-=bowl.getInternal().insert(toOut, count, trans);
									if(count>0) {
										WorldDropOperation drops=new WorldDropOperation(worldIn,pos);
										drops.addDrops(toOut.toStack(count));
										drops.updateSnapshots(trans);
									}
								}else
									worldIn.removeBlock(pos, false);
								trans.commit();
							}
						}
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
		if (pLevel.getBlockEntity(pPos) instanceof DishBlockEntity bowl) {
			bowl.setComponents(DataComponentMap.EMPTY);
			ItemResource ir=bowl.getInternal().getResourceFrom(pStack);
			try(Transaction trans=Transaction.openRoot()){
				bowl.getInternal().insert(0, ir, 1,trans);
				trans.commit();
			}
		}
	}
	@Override
	public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData, Player player) {
		if (level.getBlockEntity(pos) instanceof DishBlockEntity bowl) {
			if (bowl.getInternal() == null)
				return ItemStack.EMPTY;
			return bowl.getInternal().getResource(0).toStack();
		}
		return super.getCloneItemStack(level, pos, state, includeData, player);
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
