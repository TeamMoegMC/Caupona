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

package com.teammoeg.caupona.blocks.dolium;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.items.StewItem;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.network.NetworkHooks;

public class CounterDoliumBlock extends CPHorizontalEntityBlock<CounterDoliumBlockEntity> {

	public CounterDoliumBlock(Properties p) {
		super(CPBlockEntityTypes.DOLIUM, p);
		CPBlocks.dolium.add(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return true;
	}

	static final VoxelShape shape = Shapes.or(Shapes.or(Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 4, 0, 4, 16, 16)),
			Shapes.or(Block.box(0, 4, 0, 16, 16, 4),
					Shapes.or(Block.box(12, 4, 0, 16, 16, 16), Block.box(0, 4, 12, 16, 16, 16))));

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return shape;
	}

	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
		return Shapes.empty();
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
			BlockHitResult hit) {
		@SuppressWarnings("deprecation")
		InteractionResult p = super.use(state, worldIn, pos, player, handIn, hit);
		if (p.consumesAction())
			return p;
		if(worldIn.getBlockEntity(pos) instanceof CounterDoliumBlockEntity dolium) {
			ItemStack held = player.getItemInHand(handIn);
			if (held.isEmpty() && player.isShiftKeyDown()) {
				dolium.tank.setFluid(FluidStack.EMPTY);
				return InteractionResult.SUCCESS;
			}
			if (held.getItem() instanceof StewItem) {
				if (dolium.tryAddFluid(BowlContainingRecipe.extractFluid(held))) {
					ItemStack ret = held.getContainerItem();
					held.shrink(1);
					if (!player.addItem(ret))
						player.drop(ret, false);
				}
	
				return InteractionResult.sidedSuccess(worldIn.isClientSide);
			}
			if (FluidUtil.interactWithFluidHandler(player, handIn, dolium.tank))
				return InteractionResult.SUCCESS;
	
			if (handIn == InteractionHand.MAIN_HAND) {
				if (!worldIn.isClientSide&&(player.getAbilities().instabuild||!dolium.isInfinite))
					NetworkHooks.openGui((ServerPlayer) player, dolium, dolium.getBlockPos());
				return InteractionResult.SUCCESS;
			}
		}
		return p;
	}


	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock() && worldIn.getBlockEntity(pos) instanceof CounterDoliumBlockEntity dolium) {
			for (int i = 0; i < 6; i++) {
				ItemStack is = dolium.inv.getStackInSlot(i);
				if (!is.isEmpty())
					super.popResource(worldIn, pos, is);
			}
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

}
