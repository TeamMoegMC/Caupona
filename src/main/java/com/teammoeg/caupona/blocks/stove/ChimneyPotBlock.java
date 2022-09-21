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

package com.teammoeg.caupona.blocks.stove;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.blocks.CPHorizontalEntityBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

public class ChimneyPotBlock extends CPHorizontalEntityBlock<ChimneyPotTileEntity> {

	public ChimneyPotBlock(Properties p_54120_) {
		super(CPTileTypes.CHIMNEY, p_54120_);
		CPBlocks.chimney.add(this);
	}

	static final VoxelShape shape = Block.box(3, 0, 3, 13, 16, 13);

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
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		if (tileEntity instanceof ChimneyPotTileEntity chimneyPot
				&& player.getItemInHand(handIn).getItem() instanceof ShovelItem) {
			if (chimneyPot.countSoot > 0) {
				if (!worldIn.isClientSide) {
					player.getItemInHand(handIn).hurtAndBreak(1, player, t -> t.broadcastBreakEvent(handIn));
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(CPItems.soot, chimneyPot.countSoot));
					chimneyPot.countSoot = 0;
				}
				return InteractionResult.sidedSuccess(worldIn.isClientSide);
			}
		}
		return p;
	}
}
