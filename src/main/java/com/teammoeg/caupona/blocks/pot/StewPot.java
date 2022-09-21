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
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.blocks.pot;

import java.util.Random;
import java.util.function.BiFunction;

import com.teammoeg.caupona.blocks.CPBaseTileBlock;
import com.teammoeg.caupona.client.Particles;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.items.StewItem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

public class StewPot extends CPBaseTileBlock<StewPotTileEntity> implements LiquidBlockContainer {
	public static final EnumProperty<Axis> FACING = BlockStateProperties.HORIZONTAL_AXIS;

	public StewPot(String name, Properties blockProps, RegistryObject<BlockEntityType<StewPotTileEntity>> ste,
			BiFunction<Block, Item.Properties, Item> createItemBlock) {
		super(name, blockProps, ste, createItemBlock);
	}

	static final VoxelShape shape = Block.box(1, 0, 1, 15, 12, 15);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@SuppressWarnings("deprecation")
	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		StewPotTileEntity tileEntity = (StewPotTileEntity) worldIn.getBlockEntity(pos);
		if (tileEntity.canAddFluid()) {
			ItemStack held = player.getItemInHand(handIn);
			if (held.isEmpty() && player.isShiftKeyDown()) {
				tileEntity.getTank().setFluid(FluidStack.EMPTY);
				tileEntity.current = null;
				return InteractionResult.SUCCESS;
			}
			if (held.getItem() instanceof StewItem) {
				if (tileEntity.tryAddFluid(BowlContainingRecipe.extractFluid(held))) {
					ItemStack ret = held.getContainerItem();
					held.shrink(1);
					if (!player.addItem(ret))
						player.drop(ret, false);
				}

				return InteractionResult.sidedSuccess(worldIn.isClientSide);
			}
			if (FluidUtil.interactWithFluidHandler(player, handIn, tileEntity.getTank()))
				return InteractionResult.SUCCESS;

		}
		if (handIn == InteractionHand.MAIN_HAND) {
			if (tileEntity != null && !worldIn.isClientSide&&(player.getAbilities().instabuild||!tileEntity.isInfinite))
				NetworkHooks.openGui((ServerPlayer) player, tileEntity, tileEntity.getBlockPos());
			return InteractionResult.SUCCESS;
		}
		return p;
	}

	@Override
	public boolean canPlaceLiquid(BlockGetter w, BlockPos p, BlockState s, Fluid f) {
		StewPotTileEntity te = (StewPotTileEntity) w.getBlockEntity(p);
		return te.canAddFluid(new FluidStack(f, 1000));
	}

	@Override
	public boolean placeLiquid(LevelAccessor w, BlockPos p, BlockState s, FluidState f) {
		StewPotTileEntity te = (StewPotTileEntity) w.getBlockEntity(p);
		if (te.tryAddFluid(new FluidStack(f.getType(), 1000))) {
			return true;
		}
		return false;
	}

	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof StewPotTileEntity pot) {
			if (pot.proctype == 2 && pot.working) {

				int count = 2;
				while (--count != 0)
					worldIn.addParticle(Particles.STEAM.get(), pos.getX() + rand.nextFloat(), pos.getY() + 1,pos.getZ() + rand.nextFloat(), 0.0D,
							0.0D, 0.0D);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);
		if (blockEntity instanceof StewPotTileEntity pot && state.getBlock() != newState.getBlock()) {
			if (pot.proctype != 2)
				for (int i = 0; i < 9; i++) {
					ItemStack is = pot.getInv().getStackInSlot(i);
					if (!is.isEmpty())
						super.popResource(worldIn, pos, is);
				}
			for (int i = 9; i < 12; i++) {
				ItemStack is = pot.getInv().getStackInSlot(i);
				if (!is.isEmpty())
					super.popResource(worldIn, pos, is);
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void createBlockStateDefinition(
			net.minecraft.world.level.block.state.StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getAxis());

	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos) {
		StewPotTileEntity te = (StewPotTileEntity) pLevel.getBlockEntity(pPos);
		if (te.proctype == 0) {
			int ret = 1;
			for (int i = 0; i < 9; i++) {
				ItemStack is = te.getInv().getStackInSlot(i);
				if (!is.isEmpty())
					ret++;

			}
			ret += te.getTank().getFluidAmount() / 250;
			return ret;
		}
		return 0;
	}
}
