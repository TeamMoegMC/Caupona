/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.teammoeg.caupona.fluid.SoupFluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegistryObject;

public class CPFluids {
	private static class TextureColorPair {
		ResourceLocation texture;
		int c;

		public TextureColorPair(ResourceLocation t, int c) {
			super();
			this.texture = t;
			this.c = c;
		}
		public FluidType create(String n){
			ResourceLocation rt=CPStewTexture.texture.getOrDefault(n, texture);
			int cx=CPStewTexture.texture.containsKey(n)?0xffffffff:c;
			FluidType ft=new FluidType(FluidType.Properties.create().viscosity(1200)
					.temperature(333).rarity(Rarity.UNCOMMON).descriptionId("item."+CPMain.MODID+"."+n)) {

						@Override
						public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
							consumer.accept(new IClientFluidTypeExtensions() {

								@Override
								public int getTintColor() {
									return cx;
								}

								@Override
								public ResourceLocation getStillTexture() {
									return rt;
								}

								@Override
								public ResourceLocation getFlowingTexture() {
									return rt;
								}
								
							});
						}
				
			};

			return ft;
		}
	}

	private static final ResourceLocation STILL_WATER_TEXTURE = new ResourceLocation("block/water_still");
	private static final ResourceLocation STILL_SOUP_TEXTURE = new ResourceLocation(CPMain.MODID, "fluid/soup_fluid");
	private static final ResourceLocation STILL_MILK_TEXTURE = new ResourceLocation("forge", "block/milk_still");
	static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, CPMain.MODID);
	static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(Keys.FLUID_TYPES, CPMain.MODID);
	private static final Map<String, TextureColorPair> soupfluids = new HashMap<>();

	public static TextureColorPair soup(int c) {
		return new TextureColorPair(STILL_SOUP_TEXTURE, c);
	}

	public static TextureColorPair water(int c) {
		return new TextureColorPair(STILL_WATER_TEXTURE, c);
	}

	public static TextureColorPair milk(int c) {
		return new TextureColorPair(STILL_MILK_TEXTURE, c);
	}

	public static Stream<Fluid> getAll() {
		return soupfluids.keySet().stream().map(e -> new ResourceLocation(CPMain.MODID, e))
				.map(ForgeRegistries.FLUIDS::getValue);
	}

	static {
		soupfluids.put("acquacotta", soup(0xffdcb259));
		soupfluids.put("bisque", soup(0xffb87246));
		soupfluids.put("bone_gelatin", soup(0xffe3a14a));
		soupfluids.put("borscht", soup(0xff802629));
		soupfluids.put("borscht_cream", soup(0xffcf938e));
		soupfluids.put("congee", soup(0xffd6cbb3));
		soupfluids.put("cream_of_meat_soup", soup(0xffb98c60));
		soupfluids.put("cream_of_mushroom_soup", soup(0xffa7815f));
		soupfluids.put("custard", soup(0xffecda6e));
		soupfluids.put("dilute_soup", soup(0xffc2b598));
		soupfluids.put("egg_drop_soup", soup(0xffd9b773));
		soupfluids.put("egg_tongsui", soup(0xffc9b885));
		soupfluids.put("fish_chowder", soup(0xffd7c68e));
		soupfluids.put("fish_soup", soup(0xffa18441));
		soupfluids.put("fricassee", soup(0xffd2a85f));
		soupfluids.put("goji_tongsui", soup(0xffa97744));
		soupfluids.put("goulash", soup(0xff9e4a2a));
		soupfluids.put("gruel", soup(0xffd3ba9a));
		soupfluids.put("hodgepodge", soup(0xffb59d64));
		soupfluids.put("meat_soup", soup(0xff895e2d));
		soupfluids.put("mushroom_soup", soup(0xff97664c));
		soupfluids.put("nail_soup", water(0xFF3ABDFF));
		soupfluids.put("nettle_soup", soup(0xff467b32));
		soupfluids.put("okroshka", soup(0xffd0c776));
		// soupfluids.put("plain_milk",milk(0xffffffff));
		// soupfluids.put("plain_water",water(0xff374780));
		soupfluids.put("porridge", soup(0xffc6b177));
		soupfluids.put("poultry_soup", soup(0xffbc9857));
		soupfluids.put("pumpkin_soup", soup(0xffd88f31));
		soupfluids.put("pumpkin_soup_cream", soup(0xffe5c58b));
		soupfluids.put("rice_pudding", soup(0xffd8d2bc));
		soupfluids.put("scalded_milk", milk(0xfff3f0e3));
		soupfluids.put("seaweed_soup", soup(0xff576835));
		soupfluids.put("stock", soup(0xffc1a242));
		soupfluids.put("stracciatella", soup(0xffbfbe5c));
		soupfluids.put("ukha", soup(0xffb78533));
		soupfluids.put("vegetable_chowder", soup(0xffa39a42));
		soupfluids.put("vegetable_soup", soup(0xff848929));
		soupfluids.put("walnut_soup", soup(0xffdcb072));
		for (Entry<String, TextureColorPair> i : soupfluids.entrySet()) {
			RegistryObject<FluidType> type=FLUID_TYPES.register(i.getKey(),()->i.getValue().create(i.getKey()));
			LazySupplier<Fluid> crf=new LazySupplier<>();
			crf.setVal(FLUIDS.register(i.getKey(),
					() -> new SoupFluid(new ForgeFlowingFluid.Properties(type, crf,
							crf).slopeFindDistance(1)
											.explosionResistance(100F))));
		}
	}
	public static class LazySupplier<T> implements Supplier<T>{
		Supplier<T> val;
		@Override
		public T get() {
			if(val==null)return null;
			return val.get();
		}
		public void setVal(Supplier<T> val) {
			this.val = val;
		}
		
		
	} 
	public static Set<String> getSoupfluids() {
		return soupfluids.keySet();
	}
}
