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

package com.teammoeg.caupona.blocks.dolium;

import java.util.List;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;

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
	@Override
	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
		return Shapes.empty();
	}
	@Override
	public InteractionResult useItemOn(ItemStack held,BlockState state, Level worldIn, BlockPos pos, Player player,InteractionHand hand,
			BlockHitResult hit) {
		InteractionResult p = super.useItemOn(held,state, worldIn, pos, player, hand, hit);
		if (p.consumesAction())
			return p;
		if(worldIn.getBlockEntity(pos) instanceof CounterDoliumBlockEntity dolium) {
			if (held.isEmpty() && player.isShiftKeyDown()) {
				dolium.tank.set(0,FluidResource.EMPTY,0);
				return InteractionResult.SUCCESS;
			}
			if (FluidUtil.interactWithFluidHandler(player, hand, pos, dolium.modtank))
				return InteractionResult.SUCCESS;
		}
		return p;
	}
	@Override
	public InteractionResult useWithoutItem(BlockState state, Level worldIn, BlockPos pos, Player player,
			BlockHitResult hit) {
		InteractionResult p = super.useWithoutItem(state, worldIn, pos, player, hit);
		if (p.consumesAction())
			return p;
		if(worldIn.getBlockEntity(pos) instanceof CounterDoliumBlockEntity dolium) {
				if (!worldIn.isClientSide()&&(player.getAbilities().instabuild||!dolium.isInfinite))
					((ServerPlayer) player).openMenu(dolium, dolium.getBlockPos());
				
		}
		return InteractionResult.SUCCESS;
	}


	@Override
	protected List<ItemStack> getDrops(BlockState p_state, Builder p_params) {
		List<ItemStack> list=super.getDrops(p_state, p_params);
		if (p_params.getParameter(LootContextParams.BLOCK_ENTITY) instanceof CounterDoliumBlockEntity dolium) {
			for (int i = 0; i < 6; i++) {
				ItemResource is = dolium.inv.getResource(i);
				if (!is.isEmpty()) {
					list.add(is.toStack(dolium.inv.getAmountAsInt(i)));
				}
			}
		}
		return list;
	}


	@Override
	public boolean isPathfindable(BlockState pState, PathComputationType pType) {
	      return false;
	}
}
