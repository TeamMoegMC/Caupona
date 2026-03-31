package com.teammoeg.caupona.util;

import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.ItemHoldedFluidData;

import net.minecraft.world.item.Items;
import net.neoforged.neoforge.transfer.ItemAccessResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

public final class FluidItemWrapper extends ItemAccessResourceHandler<FluidResource> {
    public FluidItemWrapper(ItemAccess itemAccess) {
        super(itemAccess, 1);
    }

    @Override
    protected FluidResource getResourceFrom(ItemResource accessResource, int index) {
    	ItemHoldedFluidData comp=accessResource.get(CPCapability.ITEM_FLUID);
        if (comp!=null) {
            return FluidResource.of(comp.fluidType());
        }else {
            return FluidResource.EMPTY;
        }
    }

    @Override
    protected int getAmountFrom(ItemResource accessResource, int index) {
        var resource = getResourceFrom(accessResource, index);
        return resource.isEmpty() ? 0 : 250;
    }

    @Override
    protected ItemResource update(ItemResource accessResource, int index, FluidResource newResource, int newAmount) {
        if (newAmount == 0) {
            return ItemResource.of(Items.BOWL);
        } else if (newAmount != 250) {
            return ItemResource.EMPTY;
        } else {
            var newStack = newResource.toStack(newAmount);
            return ItemResource.of(newStack.getFluidType().getBucket(newStack));
        }
    }

    @Override
    protected int getCapacity(int index, FluidResource resource) {
        return 250;
    }
}