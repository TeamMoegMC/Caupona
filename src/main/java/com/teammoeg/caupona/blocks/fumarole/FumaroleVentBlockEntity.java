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

package com.teammoeg.caupona.blocks.fumarole;

import java.util.Iterator;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;

public class FumaroleVentBlockEntity extends CPBaseBlockEntity implements IStove {
	private final int heat;
	private final int checkmax;
	private final int updatemax;
	public static final TagKey<Fluid> pumice = FluidTags
			.create(new ResourceLocation(CPMain.MODID, "pumice_bloom_grow_on"));
	public static final TagKey<Block> hot = BlockTags.create(new ResourceLocation(CPMain.MODID, "fumarole_hot"));
	public static final TagKey<Block> vhot = BlockTags.create(new ResourceLocation(CPMain.MODID, "fumarole_very_hot"));

	public FumaroleVentBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.FUMAROLE.get(), pWorldPosition, pBlockState);
		heat = CPConfig.SERVER.fumarolePower.get();
		checkmax = CPConfig.SERVER.fumaroleCheck.get();
		updatemax = CPConfig.SERVER.fumaroleSpeed.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		if (isClient)
			return;
		update = nbt.getInt("update");
		check = nbt.getInt("check");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		if (isClient)
			return;
		nbt.putInt("update", update);
		nbt.putInt("check", check);
	}

	int update;
	int check;

	@SuppressWarnings("resource")
	@Override
	public void tick() {
		BlockState bs = this.getBlockState();
		if (bs.getValue(FumaroleVentBlock.HEAT) == 2) {
			if (update < updatemax) {
				update++;
			} else {
				update = 0;
				if (!this.getBlockState().getValue(FumaroleVentBlock.WATERLOGGED))
					placeFumarole(this.getLevel(), this.getBlockPos());
			}
			this.setChanged();
		}
		if (check < checkmax) {
			check++;
			
		} else {
			check = 0;
			BlockState below = this.getLevel().getBlockState(this.getBlockPos().below(2));
			int cheat = bs.getValue(FumaroleVentBlock.HEAT);
			if (below.is(vhot)) {
				if (cheat != 2) {
					this.getLevel().setBlockAndUpdate(getBlockPos(), bs.setValue(FumaroleVentBlock.HEAT, 2));
				}
			} else if (below.is(hot)) {
				if (cheat != 1) {
					this.getLevel().setBlockAndUpdate(getBlockPos(), bs.setValue(FumaroleVentBlock.HEAT, 1));
				}
			} else if (cheat != 0) {
				this.getLevel().setBlockAndUpdate(getBlockPos(), bs.setValue(FumaroleVentBlock.HEAT, 0));
			}
		}
		
	}

	public static void placeFumarole(Level pLevel, BlockPos pPos) {
		RandomSource pRandom = pLevel.getRandom();
		int dx = (pRandom.nextBoolean() ? 1 : -1) * (pRandom.nextInt(6));
		int dz = (pRandom.nextBoolean() ? 1 : -1) * (pRandom.nextInt(6));
		if (dx == 0 && dz == 0)
			return;

		BlockPos pendPos = pPos.offset(dx, 0, dz);
		for (int i = 0; i < 3; i++) {
			BlockState b0 = pLevel.getBlockState(pendPos);
			BlockState b1 = pLevel.getBlockState(pendPos.below());
			if (b0.isAir()) {
				if (b1.getFluidState().is(pumice)) {
					if (shouldPlacePumice(pLevel, pendPos))
						pLevel.setBlockAndUpdate(pendPos, CPBlocks.PUMICE_BLOOM.get().defaultBlockState());
					return;
				}
			}
			pendPos = pendPos.below();
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean shouldPlacePumice(Level pLevel, BlockPos pPos) {
		if (!pLevel.isAreaLoaded(pPos, 1))
			return false;
		int cnt = 0;
		AABB aabb = new AABB(pPos.offset(-1, 0, -1), pPos.offset(1, 0, 1));
		Iterator<BlockState> it = pLevel.getBlockStates(aabb).iterator();
		while (it.hasNext()) {
			if (it.next().getBlock() == CPBlocks.PUMICE_BLOOM.get())
				cnt++;
			if (cnt >= 2)
				return false;
		}
		return true;
	}

	@Override
	public int requestHeat() {
		if (this.getBlockState().getValue(FumaroleVentBlock.WATERLOGGED))
			return 0;
		return heat;
	}

	@Override
	public boolean canEmitHeat() {
		if (heat == 0)
			return false;
		if (this.getBlockState().getValue(FumaroleVentBlock.WATERLOGGED))
			return false;
		return this.getBlockState().getValue(FumaroleVentBlock.HEAT) != 0;
	}

}
