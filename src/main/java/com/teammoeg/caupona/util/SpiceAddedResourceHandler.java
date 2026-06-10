package com.teammoeg.caupona.util;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.StewInfo;
import com.teammoeg.caupona.data.recipes.SpiceRecipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class SpiceAddedResourceHandler implements ResourceHandler<FluidResource> {
	public final ResourceHandler<FluidResource> handler;
	public final ResourceHandler<ItemResource> item;
	public final int spiceSlot;



	public SpiceAddedResourceHandler(ResourceHandler<FluidResource> handler, ResourceHandler<ItemResource> item, int spiceSlot) {
		super();
		this.handler = handler;
		this.item = item;
		this.spiceSlot = spiceSlot;
	}

	public int size() {
		return handler.size();
	}

	/**
	 *
	 */
	public FluidResource getResource(int index) {
		
		FluidResource actual= handler.getResource(index);
		ItemResource spice = item.getResource(spiceSlot);
		return asSpiced(actual,spice);
	}

	public long getAmountAsLong(int index) {
		return handler.getAmountAsLong(index);
	}

	public int getAmountAsInt(int index) {
		return handler.getAmountAsInt(index);
	}

	public long getCapacityAsLong(int index, FluidResource resource) {
		return handler.getCapacityAsLong(index, resource);
	}

	public int getCapacityAsInt(int index, FluidResource resource) {
		return handler.getCapacityAsInt(index, resource);
	}

	public boolean isValid(int index, FluidResource resource) {
		return handler.isValid(index, resource);
	}

	public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
		return handler.insert(index, resource, amount, transaction);
	}

	public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
		
		if(amount>=250) {
			int actualShouldTake=Math.min(this.getAmountAsInt(index), amount)/250;
			FluidResource noneSpiced=handler.getResource(index);

			ItemResource spice = item.getResource(spiceSlot);
			FluidResource spiced=asSpiced(noneSpiced,spice);
			if(noneSpiced!=spiced) {
				if(!spiced.equals(resource))
					return 0;
				try(Transaction child=Transaction.open(transaction)){
					int spiceCount=item.extract(spiceSlot, spice, 1, child);
					if(spiceCount>0) {
						ItemStack spiceStack=spice.toStack(spiceCount);
						actualShouldTake=Math.min(actualShouldTake, SpiceRecipe.getMaxUse(spiceStack));
						int actualExtracted=handler.extract(index, noneSpiced, actualShouldTake*250, child);
						if(actualExtracted%250!=0)
							return 0;
						ItemStack out=SpiceRecipe.handle(spiceStack, actualShouldTake);
						if(item.insert(spiceSlot, ItemResource.of(out), out.count(), child)==out.count()) {
							child.commit();
							return actualExtracted;
						}
					}
				}
			}
		}
		return handler.extract(index, resource, amount, transaction);
		
	}
	private FluidResource asSpiced(FluidResource orig,ItemResource spice) {
		
		StewInfo si=orig.get(CPCapability.STEW_INFO);
		if(si!=null&&si.canAddSpice()) {
			SpiceRecipe recipe = SpiceRecipe.find(spice.toStack());
			if(recipe!=null) {
				StewInfo sin=si.copy();
				sin.addSpice(recipe.effect, spice.getItem());
				FluidStack out=orig.toStack(1000);
				Utils.setInfo(out, sin);
				return FluidResource.of(out);
				
			}
		}
		return orig;
	}
}
