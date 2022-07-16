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

package com.teammoeg.caupona.container;

import java.util.function.Supplier;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.blocks.StewPotTileEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class StewPotContainer extends AbstractContainerMenu {
	public static class OutputSlot extends SlotItemHandler {
		public OutputSlot(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
	};

	public static class HidableSlot extends SlotItemHandler {
		Supplier<Boolean> vs;

		public HidableSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition,
				Supplier<Boolean> visible) {
			super(itemHandler, index, xPosition, yPosition);
			vs = visible;
		}

		@Override
		public boolean isActive() {
			return vs.get();
		}

		@Override
		public boolean mayPickup(Player playerIn) {
			return vs.get();
		}

	}

	StewPotTileEntity tile;

	public StewPotTileEntity getTile() {
		return tile;
	}

	public StewPotContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (StewPotTileEntity) inv.player.level.getBlockEntity(buffer.readBlockPos()));
	}

	public StewPotContainer(int id, Inventory inv, StewPotTileEntity te) {
		super(CPGui.STEWPOT.get(), id);
		tile = te;
		for (int i = 0; i < 9; i++)
			this.addSlot(new HidableSlot(te.getInv(), i, 45 + (i % 3) * 18, 17 + (i / 3) * 18, () -> te.proctype != 2));
		this.addSlot(new SlotItemHandler(te.getInv(), 9, 143, 17));
		this.addSlot(new OutputSlot(te.getInv(), 10, 143, 51));

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(inv, i, 8 + i * 18, 142));
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index == 10) {
				if (!this.moveItemStackTo(slotStack, 11, 47, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotStack, itemStack);
			} else if (index > 10) {
				if (!this.moveItemStackTo(slotStack, 9, 10, false))
					if (!this.moveItemStackTo(slotStack, 0, 9, false)) {
						if (index < 38)
							if (!this.moveItemStackTo(slotStack, 38, 47, false))
								return ItemStack.EMPTY;
							else if (index < 47 && !this.moveItemStackTo(slotStack, 11, 38, false))
								return ItemStack.EMPTY;
					}
			} else if (!this.moveItemStackTo(slotStack, 11, 47, false)) {
				return ItemStack.EMPTY;
			}
			if (slotStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			if (slotStack.getCount() == itemStack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTake(playerIn, slotStack);
		}
		return itemStack;
	}
}
