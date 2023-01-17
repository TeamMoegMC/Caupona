package com.teammoeg.caupona.compat.treechop;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;

import ht.treechop.api.TreeChopAPI;

public class TreechopCompat {
	TreeChopAPI api;

	public TreechopCompat(Object api) {
		super();
		if(api instanceof TreeChopAPI)
		this.api = (TreeChopAPI)api;
		init();
		Main.logger.info("Treechop compat loaded");
	}
	public void init() {
		this.api.registerChoppableBlockBehavior(CPBlocks.FIG_LOG.get(),new SlimTreeHandler());
		this.api.registerChoppableBlockBehavior(CPBlocks.WOLFBERRY_LOG.get(),new SlimTreeHandler());
	}
}
