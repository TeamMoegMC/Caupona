package com.teammoeg.caupona.util;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class TwoSlotItemAccess implements ItemAccess {
	final int inslot;
	final int outslot;
	final ResourceHandler<ItemResource> handler;

	public TwoSlotItemAccess(ResourceHandler<ItemResource> handler, int inslot, int outslot) {
		super();
		this.handler = handler;
		this.inslot = inslot;
		this.outslot = outslot;
	}

	@Override
	public ItemResource getResource() {
		return handler.getResource(inslot);
	}

	@Override
	public int getAmount() {
		return handler.getAmountAsInt(inslot);
	}

	@Override
	public int insert(ItemResource resource, int amount, TransactionContext transaction) {
		return handler.insert(outslot, resource, amount, transaction);
	}

	@Override
	public int extract(ItemResource resource, int amount, TransactionContext transaction) {
		return handler.extract(outslot, resource, amount, transaction);
	}

}
