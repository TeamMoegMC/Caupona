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

package com.teammoeg.caupona.blocks.foods;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.items.DishItem;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;

public class DishBlock extends CPRegisteredEntityBlock<DishTileEntity> {
	public static final IntegerProperty PAN = IntegerProperty.create("pan", 0, 2);

	public DishBlock(String name, Properties blockProps) {
		super(name, blockProps, CPTileTypes.DISH, null);
		this.registerDefaultState(this.defaultBlockState().setValue(PAN, 0));
		CPBlocks.dishes.add(this);
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(PAN);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(PAN, 0);
	}

	static final VoxelShape shape = Block.box(0, 0, 0, 16, 3, 16);

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
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (!(newState.getBlock() instanceof DishBlock)) {
			if (tileEntity instanceof DishTileEntity) {
				DishTileEntity te = (DishTileEntity) tileEntity;

				super.popResource(worldIn, pos, te.internal);
			}
			worldIn.removeBlockEntity(pos);
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		DishTileEntity tileEntity = (DishTileEntity) worldIn.getBlockEntity(pos);
		if (tileEntity.internal != null && tileEntity.internal.getItem() instanceof DishItem
				&& tileEntity.internal.isEdible()) {
			FoodProperties fp = tileEntity.internal.getFoodProperties(player);
			if (tileEntity.isInfinite) {
				if (player.canEat(fp.canAlwaysEat())) {
					player.eat(worldIn, tileEntity.internal.copy());
					tileEntity.syncData();
				}
			} else {
				if (player.canEat(fp.canAlwaysEat())) {
					ItemStack iout = tileEntity.internal.getContainerItem();
					player.eat(worldIn, tileEntity.internal);
					tileEntity.internal = iout;
					if (tileEntity.internal.is(Items.BOWL)) {
						worldIn.setBlockAndUpdate(pos, CPBlocks.DISH.defaultBlockState());
					} else {
						worldIn.removeBlock(pos, false);
					}
					tileEntity.syncData();
				}
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
		if (tileEntity instanceof DishTileEntity) {
			DishTileEntity te = (DishTileEntity) tileEntity;
			te.internal = ItemHandlerHelper.copyStackWithSize(pStack, 1);
		}
	}

	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos,
			Player player) {
		BlockEntity tileEntity = level.getBlockEntity(pos);
		if (tileEntity instanceof DishTileEntity) {
			DishTileEntity te = (DishTileEntity) tileEntity;
			if (te.internal == null)
				return ItemStack.EMPTY;
			return te.internal.copy();
		}
		return this.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		DishTileEntity te = (DishTileEntity) pLevel.getBlockEntity(pPos);
		if (te.internal == null || te.internal.isEmpty() || !te.internal.isEdible()) {
			return 0;
		}
		return 15;
	}
}
