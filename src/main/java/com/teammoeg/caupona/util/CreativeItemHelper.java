package com.teammoeg.caupona.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class CreativeItemHelper implements Output{
	private static class Entry{
		public ItemStack is;
		public CreativeModeTab.TabVisibility tab;
		int sortnum;
		int insnum;
		public Entry(ItemStack is, TabVisibility tab, int sortnum, int insnum) {
			super();
			this.is = is;
			this.tab = tab;
			this.sortnum = sortnum;
			this.insnum = insnum;
		}
		public int getSortnum() {
			return sortnum;
		}
		public int getInsnum() {
			return insnum;
		}
	}
	private CreativeModeTab tab;
	private int num=Integer.MIN_VALUE;
	private List<Entry> items=new ArrayList<>();
	public CreativeModeTab getTab() {
		return tab;
	}
	public CreativeItemHelper(CreativeModeTab tab) {
		super();
		this.tab = tab;
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
	public void register(Output event) {
		items.sort(Comparator.comparingInt(Entry::getSortnum).thenComparing(Entry::getInsnum));
		for(Entry e:items)
			event.accept(e.is, e.tab);
	}
	
    @Override
    public void accept(ItemStack stack, TabVisibility visibility)
    {
    	this.accept(stack,0,visibility);
    }

    public void accept(Supplier<? extends ItemLike> item, CreativeModeTab.TabVisibility visibility)
    {
       this.accept(item.get(), visibility);
    }

    public void accept(Supplier<? extends ItemLike> item)
    {
       this.accept(item.get());
    }
    public void accept(ItemStack pStack,int sortNum,CreativeModeTab.TabVisibility pTabVisibility) {
    	items.add(new Entry(pStack,pTabVisibility,sortNum,num++));
    };

    public void accept(ItemStack pStack,int sortNum) {
       this.accept(pStack,sortNum, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public void accept(ItemLike pItem,int sortNum, CreativeModeTab.TabVisibility pTabVisibility) {
       this.accept(new ItemStack(pItem),sortNum, pTabVisibility);
    }

    public void accept(ItemLike pItem,int sortNum) {
       this.accept(new ItemStack(pItem),sortNum, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

    public void acceptAll(Collection<ItemStack> pStacks,int sortNum, CreativeModeTab.TabVisibility pTabVisibility) {
       pStacks.forEach((p_252337_) -> {
          this.accept(p_252337_,sortNum, pTabVisibility);
       });
    }

    public void acceptAll(Collection<ItemStack> pStacks,int sortNum) {
       this.acceptAll(pStacks,sortNum, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }
}
