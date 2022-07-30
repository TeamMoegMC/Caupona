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

package com.teammoeg.caupona.blocks.pot;

import java.util.ArrayList;
import java.util.List;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.stove.IStove;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BoilingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DissolveRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.FoodValueRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.data.recipes.StewCookingRecipe;
import com.teammoeg.caupona.data.recipes.StewPendingContext;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.network.CPBaseTile;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.SoupInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class StewPotTileEntity extends CPBaseTile implements MenuProvider, IInfinitable {
	private ItemStackHandler inv = new ItemStackHandler(12) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 9)
				return stack.getItem() == Items.POTION || StewCookingRecipe.isCookable(stack);
			if (slot == 9) {
				Item i = stack.getItem();
				return i == Items.BOWL || i instanceof StewItem || AspicMeltingRecipe.find(stack) != null;
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

	public ItemStackHandler getInv() {
		return inv;
	}

	public SoupInfo current;
	private FluidTank tank = new FluidTank(1250, StewCookingRecipe::isBoilable) {
		protected void onContentsChanged() {
			// all fluid emptied
			if (this.isEmpty())
				current = null;
			nowork = 0;
		}

	};

	public StewPotTileEntity(BlockPos p, BlockState s) {
		super(CPTileTypes.STEW_POT.get(), p, s);
		stillticks = Config.COMMON.staticTime.get();
		contTicks = Config.SERVER.containerTick.get();
	}

	public FluidTank getTank() {
		return tank;
	}

	public int process;
	public int processMax;

	public int nowork;
	private int stillticks;

	public int container;
	private int contTicks;

	public boolean working = false;
	public boolean operate = false;
	public short proctype = 0;
	public boolean rsstate = false;
	private boolean isInfinite = false;

	public Fluid become;
	public ResourceLocation nextbase;
	public static final short NOP = 0;
	public static final short BOILING = 1;
	public static final short COOKING = 2;
	public static final short STIRING = 3;

	@Override
	public void tick() {
		if (!level.isClientSide) {
			working = false;
			if (processMax > 0) {
				nowork = 0;
				BlockEntity te = level.getBlockEntity(worldPosition.below());
				if (te instanceof IStove) {
					int rh = ((IStove) te).requestHeat();
					if (!isInfinite)
						process += rh;
					if (rh > 0)
						working = true;
					if (process >= processMax) {
						process = 0;
						processMax = 0;
						doWork();
					}
				} else
					return;

			} else {
				if (!tank.isEmpty() && !isInfinite) {
					nowork++;
					if (nowork >= stillticks) {
						nowork = 0;
						if (inv.getStackInSlot(10).isEmpty()) {
							DoliumRecipe recipe = DoliumRecipe.testPot(getTank().getFluid());
							if (recipe != null) {
								ItemStack out = recipe.handle(getTank().getFluid());

								inv.setStackInSlot(10, out);
							}

						}
					}
				}
				if (!isInfinite)
					prepareWork();
				container++;
				if (container >= contTicks) {
					container = 0;
					if (isInfinite) {
						FluidStack fs = new FluidStack(tank.getFluid(), tank.getFluidAmount());
						if (canAddFluid())
							tryContianFluid();
						tank.setFluid(fs);
					} else {
						if (canAddFluid())
							tryContianFluid();
					}

				}

			}
		}
		this.syncData();
	}

	private FluidStack tryAddSpice(FluidStack fs) {
		SpiceRecipe spice = null;
		ItemStack spi = inv.getStackInSlot(11);
		if (fs.getAmount() % 250 == 0 && tank.getFluid().getFluid() instanceof SoupFluid)
			spice = SpiceRecipe.find(spi);
		if (spice != null) {
			SoupInfo si = SoupFluid.getInfo(fs);
			if (!si.canAddSpice())
				return fs;
			if (!isInfinite) {
				int consume = fs.getAmount() / 250;
				if (SpiceRecipe.getMaxUse(spi) < consume)
					return fs;
				inv.setStackInSlot(11, SpiceRecipe.handle(spi, consume));
			}
			si.addSpice(spice.effect, spi);
			SoupFluid.setInfo(fs, si);
		}
		return fs;

	}

	private void tryContianFluid() {
		ItemStack is = inv.getStackInSlot(9);
		if (!is.isEmpty() && inv.getStackInSlot(10).isEmpty()) {
			if (is.getItem() == Items.BOWL && tank.getFluidAmount() >= 250) {
				BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(this.tank.getFluid().getFluid());
				if (recipe != null) {
					is.shrink(1);
					inv.setStackInSlot(10, recipe.handle(tryAddSpice(tank.drain(250, FluidAction.EXECUTE))));
					nowork = 0;
					return;
				}
			}

			if (is.getItem() instanceof StewItem) {
				if (tryAddFluid(BowlContainingRecipe.extractFluid(is))) {
					ItemStack ret = is.getContainerItem();
					is.shrink(1);
					nowork = 0;
					inv.setStackInSlot(10, ret);
				}
				return;
			}
			if (!isInfinite) {
				AspicMeltingRecipe amr = AspicMeltingRecipe.find(is);
				if (amr != null) {
					int remainSpace = tank.getCapacity() - tank.getFluidAmount();
					int produce = Math.min(remainSpace / amr.amount, is.getCount());
					FluidStack fs = amr.handle(is);
					fs.setAmount(fs.getAmount() * produce);
					if (tryAddFluid(fs, amr.time, false)) {
						ItemStack ret = is.getContainerItem();
						ret.setCount(produce);
						is.shrink(produce);
						nowork = 0;
						inv.setStackInSlot(10, ret);
					}
					return;
				}
			}
			FluidActionResult far = FluidUtil.tryFillContainer(is, this.tank, 1250, null, true);
			if (far.isSuccess()) {
				is.shrink(1);
				if (far.getResult() != null) {
					nowork = 0;
					inv.setStackInSlot(10, far.getResult());
				}
			}
		}
	}

	public boolean canAddFluid() {
		return proctype == 0;
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		process = nbt.getInt("process");
		processMax = nbt.getInt("processMax");
		proctype = nbt.getShort("worktype");
		rsstate = nbt.getBoolean("rsstate");
		if (inv.getSlots() < 12)
			inv.setSize(12);
		if (isClient)
			working = nbt.getBoolean("working");
		tank.readFromNBT(nbt);
		if (nbt.contains("result"))
			become = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(nbt.getString("result")));
		else
			become = null;
		if (!isClient) {
			inv.deserializeNBT(nbt.getCompound("inv"));
			current = nbt.contains("current") ? new SoupInfo(nbt.getCompound("current")) : null;
			nextbase = nbt.contains("resultBase") ? new ResourceLocation(nbt.getString("resultBase")) : null;
			nowork = nbt.getInt("nowork");
			container = nbt.getInt("containerTicks");
			isInfinite = nbt.getBoolean("inf");
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process", process);
		nbt.putInt("processMax", processMax);
		nbt.putShort("worktype", proctype);
		nbt.putBoolean("rsstate", rsstate);
		if (isClient)
			nbt.putBoolean("working", working);

		tank.writeToNBT(nbt);
		if (become != null)
			nbt.putString("result", become.getRegistryName().toString());
		if (!isClient) {
			nbt.put("inv", inv.serializeNBT());
			nbt.putInt("nowork", nowork);
			if (current != null)
				nbt.put("current", current.save());
			if (nextbase != null)
				nbt.putString("resultBase", nextbase.toString());
			nbt.putInt("containerTicks", container);
			nbt.putBoolean("inf", isInfinite);
		}
	}

	private void prepareWork() {
		if (rsstate && proctype == 0 && !operate && level.hasNeighborSignal(this.worldPosition))
			operate = true;

		if (operate && proctype == 0) {
			operate = false;
			BlockEntity te = level.getBlockEntity(worldPosition.below());
			if (!(te instanceof IStove) || !((IStove) te).canEmitHeat())
				return;
			if (doBoil())
				proctype = 1;
			else if (makeSoup())
				proctype = 2;
		} else if (proctype == 1) {
			if (makeSoup())
				proctype = 2;
			else
				proctype = 0;
		}
	}

	private void doWork() {
		if (proctype == 1) {
			finishBoil();
			boolean hasItem = false;
			for (int i = 0; i < 9; i++) {
				ItemStack is = inv.getStackInSlot(i);
				if (!is.isEmpty()) {
					hasItem = true;
					break;
				}
			}
			if (!hasItem)
				proctype = 0;
		} else if (proctype == 2 || proctype == 3)
			finishSoup();
	}

	private boolean doBoil() {
		BoilingRecipe recipe = BoilingRecipe.recipes.get(this.tank.getFluid().getFluid());
		if (recipe == null)
			return false;
		become = recipe.after;
		this.processMax = (int) (recipe.time * (this.tank.getFluidAmount() / 250f));
		this.process = 0;
		return true;
	}

	private void finishBoil() {
		BoilingRecipe recipe = BoilingRecipe.recipes.get(this.tank.getFluid().getFluid());
		if (recipe == null)
			return;
		current = null;
		tank.setFluid(recipe.handle(tank.getFluid()));
	}

	private void adjustParts(int count) {
		float oparts = tank.getFluidAmount() / 250f;
		int parts = (int) (oparts + count);
		getCurrent().adjustParts(oparts, parts);
		tank.getFluid().setAmount(parts * 250);
	}

	private boolean makeSoup() {
		if (tank.getFluidAmount() <= 250)
			return false;// cant boil if under one bowl
		if (getCurrent().stacks.size() > 27)
			return false;// too much ingredients
		int oparts = tank.getFluidAmount() / 250;
		int parts = oparts - 1;
		int itms = 0;
		List<MobEffectInstance> cr = new ArrayList<>(current.effects);
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {

				if (is.getItem() == Items.POTION) {
					outer: for (MobEffectInstance n : PotionUtils.getMobEffects(is)) {
						for (MobEffectInstance eff : cr) {
							if (SoupInfo.isEffectEquals(eff, n))
								continue outer;
						}
						cr.add(n);
					}
				} else if (StewCookingRecipe.isCookable(is))
					itms++;
				else
					return false;
			}
		}
		if (itms / (float) parts + (current.getDensity() * oparts) / parts > 3 || cr.size() > 3) {// too dense
			return false;
		}

		process = 0;
		adjustParts(-1);
		boolean hasItem = false;
		NonNullList<ItemStack> interninv = NonNullList.withSize(9, ItemStack.EMPTY);
		for (int i = 0; i < 9; i++) {
			ItemStack is = inv.getStackInSlot(i);
			if (!is.isEmpty()) {
				if (is.getItem() == Items.POTION) {
					for (MobEffectInstance eff : PotionUtils.getMobEffects(is))
						current.addEffect(eff, parts);
					inv.setStackInSlot(i, new ItemStack(Items.GLASS_BOTTLE));
				} else {
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
				hasItem = true;
			}
		}

		if (!hasItem) {// just reduce water
			current.completeEffects();
			processMax = Math.max(Config.SERVER.potCookTimeBase.get(), decideSoup());
			return true;
		}
		int tpt = Config.SERVER.potMixTimeBase.get();
		outer: for (int i = 0; i < 9; i++) {
			ItemStack is = interninv.get(i);
			if (is.isEmpty())
				break;
			current.addItem(is, parts);
			for (DissolveRecipe rs : DissolveRecipe.recipes) {
				if (rs.item.test(is)) {
					tpt += rs.time;
					continue outer;
				}
			}
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(is.getItem());
			if (fvr != null)
				tpt += fvr.processtimes.getOrDefault(is.getItem(), 0);
		}
		current.completeAll();
		tpt = Math.max(Config.SERVER.potCookTimeBase.get(), tpt);
		interninv.clear();
		processMax = Math.max(decideSoup(), tpt);
		return true;
	}

	private int decideSoup() {
		become = tank.getFluid().getFluid();

		StewPendingContext ctx = new StewPendingContext(getCurrent(), become.getRegistryName());
		nextbase = current.base;
		if (ctx.getItems().isEmpty()) {
			return 0;
		}
		StewCookingRecipe cri = StewCookingRecipe.recipes.get(become);
		int minprio = Integer.MIN_VALUE;
		if (cri != null && cri.matches(ctx) != 0)
			minprio = cri.getPriority();
		for (StewCookingRecipe cr : StewCookingRecipe.sorted) {
			if (cr.getPriority() <= minprio)
				return 0;
			int mt = cr.matches(ctx);
			if (mt != 0) {
				if (mt == 2)
					nextbase = become.getRegistryName();
				else
					nextbase = current.base;
				become = cr.output;
				return cr.time;
			}
		}

		return 0;
	}

	private void finishSoup() {
		if (nextbase != null && become != null) {
			getCurrent().base = nextbase;
			nextbase = null;
			FluidStack fss = new FluidStack(become, tank.getFluidAmount());
			current.recalculateHAS();
			SoupFluid.setInfo(fss, current);
			current = null;
			tank.setFluid(fss);
		}
		become = null;
		nextbase = null;
		proctype = 0;
	}

	public boolean canAddFluid(FluidStack fs) {
		if (isInfinite)
			return false;
		int tryfill = tank.fill(fs, FluidAction.SIMULATE);
		if (tryfill > 0) {
			if (tryfill == fs.getAmount()) {
				return true;
			}
			return false;
		}
		if (tank.getCapacity() - tank.getFluidAmount() < fs.getAmount())
			return false;
		BlockEntity te = level.getBlockEntity(worldPosition.below());
		if (!(te instanceof IStove) || !((IStove) te).canEmitHeat())
			return false;
		SoupInfo n = SoupFluid.getInfo(fs);
		if (!getCurrent().base.equals(n.base) && !current.base.equals(fs.getFluid().getRegistryName())
				&& !n.base.equals(tank.getFluid().getFluid().getRegistryName())) {
			BoilingRecipe bnx = BoilingRecipe.recipes.get(fs.getFluid());
			if (bnx == null)
				return false;
			if (!current.base.equals(bnx.after.getRegistryName()))
				return false;
		}
		return current.canMerge(n, tank.getFluidAmount() / 250f, fs.getAmount() / 250f);
	}

	public boolean tryAddFluid(FluidStack fs) {
		return tryAddFluid(fs, Config.SERVER.potMixTimeBase.get(), true);
	}

	public boolean tryAddFluid(FluidStack fs, int extraTime, boolean canIgnoreHeat) {
		if (isInfinite)
			return false;
		if (canIgnoreHeat) {
			int tryfill = tank.fill(fs, FluidAction.SIMULATE);
			if (tryfill > 0) {
				if (tryfill == fs.getAmount()) {
					tank.fill(fs, FluidAction.EXECUTE);
					nowork = 0;
					return true;
				}
				return false;
			}
		} else if (tank.isEmpty()) {
			int tryfill = tank.fill(fs, FluidAction.SIMULATE);
			if (tryfill > 0) {
				if (tryfill == fs.getAmount()) {
					tank.fill(fs, FluidAction.EXECUTE);
					this.proctype = 3;
					this.process = 0;
					this.processMax = extraTime;
					become = null;
					nextbase = null;
					nowork = 0;
					return true;
				}
				return false;
			}
		}
		if (tank.getCapacity() - tank.getFluidAmount() < fs.getAmount())
			return false;
		BlockEntity te = level.getBlockEntity(worldPosition.below());
		if (!(te instanceof IStove) || !((IStove) te).canEmitHeat())
			return false;
		SoupInfo n = SoupFluid.getInfo(fs);
		int pm = 0;

		if (!getCurrent().base.equals(n.base) && !current.base.equals(fs.getFluid().getRegistryName())
				&& !n.base.equals(tank.getFluid().getFluid().getRegistryName())) {
			BoilingRecipe bnx = BoilingRecipe.recipes.get(fs.getFluid());
			if (bnx == null)
				return false;
			if (!getCurrent().base.equals(bnx.after.getRegistryName()))
				return false;
			fs = bnx.handle(fs);
			pm = (int) (bnx.time * (fs.getAmount() / 250f));
		}

		if (current.merge(n, tank.getFluidAmount() / 250f, fs.getAmount() / 250f)) {
			this.adjustParts(fs.getAmount() / 250);
			int num = Math.max(decideSoup(), extraTime);
			this.proctype = 3;
			this.process = 0;
			this.processMax = Math.max(pm, num);
			nowork = 0;
			return true;
		}

		return false;
	}

	@Override
	public AbstractContainerMenu createMenu(int p1, Inventory p2, Player p3) {
		return new StewPotContainer(p1, p2, this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("container." + Main.MODID + ".stewpot.title");
	}

	@Override
	public void handleMessage(short type, int data) {
		if (type == 0)
			if (this.proctype == 0)
				this.operate = true;
		if (type == 1) {
			if (data == 1)
				rsstate = false;
			else if (data == 2)
				rsstate = true;
		}

	}

	IFluidHandler handler = new IFluidHandler() {
		@Override
		public int getTanks() {
			return 1;
		}

		@Override
		public FluidStack getFluidInTank(int t) {
			if (t == 0)
				return tank.getFluid();
			return FluidStack.EMPTY;
		}

		@Override
		public int getTankCapacity(int t) {
			if (t == 0)
				return tank.getCapacity();
			return 0;
		}

		@Override
		public boolean isFluidValid(int t, FluidStack stack) {
			if (t == 0 && canAddFluid())
				return tank.isFluidValid(stack);
			return false;
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			if (canAddFluid() && !isInfinite)
				return tank.fill(resource, action);
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {

			if (canAddFluid()) {
				if (isInfinite)
					return action.simulate() ? resource : tryAddSpice(resource);
				return action.simulate() ? tank.drain(resource, action) : tryAddSpice(tank.drain(resource, action));
			}
			return FluidStack.EMPTY;
		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {

			if (canAddFluid()) {
				if (isInfinite)
					return action.simulate() ? new FluidStack(tank.getFluid(), maxDrain)
							: tryAddSpice(new FluidStack(tank.getFluid(), maxDrain));
				return action.simulate() ? tank.drain(maxDrain, action) : tryAddSpice(tank.drain(maxDrain, action));
			}
			return FluidStack.EMPTY;
		}

	};
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
	LazyOptional<IFluidHandler> fl = LazyOptional.of(() -> handler);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == Direction.UP)
				return up.cast();
			return this.side.cast();
		}
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return fl.cast();
		return super.getCapability(cap, side);
	}

	public SoupInfo getCurrent() {
		if (current == null)
			current = SoupFluid.getInfo(tank.getFluid());
		return current;
	}

	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}
}
