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

package com.teammoeg.caupona.blocks.stove;

import java.util.Objects;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.CPConfig;
import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.CPTags.Blocks;
import com.teammoeg.caupona.client.CPParticles;
import com.teammoeg.caupona.network.CPBaseBlockEntity;
import com.teammoeg.caupona.util.ChimneyHelper;
import com.teammoeg.caupona.util.FuelType;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.Utils;
import com.teammoeg.caupona.util.WorldDropOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class KitchenStoveBlockEntity extends CPBaseBlockEntity implements MenuProvider, IInfinitable {
	private ItemStacksResourceHandler fuel = new ItemStacksResourceHandler(1) {

		@Override
		public boolean isValid(int index, ItemResource resource) {
			return resource.toStack().getBurnTime(RecipeType.SMELTING,level.fuelValues()) > 0 ;
		}

		@Override
		protected void onContentsChanged(int index, ItemStack previousContents) {
			FuelType old=inventory_fuel;
			inventory_fuel=FuelType.getType(this.getResource(0));
			if(old!=inventory_fuel)
				syncData();
			else
				setChanged();
			super.onContentsChanged(index, previousContents);
		}
		
		
	};
	public ItemStacksResourceHandler getInv() {
		return fuel;
	}
	public int process;
	public int processMax;
	private final int speed;
	private final int maxcd;
	private int cd;
	private float fuelMod = 1f;
	public BlockPos attachedChimney;
	private int chimneyTicks = 0;
	private int chimneyCheckTicks = 20;
	boolean isInfinite = false;
	public FuelType inventory_fuel = FuelType.OTHER;
	public FuelType current=FuelType.OTHER;
	public WorldDropOperation drops;
	public IStove stoveCapabilty=new IStove() {

		@Override
		public int requestHeat(int maxExtract, Transaction trans) {
			if (process <= 0) {
				if (!consumeFuel(trans)) {
					return 0;
				}
				syncNeeded=true;
				setChanged();
				if (!isInfinite)
					process--;
			}
			BlockState bs = getBlockState();
			cd = maxcd;
			if (!bs.getValue(KitchenStove.LIT))
				level.setBlockAndUpdate(getBlockPos(), bs.setValue(KitchenStove.LIT, true));

			return speed;
		}

		@Override
		public boolean canEmitHeat() {
			return process > 0 || fuel.getResource(0).toStack().getBurnTime(RecipeType.SMELTING,level.fuelValues()) > 0;
		}
		
	};
	public KitchenStoveBlockEntity(BlockEntityType<KitchenStoveBlockEntity> tet, BlockPos p, BlockState s, int spd) {
		super(tet, p, s);
		this.speed = spd;
		maxcd = CPConfig.SERVER.stoveCD.get() / speed;
		fuelMod = (float)(double)CPConfig.SERVER.stoveFuel.get();
		chimneyCheckTicks = CPConfig.SERVER.chimneyCheck.get();
		
	}
	public WorldDropOperation getWorldDrop() {
		if(drops==null) {
			drops=new WorldDropOperation(level,worldPosition);
		}
		return drops;
	}
	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(ValueInput nbt, boolean isClient) {
		process = nbt.getIntOr("process",0);
		processMax = nbt.getIntOr("processMax",0);
		attachedChimney = nbt.getLong("chimneyPos").map(BlockPos::of).orElse(null);
		inventory_fuel = FuelType.parse(nbt.getStringOr("fuel_type",""));
		current = FuelType.parse(nbt.getStringOr("current_fuel",""));
		if (!isClient) {
			cd = nbt.getIntOr("cd",0);
			fuel.deserialize(nbt.childOrEmpty("fuel"));
			chimneyTicks = nbt.getIntOr("chimneyTick",0);
			isInfinite = nbt.getBooleanOr("inf",false);
		}
	}
	@Override
	public void writeCustomNBT(ValueOutput nbt, boolean isClient) {
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		if (attachedChimney != null)
			nbt.putLong("chimneyPos", attachedChimney.asLong());
		nbt.putString("fuel_type", inventory_fuel.serialize());
		nbt.putString("current_fuel", current.serialize());
		if (!isClient) {
			nbt.putInt("cd", cd);
			fuel.serialize(nbt.child("fuel"));
			nbt.putInt("chimneyTick", chimneyTicks);
			nbt.putBoolean("inf", isInfinite);
		}
	}




	@Override
	public AbstractContainerMenu createMenu(int a, Inventory b, Player c) {
		return new KitchenStoveContainer(a, b, this);
	}

	@Override
	public Component getDisplayName() {
		return Utils.translate("container." + CPMain.MODID + ".kitchen_stove.title");
	}

	private boolean consumeFuel(Transaction trans) {
		ItemResource infuel=fuel.getResource(0);
		ItemStack fuelStack=infuel.toStack();
		int time = fuelStack.getBurnTime(RecipeType.SMELTING,level.fuelValues());
		if (time <= 0) {
			process = processMax = 0;
			current=FuelType.OTHER;
			return false;
		}
		current = FuelType.getType(infuel);
		ItemStackTemplate ist=fuelStack.getCraftingRemainder();
		
		int extracted=fuel.extract(infuel, 1, trans);
		if(extracted>0) {
			if(ist!=null) {
				ItemStack remain=ist.create();
				ItemResource todrop=fuel.getResourceFrom(remain);
				int toreturn=extracted*remain.getCount();
				toreturn-=fuel.insert(todrop, toreturn, trans);
				if(toreturn>0){
					drops.addDrop(todrop.toStack(toreturn));
					drops.updateSnapshots(trans);
				}
			}
			inventory_fuel=FuelType.getType(fuel.getResource(0));
			float ftime = time * fuelMod / speed* extracted;
			float frac = Mth.frac(ftime);
			if (frac > 0)
				processMax = process = (int) ftime + (this.level.getRandom().nextDouble() < frac ? 1 : 0);
			else
				processMax = process = (int) ftime;
			return true;
		}
		return false;
	}
	boolean syncNeeded=false;
	@SuppressWarnings("resource")
	@Override
	public void tick() {
		if (!level.isClientSide()) {// server logic
			BlockState bs = this.getBlockState();
			
			chimneyTicks++;
			if (chimneyTicks >= chimneyCheckTicks) {
				chimneyTicks = 0;
				BlockPos newChimney=ChimneyHelper.getNearestChimney(this.getLevel(), this.getBlockPos(), 2);
				
				if(!Objects.equals(newChimney, attachedChimney)) {
					
					attachedChimney = newChimney;
					syncNeeded=true;
				}
			}
			boolean flag = false;
			if (process <= 0 && (bs.getValue(KitchenStove.LIT))) {
				bs = bs.setValue(KitchenStove.LIT, false);
				flag = true;
			}
			if (process > 0) {
				if (bs.getValue(KitchenStove.LIT)) {
					cd--;
					if (!isInfinite) {
						process--;
						
					}
					if (attachedChimney != null) {
						if (this.getLevel().getBlockEntity(attachedChimney) instanceof ChimneyPotBlockEntity chimney) {
							chimney.addAsh(speed);
						}
					}
					if (cd <= 0) {
						bs = bs.setValue(KitchenStove.LIT, false);
						flag = true;
					}
					this.setChanged();
				}
			}
			if (flag)
				this.level.setBlockAndUpdate(this.getBlockPos(), bs);
			if(syncNeeded) {
				syncNeeded=false;
				this.syncData();
			}
		} else {// client particles
			if (this.getBlockState().getValue(KitchenStove.LIT)) {
				double d0 = this.getBlockPos().getX();
				double d1 = this.getBlockPos().getY();
				double d2 = this.getBlockPos().getZ();
				RandomSource rand = this.getLevel().getRandom();
				if (attachedChimney == null) {
					if (rand.nextDouble() < 0.25D * speed) {
						this.getLevel().addParticle(ParticleTypes.SMOKE, d0 + .5, d1 + 1, d2 + .5,
								rand.nextDouble() * .5 - .25, rand.nextDouble() * .125, rand.nextDouble() * .5 - .25);
					}
				} else {
					if (rand.nextDouble() < 0.25D * speed) {
						double motY = -0.3, delY = .5;
						if (!this.getLevel().getBlockState(attachedChimney).is(Blocks.CHIMNEY_POT)) {
							motY = rand.nextDouble() * .25;
							delY = 0;
						}
						this.getLevel().addParticle(CPParticles.SOOT.get(), attachedChimney.getX() + .5,
								attachedChimney.getY() + delY, attachedChimney.getZ() + .5,
								rand.nextDouble() * .5 - .25, motY, rand.nextDouble() * .5 - .25);
					}
				}
			}
		}
	}




	public int getSpeed() {
		return speed;
	}

	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}
	@Override
	public boolean isInfinite() {
		return isInfinite;
	}
	@Override
	public Object getCapability(BlockCapability<?, Direction> cap, Direction side) {
		if (cap == Capabilities.Item.BLOCK) {
			return this.fuel;
		}
		if (cap == CPCapability.HEAT_STOVE&&side==Direction.UP)
			return this.stoveCapabilty;
		return null;
	}


}
