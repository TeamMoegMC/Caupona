package com.teammoeg.caupona.util;

import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

public class LimitedInterfaceStacksHandler extends RangedResourceHandler<ItemResource> {
	ItemStacksResourceHandler delegate;
	public LimitedInterfaceStacksHandler(ItemStacksResourceHandler delegate) {
		super(delegate, 0, delegate.size());
		this.delegate=delegate;
	}
    public void set(int index, ItemResource resource, int amount) {
    	delegate.set(index, resource, amount);
    }
}
