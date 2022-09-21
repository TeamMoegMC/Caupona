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

package com.teammoeg.caupona.blocks.hypocaust;

import java.util.HashSet;
import java.util.Set;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FireboxTile extends BathHeatingTile {
	TagKey<Block> HEAT_CONDUCTOR = BlockTags.create(new ResourceLocation(Main.MODID, "heat_conductor"));
	int process;
	int heat;
	private int r;
	private int mp;

	public FireboxTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.FIREBOX.get(), pWorldPosition, pBlockState);
		r = Config.SERVER.bathRange.get();
		mp = Config.SERVER.bathPath.get() / 2;
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		super.readCustomNBT(nbt, isClient);
		process = nbt.getInt("heatProcess");
		heat = nbt.getInt("heatSpeed");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		super.writeCustomNBT(nbt, isClient);
		nbt.putInt("heatProcess", process);
		nbt.putInt("heatSpeed", heat);
	}

	private boolean dist(BlockPos crn, BlockPos orig) {
		return Mth.abs(crn.getX() - orig.getX()) <= r && Mth.abs(crn.getZ() - orig.getZ()) <= r;
	}

	public void findNext(Level l, BlockPos crn, BlockPos orig, Set<BlockPos> pos) {
		if (dist(crn, orig)) {
			if (pos.add(crn)) {
				for (Direction dir : Utils.horizontals) {
					BlockPos act = crn.relative(dir);
					if (l.isLoaded(act) && l.getBlockState(act).is(HEAT_CONDUCTOR)) {
						findNext(l, act, orig, pos);
					}
				}
			}
		}
	}

	@SuppressWarnings("resource")
	public Set<BlockPos> getAll() {
		Set<BlockPos> poss = new HashSet<>();
		findNext(this.getLevel(), this.getBlockPos(), this.getBlockPos(), poss);
		return poss;
	}

	@Override
	public void tick() {
		if (this.level.isClientSide)
			return;
		BlockEntity te = level.getBlockEntity(worldPosition.below());
		if (te instanceof IStove stove) {
			int nh = stove.requestHeat();
			if (heat != nh) {
				process = 0;
				heat = nh;
			}
		} else if (heat != 0) {
			process = 0;
			heat = 0;
		}
		if (process > 0) {
			process--;
		} else {
			process = mp;
			Set<BlockPos> pss = getAll();
			for (BlockPos pos : pss) {
				BlockEntity hte = level.getBlockEntity(pos);
				if (hte instanceof BathHeatingTile bath)
					bath.setHeat(heat);
			}
		}
		this.setChanged();
		super.tick();

	}

	@Override
	public int getHeat() {
		return Math.max(super.heat, heat);
	}
}
