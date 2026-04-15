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

package com.teammoeg.caupona.container;

import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.CPTags.Items;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.util.INetworkContainer;
import com.teammoeg.caupona.util.ITickableContainer;
import com.teammoeg.caupona.util.RecipeHandleStatus;
import com.teammoeg.caupona.util.RecipeHandler;
import com.teammoeg.caupona.util.SerializeUtil;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class PortableBrazierContainer extends AbstractContainerMenu implements INetworkContainer, ITickableContainer {
	public static final int INGREDIENT = 0;
	private static final int CONTAINER = 1;
	private static final int FUEL = 2;
	private static final int OUT = 3;
	private final Player player;
	ItemStacksResourceHandler items = new ItemStacksResourceHandler(4) {
		@Override
		public boolean isValid(int slot, ItemResource stack) {
			if (slot == INGREDIENT)
				return AspicMeltingRecipe.find(stack) != null;
			if (slot == FUEL)
				return stack.is(Items.PORTABLE_BRAZIER_FUEL_TYPE);
			return true;
		}

		@Override
		public int getCapacity(int slot, ItemResource stack) {
			return 1;
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			if(index != OUT)
				handler.onContainerChanged();
		}
	};
	public RecipeHandler<AspicMeltingRecipe> handler=new RecipeHandler<>(t->{
		ItemResource aspicItem=items.getResource(INGREDIENT);
		ItemResource container=items.getResource(CONTAINER);
		ItemResource fuel=items.getResource(FUEL);
		if(!container.isEmpty()&&!aspicItem.isEmpty()&&!fuel.isEmpty()) {
			RecipeHolder<AspicMeltingRecipe> recipe = AspicMeltingRecipe.find(aspicItem);
			if (recipe != null&&recipe.id().identifier().equals(t)) {
				try(Transaction trans=Transaction.openRoot()){
					int aspicCount=items.extract(INGREDIENT, aspicItem, 1, trans);
					int containerCount=items.extract(CONTAINER, container, 1, trans);
					int fuelCount=items.extract(FUEL, fuel, 1, trans);
					if(aspicCount>0&&containerCount>0&&fuelCount>0) {
						FluidStack fluid=recipe.value().handle(aspicItem.toStack(aspicCount));
						ItemResource rs=Utils.contain(container, FluidResource.of(fluid), fluid.amount()).getOutput();
						int filled=items.insert(OUT, rs, 1, trans);
						if(filled>0) {
							trans.commit();
							return RecipeHandleStatus.SUCCEED;
						}else {
							return RecipeHandleStatus.BLOCKED;
						}
					}
					
				}
			}
		}
		return RecipeHandleStatus.FAILED;
	});

	/**
	 * @param data  
	 */
	public PortableBrazierContainer(int id, Inventory playerInventory, FriendlyByteBuf data) {
		this(id, playerInventory);
	}

	public PortableBrazierContainer(int id, Inventory playerInventory) {
		super(CPGui.BRAZIER.get(), id);
		this.player = playerInventory.player;
		this.addSlot(new ResourceHandlerSlot(items,items::set, INGREDIENT, 44, 11));
		this.addSlot(new ResourceHandlerSlot(items,items::set, CONTAINER, 74, 11));
		this.addSlot(new ResourceHandlerSlot(items,items::set, FUEL, 74, 44));
		addSlot(new OutputSlot(items,items::set, OUT, 104, 11));
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 83 + i * 18));
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(playerInventory, i, 8 + i * 18, 141));
		this.addDataSlots(handler);
	}

	/**
	 * Called when the container is closed.
	 */
	public void removed(Player pPlayer) {
		super.removed(pPlayer);

		if (!pPlayer.isAlive() || pPlayer instanceof ServerPlayer && ((ServerPlayer) pPlayer).hasDisconnected()) {
			for (int j = 0; j < items.size(); ++j) {
				pPlayer.drop(items.getResource(j).toStack(items.getAmountAsInt(j)), false);
			}
		} else {
			Inventory inventory = pPlayer.getInventory();
			if (inventory.player instanceof ServerPlayer) {
				for (int i = 0; i < items.size(); ++i) {
					inventory.placeItemBackInInventory(items.getResource(i).toStack(items.getAmountAsInt(i)));
				}
			}
		}

	}
	public ContainerData getData() {
		return handler;
	}
	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player
	 * inventory and the other inventory(s).
	 */
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotStack = slot.getItem();
			itemStack = slotStack.copy();
			if (index == OUT) {
				if (!this.moveItemStackTo(slotStack, 4, 40, true)) {
					return ItemStack.EMPTY;
				}
				slot.onQuickCraft(slotStack, itemStack);
			} else if (index > OUT) {
				if (!this.moveItemStackTo(slotStack, 0, 3, false))
					if (index < 31) {
						if (!this.moveItemStackTo(slotStack, 31, 40, false))
							return ItemStack.EMPTY;
					} else if (index < 40 && !this.moveItemStackTo(slotStack, 6, 31, false))
						return ItemStack.EMPTY;
			} else if (!this.moveItemStackTo(slotStack, 6, 40, false)) {
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
	private RecipeHolder<AspicMeltingRecipe> testRecipe() {
		if(items.getAmountAsInt(CONTAINER)>0&&items.getAmountAsInt(INGREDIENT)>0&&items.getAmountAsInt(FUEL)>0) {
			
			ItemResource aspicItem=items.getResource(INGREDIENT);
			if(!aspicItem.isEmpty()) {
				RecipeHolder<AspicMeltingRecipe> recipe=AspicMeltingRecipe.find(aspicItem);
				ItemResource container=items.getResource(CONTAINER);
				if(recipe!=null) {
					FluidStack fluid=recipe.value().handle(aspicItem.toStack(items.getAmountAsInt(INGREDIENT)));
					ItemResource rs=Utils.contain(container, FluidResource.of(fluid), fluid.amount()).getOutput();
					if(!rs.isEmpty())
						return recipe;
				}
			}
		}
		return null;
	}
	@Override
	public void tick(boolean isServer) {
		if (isServer) {
			if(handler.shouldTestRecipe()) {
				handler.setRecipe(testRecipe());
			}
			if (handler.tickProcess(1)) {
			}
		}
	}

	@Override
	public ServerPlayer getOpenedPlayer() {
		return (ServerPlayer) player;
	}

	@Override
	public void handle(CompoundTag nbt) {
		try(ProblemReporter.ScopedCollector rp=SerializeUtil.reporter()){
			handler.readCustomNBT(TagValueInput.create(rp, this.player.registryAccess(), nbt), true);
		}
	}
}