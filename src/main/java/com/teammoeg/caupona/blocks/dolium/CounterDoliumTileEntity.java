package com.teammoeg.caupona.blocks.dolium;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.pot.StewPotContainer;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;
import com.teammoeg.caupona.fluid.SoupFluid;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.network.CPBaseTile;
import com.teammoeg.caupona.util.IInfinitable;
import com.teammoeg.caupona.util.SoupInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class CounterDoliumTileEntity extends CPBaseTile implements MenuProvider, IInfinitable {
	ItemStackHandler inv = new ItemStackHandler(6) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 3)
				return DoliumRecipe.testInput(stack);
			if (slot == 3) {
				return SpiceRecipe.isValid(stack);
			}
			if (slot == 4) {
				return true;
			}
			return false;
		}
	};
	public final FluidTank tank = new FluidTank(1250, f -> !f.getFluid().getAttributes().isGaseous(f)) {

		@Override
		protected void onContentsChanged() {
			super.onContentsChanged();
			process = -1;
		}

	};

	private FluidStack tryAddSpice(FluidStack fs) {
		SpiceRecipe spice = null;
		ItemStack spi = inv.getStackInSlot(3);
		if (fs.getAmount() % 250 == 0 && tank.getFluid().getFluid() instanceof SoupFluid)
			spice = SpiceRecipe.find(spi);
		if (spice != null) {
			SoupInfo si = SoupFluid.getInfo(fs);
			if(!si.canAddSpice())return fs;
			if (!isInfinite) {
				int consume=fs.getAmount()/250;
				if(SpiceRecipe.getMaxUse(spi)<consume)return fs;
				inv.setStackInSlot(3, SpiceRecipe.handle(spi, consume));
			}
			si.addSpice(spice.effect,spi);
			SoupFluid.setInfo(fs, si);
		}
		return fs;

	}

	public int process;
	public int processMax;

	public int container;
	private int contTicks;
	boolean isInfinite = false;
	ItemStack inner = ItemStack.EMPTY;

	public CounterDoliumTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.DOLIUM.get(), pWorldPosition, pBlockState);
		processMax = Config.SERVER.staticTime.get();
		contTicks = Config.SERVER.containerTick.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		process = nbt.getInt("process");
		tank.readFromNBT(nbt.getCompound("tank"));
		if (!isClient) {
			container = nbt.getInt("containerTicks");
			inner = ItemStack.of(nbt.getCompound("inner"));
			inv.deserializeNBT(nbt.getCompound("inventory"));
			isInfinite=nbt.getBoolean("inf");
		}

	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process", process);
		nbt.put("tank", tank.writeToNBT(new CompoundTag()));
		nbt.put("inventory", inv.serializeNBT());
		nbt.put("inner", inner.serializeNBT());
		nbt.putInt("containerTicks", container);
		nbt.putBoolean("inf",isInfinite);
	}

	@Override
	public void tick() {
		if(this.level.isClientSide)return;
		container++;
		if (container >= contTicks) {
			container = 0;
			if(isInfinite) {
				FluidStack fs = new FluidStack(tank.getFluid(), tank.getFluidAmount());
				tryContianFluid();
				tank.setFluid(fs);
			}else tryContianFluid();
		}
		if (!inner.isEmpty()) {
			inner = Utils.insertToOutput(inv, 5, inner);
			this.syncData();
			return;
		}
		if (process < 0&&!isInfinite) {
			if (DoliumRecipe.testDolium(tank.getFluid(), inv) != null)
				process = 0;
		}
		if (process >= 0&&!isInfinite) {
			process++;
			if (process >= processMax) {
				process = -1;
				if (inner.isEmpty()) {
					DoliumRecipe recipe = DoliumRecipe.testDolium(tank.getFluid(), inv);
					if (recipe != null) {
						inner = recipe.handleDolium(tank.getFluid(), inv);
					}

				}
			}
		}
		this.syncData();
	}

	boolean tryAddFluid(FluidStack fs) {
		if(isInfinite) return false;
		int tryfill = tank.fill(fs, FluidAction.SIMULATE);
		if (tryfill > 0) {
			if (tryfill == fs.getAmount()) {
				tank.fill(fs, FluidAction.EXECUTE);
				process = -1;
				return true;
			}
			return false;
		}
		return false;
	}

	private void tryContianFluid() {
		ItemStack is = inv.getStackInSlot(4);
		if (!is.isEmpty() && inv.getStackInSlot(5).isEmpty()) {
			if (is.getItem() == Items.BOWL && tank.getFluidAmount() >= 250) {
				BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(this.tank.getFluid().getFluid());
				if (recipe != null) {
					is.shrink(1);
					inv.setStackInSlot(5, recipe.handle(tryAddSpice(tank.drain(250, FluidAction.EXECUTE))));
					process = -1;
					return;
				}
			}

			if (is.getItem() instanceof StewItem) {
				if (tryAddFluid(BowlContainingRecipe.extractFluid(is))) {
					ItemStack ret = is.getContainerItem();
					is.shrink(1);
					process = -1;
					inv.setStackInSlot(5, ret);
				}
				return;
			}
			FluidActionResult far = FluidUtil.tryFillContainer(is, this.tank, 1250, null, true);
			if (far.isSuccess()) {
				is.shrink(1);
				if (far.getResult() != null) {
					process = -1;
					inv.setStackInSlot(5, far.getResult());
				}
				return;
			}
			if(!isInfinite) {
				far = FluidUtil.tryEmptyContainer(is, this.tank, 1250, null, true);
				if (far.isSuccess()) {
					is.shrink(1);
					if (far.getResult() != null) {
						process = -1;
						inv.setStackInSlot(5, far.getResult());
					}
					return;
				}
			}
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int p1, Inventory p2, Player p3) {
		return new DoliumContainer(p1, p2, this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("container." + Main.MODID + ".counter_dolium.title");
	}

	RangedWrapper bowl = new RangedWrapper(inv, 3, 6) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (slot == 5)
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == 3 || slot == 4)
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}
	};
	RangedWrapper ingredient = new RangedWrapper(inv, 0, 3) {

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return ItemStack.EMPTY;
		}
	};
	
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
			return tank.isFluidValid(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action) {
			process = -1;
			if (!isInfinite)
				return tank.fill(resource, action);
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, FluidAction action) {
			process = -1;
			if (isInfinite)
				return action.simulate()?resource:tryAddSpice(resource);
			return action.simulate()?tank.drain(resource, action):tryAddSpice(tank.drain(resource, action));

		}

		@Override
		public FluidStack drain(int maxDrain, FluidAction action) {
			process = -1;
			if (isInfinite)
				return action.simulate()?new FluidStack(tank.getFluid(), maxDrain):tryAddSpice(new FluidStack(tank.getFluid(), maxDrain));
			return action.simulate()?tank.drain(maxDrain, action):tryAddSpice(tank.drain(maxDrain, action));

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

	@Override
	public boolean setInfinity() {
		return isInfinite = !isInfinite;
	}
}
