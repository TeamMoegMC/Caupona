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

package com.teammoeg.caupona.blocks.pot;

import java.util.List;

import org.jspecify.annotations.Nullable;

import com.teammoeg.caupona.blocks.CPRegisteredEntityBlock;
import com.teammoeg.caupona.client.CPParticles;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class StewPot extends CPRegisteredEntityBlock<StewPotBlockEntity> {
	public static final EnumProperty<Axis> FACING = BlockStateProperties.HORIZONTAL_AXIS;

	public StewPot(Properties blockProps, DeferredHolder<BlockEntityType<?>,BlockEntityType<StewPotBlockEntity>> ste) {
		super(blockProps, ste);
	}

	static final VoxelShape shape = Block.box(1, 0, 1, 15, 12, 15);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	@Override
	public InteractionResult useItemOn(ItemStack held,BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		InteractionResult p = super.useItemOn(held,state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) worldIn.getBlockEntity(pos);
		if (blockEntity.canAddFluid()) {
			if (held.isEmpty() && player.isShiftKeyDown()) {
				try(Transaction trans=Transaction.openRoot()){
					blockEntity.getTank().extract(blockEntity.getTank().getResource(0), 1250,trans);
					blockEntity.syncData();
				}
				return InteractionResult.SUCCESS;
			}

			try(Transaction trans=Transaction.openRoot()){
				@Nullable ResourceHandler<FluidResource> cap=held.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forPlayerInteraction(player, handIn));
				if(cap!=null) {
					FluidResource fr=cap.getResource(0);
					int amt=cap.extract(fr, cap.getAmountAsInt(0), trans);
					if (blockEntity.tryAddFluid(fr,amt,trans)) {
						trans.commit();
						return InteractionResult.SUCCESS;
					}
				}
			}
			
			if (FluidUtil.interactWithFluidHandler(player, handIn,pos, blockEntity.getTank()))
				return InteractionResult.SUCCESS;

		}
		return p;
	}


	@Override
	public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
		if (worldIn.getBlockEntity(pos) instanceof StewPotBlockEntity pot) {
			if (pot.proctype == 2 && pot.working) {

				int count = 2;
				while (--count != 0)
					worldIn.addParticle(CPParticles.STEAM.get(), pos.getX() + rand.nextFloat(), pos.getY() + 1,pos.getZ() + rand.nextFloat(), 0.0D,
							0.0D, 0.0D);
			}
		}
	}


	@Override
	protected List<ItemStack> getDrops(BlockState p_state, Builder p_params) {
		List<ItemStack> list=super.getDrops(p_state, p_params);
		if (p_params.getParameter(LootContextParams.BLOCK_ENTITY) instanceof StewPotBlockEntity pot) {
			if (pot.proctype != 2)
				for (int i = 0; i < 9; i++) {
					ItemResource is = pot.getInternInv().getResource(i);
					if (!is.isEmpty())
						list.add(is.toStack(pot.getInternInv().getAmountAsInt(i)));
				}
			for (int i = 9; i < 12; i++) {
				ItemResource is = pot.getInternInv().getResource(i);
				if (!is.isEmpty())
					list.add(is.toStack(pot.getInternInv().getAmountAsInt(i)));
			}
		}
		return list;
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
	public int getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, Direction dir) {
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) pLevel.getBlockEntity(pPos);
		if (blockEntity.proctype == 0) {
			int ret = 1;
			for (int i = 0; i < 9; i++) {
				ItemResource is = blockEntity.getInv().getResource(i);
				if (!is.isEmpty())
					ret++;

			}
			ret += blockEntity.getTank().getAmountAsInt(0) / 250;
			return ret;
		}
		return 0;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		InteractionResult p= super.useWithoutItem(state, level, pos, player, hitResult);
		if (p.consumesAction())
			return p;
		StewPotBlockEntity blockEntity = (StewPotBlockEntity) level.getBlockEntity(pos);
		if (blockEntity != null && !level.isClientSide()&&(player.getAbilities().instabuild||!blockEntity.isInfinite)) {
			((ServerPlayer) player).openMenu( blockEntity, blockEntity.getBlockPos());
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
		return false;
	}


}
