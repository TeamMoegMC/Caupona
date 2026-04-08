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

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.item.StewItem;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class BowlBlockEntity extends CPBaseBlockEntity implements IInfinitable,IFoodContainer {
	private ItemStack internal=ItemStack.EMPTY;
	public ItemStack getInternal() {
		return internal;
	}

	boolean isInfinite = false;

	public BowlBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.BOWL.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
	
		internal = nbt.read("bowl", ItemStack.CODEC).orElse(ItemStack.EMPTY);
		isInfinite = nbt.getBooleanOr("inf",false);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		if(!internal.isEmpty())
			nbt.store("bowl", ItemStack.CODEC, internal);
		nbt.putBoolean("inf", isInfinite);
	}

	@Override
	public void tick() {
	}

	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}

	@Override
	public ItemStack getInternal(int num) {
		return internal;
	}

	@Override
	public void setInternal(int num, ItemStack is) {
		if(!isInfinite) {
			internal=is;
			this.syncData();
		}
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public boolean accepts(int num, ItemStack is) {
		return is.getItem() instanceof StewItem||is.is(Items.BOWL);
	}

	public void setInternal(ItemStack internal) {
		this.internal = internal;
		this.syncData();
	}
	@Override
	public boolean isInfinite() {
		return isInfinite;
	}
}
