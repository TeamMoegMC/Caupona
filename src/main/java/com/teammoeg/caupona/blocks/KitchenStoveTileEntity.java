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

package com.teammoeg.caupona.blocks;

import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.container.KitchenStoveContainer;
import com.teammoeg.caupona.network.INetworkTile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;

public class KitchenStoveTileEntity extends INetworkTile implements Container, MenuProvider,AbstractStove {
	private NonNullList<ItemStack> fuel=NonNullList.withSize(1,ItemStack.EMPTY);
	public int process;
	public int processMax;
	private final int speed;
	private final int maxcd;
	private int cd;
	public KitchenStoveTileEntity(BlockEntityType<KitchenStoveTileEntity> tet,BlockPos p,BlockState s,int spd) {
		super(tet,p,s);
		this.speed = spd;
		maxcd=100/speed;
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		process=nbt.getInt("process");
		processMax=nbt.getInt("processMax");
		fuel.set(0,ItemStack.of(nbt.getCompound("fuel")));
		if(!isClient)
			cd=nbt.getInt("cd");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process",process);
		nbt.putInt("processMax",processMax);
		nbt.put("fuel",fuel.get(0).serializeNBT());
		if(!isClient)
			nbt.putInt("cd",cd);
	}

	@Override
	public void clearContent() {
		fuel.clear();
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return fuel.get(0).isEmpty();
	}

	@Override
	public ItemStack getItem(int index) {
		return fuel.get(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return ContainerHelper.removeItem(fuel, index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ContainerHelper.takeItem(fuel, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
	      this.fuel.set(index, stack);
	      if (stack.getCount() > this.getMaxStackSize()) {
	         stack.setCount(this.getMaxStackSize());
	      }
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		ItemStack itemstack = fuel.get(0);
        return ForgeHooks.getBurnTime(stack,null) > 0 || stack.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
	}

	@Override
	public AbstractContainerMenu createMenu(int a, Inventory b, Player c) {
		return new KitchenStoveContainer(a,b,this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("container." + Main.MODID + ".kitchen_stove.title");
	}
	private boolean consumeFuel() {
        int time = ForgeHooks.getBurnTime(fuel.get(0), RecipeType.SMELTING);
        if (time <= 0) {
        	process=processMax=0;
        	return false;
        }
        fuel.get(0).shrink(1);
        float ftime=time*1.0f/speed;
        float frac=Mth.frac(ftime);
        if(frac>0)
        	processMax=process=(int)ftime+(this.level.random.nextDouble()<frac?1:0);
        else
        	processMax=process=(int)ftime;
        return true;
    }
	@Override
	public void tick() {
		if(!level.isClientSide) {
			BlockState bs=this.getBlockState();
			boolean flag=false;
			if(process<=0&&(bs.getValue(KitchenStove.LIT)||bs.getValue(KitchenStove.ASH))) {
				bs=bs.setValue(KitchenStove.LIT,false).setValue(KitchenStove.ASH,false);
				flag=true;
			}
			boolean ie=fuel.get(0).isEmpty();
			int fs=bs.getValue(KitchenStove.FUELED);
			if(ie!=(fs==0)) {
				flag=true;
				bs=bs.setValue(KitchenStove.FUELED,ie?0:1);
			}
			if(process>0) {
				if(!bs.getValue(KitchenStove.ASH)) {
					flag=true;
					bs=bs.setValue(KitchenStove.ASH,true);
				}
				if(bs.getValue(KitchenStove.LIT)) {
					cd--;
					process--;
					if(cd<=0) {
						bs=bs.setValue(KitchenStove.LIT,false);
						flag=true;
					}
				}
			}
			if(flag)
				this.level.setBlockAndUpdate(this.getBlockPos(),bs);
			this.syncData();
		}
	}

	@Override
	public int requestHeat() {
		if(this.process<=0) {
			if(!consumeFuel()) {
				return 0;
			}
			process--;
		}
		BlockState bs=this.getBlockState();
		cd=maxcd;
		if(!bs.getValue(KitchenStove.LIT))
			this.level.setBlockAndUpdate(this.getBlockPos(),bs.setValue(KitchenStove.LIT,true));
		
		return speed;
	}

	@Override
	public boolean canEmitHeat() {
		return this.process>0||ForgeHooks.getBurnTime(fuel.get(0), RecipeType.SMELTING)>0;
	}



}
