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

package com.teammoeg.caupona.blocks.pan;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.FryingRecipe;
import com.teammoeg.caupona.data.recipes.PanPendingContext;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.items.DishItem;
import com.teammoeg.caupona.network.CPBaseTile;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class PanTileEntity extends CPBaseTile implements MenuProvider,IInfinitable {
	public ItemStackHandler inv = new ItemStackHandler(12) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 9)
				return FryingRecipe.isCookable(stack);
			if (slot == 9) {
				return stack.is(Items.BOWL);
			}
			if (slot == 11)
				return SpiceRecipe.isValid(stack);
			return false;
		}

		@Override
		public int getSlotLimit(int slot) {
			if (slot < 9)
				return 1;
			return super.getSlotLimit(slot);
		}
	};
	public int process;
	public int processMax;
	public Item preout = Items.AIR;
	public ItemStack sout = ItemStack.EMPTY;
	public int oamount;
	public boolean working = false;
	public boolean operate = false;
	public boolean rsstate = false;
	public SauteedFoodInfo current;
	boolean isInfinite = false;
	public PanTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.PAN.get(), pWorldPosition, pBlockState);
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0)
			this.operate = true;
		if (type == 1) {
			if (data == 1)
				rsstate = false;
			else if (data == 2)
				rsstate = true;
		}

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		working = nbt.getBoolean("working");
		operate = nbt.getBoolean("operate");
		rsstate = nbt.getBoolean("rsstate");
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		
		if (nbt.contains("sout"))
			sout = ItemStack.of(nbt.getCompound("sout"));
		else
			sout = ItemStack.EMPTY;
		inv.deserializeNBT(nbt.getCompound("items"));
		if (!isClient) {
			isInfinite =nbt.getBoolean("inf");
			oamount = nbt.getInt("amount");
			if (nbt.contains("cur"))
				current = new SauteedFoodInfo(nbt.getCompound("cur"));
			else
				current = null;

		}
		preout = ForgeRegistries.ITEMS.getValue(new ResourceLocation(nbt.getString("out")));

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putBoolean("working", working);
		nbt.putBoolean("operate", operate);
		nbt.putBoolean("rsstate", rsstate);
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.put("sout", sout.serializeNBT());
		nbt.put("items", inv.serializeNBT());
		if (!isClient) {
			nbt.putInt("amount", oamount);
			nbt.putBoolean("inf",isInfinite);
			if (current != null)
				nbt.put("cur", current.save());

		}
		nbt.putString("out", preout.getRegistryName().toString());

	}

	private ItemStack tryAddSpice(ItemStack fs) {
		ItemStack spi = inv.getStackInSlot(11);
		SpiceRecipe spice = SpiceRecipe.find(spi);
		if (spice != null && SpiceRecipe.getMaxUse(spi) >= fs.getCount()) {
			SauteedFoodInfo si = DishItem.getInfo(fs);
			if (!isInfinite) 
				inv.setStackInSlot(11, SpiceRecipe.handle(spi, fs.getCount()));
			si.addSpice(spice.effect, spi);
			DishItem.setInfo(fs, si);
		}
		return fs;
	}

	@Override
	public void tick() {
		if (!level.isClientSide) {
			working = false;
			if (processMax > 0) {
				BlockEntity te = level.getBlockEntity(worldPosition.below());
				if (te instanceof IStove) {
					int rh = ((IStove) te).requestHeat();
					process += rh;
					if (rh > 0) {
						working = true;
						this.syncData();
					}
					if (process >= processMax) {
						process = 0;
						processMax = 0;
						doWork();
						this.setChanged();
					}
				} else
					return;

			} else if (!sout.isEmpty()) {
				operate = false;
				if (inv.getStackInSlot(10).isEmpty()) {
					if(!isInfinite)
						inv.setStackInSlot(10, tryAddSpice(sout.split(1)));
					else
						inv.setStackInSlot(10, tryAddSpice(ItemHandlerHelper.copyStackWithSize(sout, 1)));
					
					if(sout.isEmpty())
						this.syncData();
					else
						this.setChanged();
				}
			} else {
				prepareWork();
			}
		}
		
	}

	private void prepareWork() {
		if (rsstate && !operate && level.hasNeighborSignal(this.worldPosition))
			operate = true;

		if (operate) {
			operate = false;
			BlockEntity te = level.getBlockEntity(worldPosition.below());
			if (!(te instanceof IStove) || !((IStove) te).canEmitHeat())
				return;
			make();
		}
	}

	private void doWork() {
		ItemStack is = new ItemStack(preout, oamount);
		DishItem.setInfo(is, current);
		current = null;
		oamount = 0;
		preout = Items.AIR;
		sout = is;
	}

	@SuppressWarnings("resource")
	private void make() {
		int itms = 0;
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {
				if (FryingRecipe.isCookable(is))
					itms++;
				else
					return;
			}
		}
		if (itms <= 0)
			return;
		int cook = Mth.ceil(itms / 2f);
		if (inv.getStackInSlot(9).getCount() < cook)
			return;
		boolean has_oil = false;
		for (Direction d : Utils.horizontals) {
			BlockPos bp = this.getBlockPos().relative(d);
			BlockState bs = this.getLevel().getBlockState(bp);
			if (bs.is(CPBlocks.GRAVY_BOAT)) {
				int oil = GravyBoatBlock.getOil(bs);
				if (oil > 0) {
					GravyBoatBlock.drawOil(getLevel(), bp, bs, 1);
					has_oil = true;
					break;
				}
			}
		}
		if (!has_oil)
			return;
		inv.getStackInSlot(9).shrink(cook);
		processMax = process = 0;
		NonNullList<ItemStack> interninv = NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {
				for (int j = 0; j < 9; j++) {
					ItemStack ois = interninv.get(j);
					if (ois.isEmpty()) {
						interninv.set(j, is.copy());
						break;
					} else if (ois.sameItem(is) && ItemStack.tagMatches(ois, is)) {
						ois.setCount(ois.getCount() + is.getCount());
						break;
					}
				}
				inv.setStackInSlot(i, is.getContainerItem());
			}
		}
		int tpt = 0;
		current = new SauteedFoodInfo();
		for (int i = 0; i < 9; i++) {
			ItemStack is = interninv.get(i);
			if (is.isEmpty())
				break;
			current.addItem(is, cook);
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(is.getItem());
			if (fvr != null)
				tpt += fvr.processtimes.getOrDefault(is.getItem(), 0);
		}

		current.completeAll();
		current.recalculateHAS();
		tpt = Math.max(Config.SERVER.fryTimeBase.get(), tpt);
		interninv.clear();
		PanPendingContext ctx = new PanPendingContext(current);
		oamount = cook;

		for (FryingRecipe cr : FryingRecipe.sorted) {
			if (cr.matches(ctx)) {
				processMax = Math.max(cr.time, tpt);
				preout = cr.output;
				return;
			}
		}
		preout = CPItems.ddish;
		if (this.getBlockState().is(CPBlocks.STONE_PAN))
			tpt *= 2;
		processMax = tpt;
		this.syncData();
		return;
	}

	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
		return new PanContainer(pContainerId, pInventory, this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("container." + Main.MODID + ".pan.title");
	}

	RangedWrapper bowl = new RangedWrapper(inv, 9, 12) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot == 10)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == 9 || slot == 11)
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}
	};
	RangedWrapper ingredient = new RangedWrapper(inv, 0, 10) {

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};
	LazyOptional<IItemHandler> up = LazyOptional.of(() -> ingredient);
	LazyOptional<IItemHandler> side = LazyOptional.of(() -> bowl);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.UP)
				return up.cast();
			return this.side.cast();
		}
		return super.getCapability(cap, side);
	}

	public ItemStackHandler getInv() {
		return inv;
	}

	@Override
	public boolean setInfinity() {
		return isInfinite=!isInfinite;
	}
}
