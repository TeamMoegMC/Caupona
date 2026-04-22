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
import com.teammoeg.caupona.data.recipes.SauteedRecipe;
import com.teammoeg.caupona.item.DishItem;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class DishBlockEntity extends CPBaseBlockEntity implements IInfinitable,IFoodContainer {
	private ItemStacksResourceHandler internal=new ItemStacksResourceHandler(1) {

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			syncData();
			super.onContentsChanged(index, previousContents);
		}
		
		
	};
	boolean isInfinite = false;

	public DishBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.DISH.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
	}
	
	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
	
		internal.deserialize(nbt.childOrEmpty("bowl"));
		isInfinite = nbt.getBooleanOr("inf",false);
	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		internal.serialize(nbt.child("bowl"));
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
	public int getSlots() {
		return 1;
	}

	@Override
	public boolean accepts(int num, ItemResource is) {
		return is.getItem() instanceof DishItem||SauteedRecipe.isBowl(is.toStack());
	}

	@Override
	public boolean isInfinite() {
		return isInfinite;
	}
	@Override
	public ItemResource exchangeInternal(int num, ItemResource is,TransactionContext parent) {
		ItemResource ir=internal.getResource(0);
		try(Transaction trans=Transaction.open(parent)){
			int inserted=1;
			int extracted=internal.extract(0, ir, 1, trans);
			if(!is.isEmpty()) {
				inserted=internal.insert(0,is,1,trans);
			}
			if(inserted==1) {
				trans.commit();
				if(extracted>0) {
					return ir;
				}
				return ItemResource.EMPTY;
			}
		}
		return is;
	}
	public ItemStacksResourceHandler getInternal() {
		return internal;
	}
}
