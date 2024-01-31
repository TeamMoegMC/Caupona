package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.teammoeg.caupona.CPMain;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.ForgeRegistries;

public enum MosaicMaterial implements StringRepresentable{
	brick("t"),
	basalt("b"),
	pumice("p");
	public final String shortName;
	private MosaicMaterial(String shortName) {
		this.shortName = shortName;
	}
	private Item tell;
	private Item getTesserae() {
		if(tell==null) {
			tell=ForgeRegistries.ITEMS.getValue(new ResourceLocation(CPMain.MODID,name()+"_tesserae"));
		}
		return tell;
	}
	private static Map<Item,MosaicMaterial> materials;
	public static MosaicMaterial fromItem(ItemStack is) {
		if(materials==null) {
			materials=ImmutableMap.of(brick.getTesserae(),brick,basalt.getTesserae(),basalt,pumice.getTesserae(),pumice);
		}
		return materials.get(is.getItem());
	}
	@Override
	public String getSerializedName() {
		return this.name();
	}
}
