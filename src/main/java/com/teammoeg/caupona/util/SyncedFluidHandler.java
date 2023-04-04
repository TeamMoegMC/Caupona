package com.teammoeg.caupona.util;

import com.teammoeg.caupona.network.CPBaseBlockEntity;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SyncedFluidHandler implements IFluidHandler {
	CPBaseBlockEntity block;
	public SyncedFluidHandler(CPBaseBlockEntity block, IFluidHandler nested) {
		super();
		this.block = block;
		this.nested = nested;
	}

	IFluidHandler nested;
	

	@Override
	public int getTanks() {
		return nested.getTanks();
	}

	@Override
	public FluidStack getFluidInTank(int t) {
		return nested.getFluidInTank(t);
	}

	@Override
	public int getTankCapacity(int t) {
		return nested.getTankCapacity(t);
	}

	@Override
	public boolean isFluidValid(int t, FluidStack stack) {
		return nested.isFluidValid(t, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		int filled = nested.fill(resource, action);
		if (filled != 0 && action.execute()) {
			block.syncData();
		}
		return filled;
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		FluidStack drained = nested.drain(resource, action);
		if (!drained.isEmpty() && action.execute()) {
			block.syncData();
		}
		return drained;
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		FluidStack drained = nested.drain(maxDrain, action);
		if (!drained.isEmpty() && action.execute()) {
			block.syncData();
		}
		return drained;
	}

}
