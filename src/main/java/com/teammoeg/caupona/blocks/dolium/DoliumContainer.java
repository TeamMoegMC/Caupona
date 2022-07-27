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

package com.teammoeg.caupona.blocks.dolium;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.container.OutputSlot;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class DoliumContainer extends AbstractContainerMenu {
	public final CounterDoliumTileEntity tile;

	public CounterDoliumTileEntity getTile() {
		return tile;
	}

	public DoliumContainer(int id, Inventory inv, FriendlyByteBuf buffer) {
		this(id, inv, (CounterDoliumTileEntity) inv.player.level.getBlockEntity(buffer.readBlockPos()));
	}

	public DoliumContainer(int id, Inventory inv, CounterDoliumTileEntity te) {
		super(CPGui.DOLIUM.get(), id);
		tile = te;
		this.addSlot(new SlotItemHandler(te.inv, 0, 153, 4));
		this.addSlot(new SlotItemHandler(te.inv, 1, 134, 8));
		this.addSlot(new SlotItemHandler(te.inv, 2, 115, 12));
		this.addSlot(new SlotItemHandler(te.inv, 3, 132, 35));
		this.addSlot(new SlotItemHandler(te.inv, 4, 132, 53));
		this.addSlot(new OutputSlot(te.inv, 5, 152, 51));
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
			if (index == 5) {
				if (!this.moveItemStackTo(slotStack, 6, 42, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotStack, itemStack);
			} else if (index >= 6) {
				if (!this.moveItemStackTo(slotStack, 3, 5, false))
					if (!this.moveItemStackTo(slotStack, 0, 3, false))
						if (index < 33) {
							if (!this.moveItemStackTo(slotStack, 33, 42, false))
								return ItemStack.EMPTY;
						} else if (index < 42 && !this.moveItemStackTo(slotStack, 6, 33, false))
							return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(slotStack, 6, 42, false)) {
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
