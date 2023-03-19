package com.teammoeg.caupona.util;

import java.util.function.Supplier;

import com.teammoeg.caupona.TabType;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.CreativeModeTabEvent.BuildContents;

public class CreativeItemHelper implements Output{
	CreativeModeTab tab;
	CreativeModeTabEvent.BuildContents ev;
	public CreativeModeTab getTab() {
		return tab;
	}
	public CreativeModeTabEvent.BuildContents getEv() {
		return ev;
	}
	public CreativeItemHelper(BuildContents ev) {
		super();
		this.tab = ev.getTab();
		this.ev = ev;
	}
	public boolean isMainTab() {
		return TabType.MAIN.test(tab);
	}
	public boolean isFoodTab() {
		return TabType.FOODS.test(tab);
	}
	public boolean isType(TabType tab) {
		if(tab==null)return false;
		return tab.test(this.tab);
	}
    @Override
    public void accept(ItemStack stack, TabVisibility visibility)
    {
    	ev.accept(stack, visibility);
    }

    public void accept(Supplier<? extends ItemLike> item, CreativeModeTab.TabVisibility visibility)
    {
       this.accept(item.get(), visibility);
    }

    public void accept(Supplier<? extends ItemLike> item)
    {
       this.accept(item.get());
    }
}
