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

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.api.events.ContanerContainFoodEvent;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.LazyTickWorker;
import com.teammoeg.caupona.util.RecipeHandleStatus;
import com.teammoeg.caupona.util.RecipeHandler;
import com.teammoeg.caupona.util.SpiceAddedResourceHandler;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class CounterDoliumBlockEntity extends CPBaseBlockEntity implements MenuProvider, IInfinitable {
	ItemStacksResourceHandler inv = new ItemStacksResourceHandler(6) {
		@Override
		public boolean isValid(int slot, ItemResource stack) {
			if (slot < 3)
				return DoliumRecipe.testInput(stack.toStack());
			if (slot == 3) {
				return SpiceRecipe.isValid(stack.toStack());
			}
			return true;
		}

		@Override
		protected void onContentsChanged(int slot, ItemStack stack) {
			if(slot<5&&slot!=3)
				recipeHandler.onContainerChanged();
			setChanged();
			super.onContentsChanged(slot, stack);
		}
		
	};
	public final FluidStacksResourceHandler tank = new FluidStacksResourceHandler(1,1250) {

		@Override
		public boolean isValid(int index, FluidResource resource) {
			return !resource.getFluid().getFluidType().isLighterThanAir();
		}

		@Override
		protected void onContentsChanged(int slot,FluidStack stackBefore) {
			if (isInfinite) {
				this.set(slot, FluidResource.of(stackBefore), stackBefore.amount());
				return;
			}
			super.onContentsChanged(slot,stackBefore);
			recipeHandler.onContainerChanged();
			recipeHandler.resetProgress();
			syncData();
		}

	};
	public final SpiceAddedResourceHandler modtank=new SpiceAddedResourceHandler(tank,inv,3);
	public LazyTickWorker contain;
	boolean isInfinite = false;
	public final RecipeHandler<DoliumRecipe> recipeHandler=new RecipeHandler<>(t->{
		RecipeHolder<DoliumRecipe> recipe = DoliumRecipe.testDolium(tank, inv);
		if(recipe!=null&&recipe.id().identifier().equals(t)) {
			return recipe.value().handleDolium(tank, inv);
		}
		return RecipeHandleStatus.FAILED;
	});
	public CounterDoliumBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPBlockEntityTypes.DOLIUM.get(), pWorldPosition, pBlockState);
		contain = new LazyTickWorker(CPConfig.SERVER.containerTick.get(),()->{
			
			if(tryContianFluid())
				return true;
			
			return false;
		});
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		recipeHandler.readCustomNBT(nbt, isClient);
		tank.deserialize(nbt.childOrEmpty("tank"));
		isInfinite = nbt.getBooleanOr("inf",false);
		if (!isClient) {
			inv.deserialize(nbt.childOrEmpty("inventory"));
			
		}

	}

	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		recipeHandler.writeCustomNBT(nbt, isClient);
		tank.serialize(nbt.child("tank"));
		nbt.putBoolean("inf", isInfinite);
		if (!isClient) {
			inv.serialize(nbt.child("inventory"));
		}

	}

	@Override
	public void tick() {
		if (this.level.isClientSide())
			return;
		boolean updateNeeded = contain.tick();
		if(!isInfinite) {
			if(recipeHandler.shouldTestRecipe()){
				RecipeHolder<DoliumRecipe> recipe=DoliumRecipe.testDolium(tank, inv);
				recipeHandler.setRecipe(recipe);
			}
			if (recipeHandler.tickProcess(1)) {
				updateNeeded=true;
			}
		}

		if(updateNeeded)
			this.syncData();
	}

	private boolean tryContianFluid() {
		ItemResource container=inv.getResource(4);
		if(!container.isEmpty()) {
			try(Transaction trans=Transaction.openRoot()){
				if(tank.getAmountAsInt(0)>=250) {
					FluidResource rs=tank.getResource(0);
					int itemCount=inv.extract(4, container, 1, trans);
					int fluidAmount=tank.extract(rs, 250, trans);
					if(itemCount>0&&fluidAmount>=250) {
						ContanerContainFoodEvent result=Utils.contain(container,rs,fluidAmount);
						if(result.isAllowed()) {
							if(inv.insert(5,result.getOutput(), 1, trans)==1) {
								trans.commit();
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int p1, Inventory p2, Player p3) {
		return new DoliumContainer(p1, p2, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".counter_dolium.title");
	}

	RangedResourceHandler<ItemResource> bowl = new RangedResourceHandler<>(inv, 3, 6) {

	};
	RangedResourceHandler<ItemResource> ingredient = new RangedResourceHandler<>(inv, 0, 3) {

		@Override
		public int extract(ItemResource resource, int amount, TransactionContext transaction) {
			return 0;
		}

		@Override
		public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
			return 0;
		}

	};





	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}

	public ItemStacksResourceHandler getInv() {
		return inv;
	}

	@Override
	public Object getCapability(BlockCapability<?, Direction> cap, Direction side) {
		if (cap == Capabilities.Item.BLOCK) {
			if (side == Direction.UP)
				return ingredient;
			return this.bowl;
		}
		if (cap == Capabilities.Fluid.BLOCK)
			return modtank;
		return null;
	}

	@Override
	public boolean isInfinite() {
		return isInfinite;
	}

}
