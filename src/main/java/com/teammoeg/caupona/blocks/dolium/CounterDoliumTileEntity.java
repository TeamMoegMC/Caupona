package com.teammoeg.caupona.blocks.dolium;

import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Config;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.blocks.pot.StewPotContainer;
import com.teammoeg.caupona.data.recipes.AspicMeltingRecipe;
import com.teammoeg.caupona.data.recipes.BowlContainingRecipe;
import com.teammoeg.caupona.data.recipes.DoliumRecipe;
import com.teammoeg.caupona.items.StewItem;
import com.teammoeg.caupona.network.CPBaseTile;

import net.minecraft.core.BlockPos;
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
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class CounterDoliumTileEntity extends CPBaseTile implements MenuProvider {
	ItemStackHandler inv = new ItemStackHandler(6) {
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (slot < 3)
				return DoliumRecipe.testInput(stack);
			if (slot == 3) {
				return DoliumRecipe.testContainer(stack);
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
	public int process;
	public int processMax;
	
	public int container;
	private int contTicks;
	
	ItemStack inner=ItemStack.EMPTY;
	public CounterDoliumTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(CPTileTypes.DOLIUM.get(), pWorldPosition, pBlockState);
		processMax = Config.SERVER.staticTime.get();
		contTicks=Config.SERVER.containerTick.get();
	}

	@Override
	public void handleMessage(short type, int data) {
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean isClient) {
		process=nbt.getInt("process");
		tank.readFromNBT(nbt.getCompound("tank"));
		if(!isClient) {
			container=nbt.getInt("containerTicks");
			inner=ItemStack.of(nbt.getCompound("inner"));
			inv.deserializeNBT(nbt.getCompound("inventory"));
		}
		
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean isClient) {
		nbt.putInt("process",process);
		nbt.put("tank",tank.writeToNBT(new CompoundTag()));
		nbt.put("inventory",inv.serializeNBT());
		nbt.put("inner",inner.serializeNBT());
		nbt.putInt("containerTicks", container);
	}

	@Override
	public void tick() {
		container++;
		if(container>=contTicks) {
			container=0;
			tryContianFluid();
		}
		if(!inner.isEmpty()) {
			ItemStack is=inv.getStackInSlot(5);
			if(is.isEmpty()) {
				inv.setStackInSlot(5, inner.split(inner.getMaxStackSize()));
			}else if(ItemHandlerHelper.canItemStacksStack(inner, is)){
				int limit=Math.min(inv.getSlotLimit(5),is.getMaxStackSize());
				is.grow(limit);
				inner.shrink(limit);
			}
			return;
		}
		if(process<0) {
			if(DoliumRecipe.testDolium(tank.getFluid(),inv)!=null)
				process=0;
		}
		if(process>=0) {
			process++;
			if (process >= processMax) {
				process = -1;
				if (inv.getStackInSlot(5).isEmpty()) {
					DoliumRecipe recipe = DoliumRecipe.testDolium(tank.getFluid(),inv);
					if (recipe != null) {
						inner = recipe.handleDolium(tank.getFluid(),inv);
					}
	
				}
			}
		}
		
	}

	boolean tryAddFluid(FluidStack fs) {
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
					inv.setStackInSlot(5, recipe.handle(tank.drain(250, FluidAction.EXECUTE)));
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

	@Override
	public AbstractContainerMenu createMenu(int p1, Inventory p2, Player p3) {
		return new DoliumContainer(p1, p2, this);
	}

	@Override
	public Component getDisplayName() {
		return new TranslatableComponent("container." + Main.MODID + ".counter_dolium.title");
	}


}
