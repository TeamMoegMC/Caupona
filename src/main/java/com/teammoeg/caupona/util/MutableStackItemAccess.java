package com.teammoeg.caupona.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class MutableStackItemAccess implements ItemAccess {

    // We essentially reuse the ability of the Container wrappers to mutate the original stack.
    private final ItemStacksResourceHandler wrapper;

    public MutableStackItemAccess(ItemStack stack) {
        wrapper = new ItemStacksResourceHandler(1);
        wrapper.set(0,ItemResource.of(stack),stack.count());
    }

    @Override
    public ItemResource getResource() {
        return wrapper.getResource(0);
    }

    @Override
    public int getAmount() {
        return wrapper.getAmountAsInt(0);
    }

    @Override
    public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        return wrapper.insert(resource, amount, transaction);
    }

    @Override
    public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        return wrapper.extract(resource, amount, transaction);
    }
}
