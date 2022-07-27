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
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.data.recipes;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.SerializeUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SpiceRecipe extends IDataRecipe {
	public static List<SpiceRecipe> recipes;
	public static RecipeType<?> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}

	@Override
	public RecipeType<?> getType() {
		return TYPE;
	}

	public Ingredient spice;
	public MobEffectInstance effect;

	public SpiceRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		spice = Ingredient.fromJson(jo.get("spice"));
		if (jo.has("effect")) {
			JsonObject x = jo.get("effect").getAsJsonObject();
			int amplifier = 0;
			if (x.has("level"))
				amplifier = x.get("level").getAsInt();
			int duration = 0;
			if (x.has("time"))
				duration = x.get("time").getAsInt();
			MobEffect eff = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(x.get("effect").getAsString()));
			if(eff!=null)
				effect = new MobEffectInstance(eff, duration, amplifier);
		}
	}

	public SpiceRecipe(ResourceLocation id, FriendlyByteBuf pb) {
		super(id);
		spice=Ingredient.fromNetwork(pb);
		
		effect=SerializeUtil.readOptional(pb,b->MobEffectInstance.load(b.readNbt())).orElse(null);
	}



	public SpiceRecipe(ResourceLocation id, Ingredient spice, MobEffectInstance effect) {
		super(id);
		this.spice = spice;
		this.effect = effect;
	}

	public void write(FriendlyByteBuf pack) {
		spice.toNetwork(pack);;
		SerializeUtil.writeOptional(pack,effect,(e,b)->b.writeNbt(e.save(new CompoundTag())));
	}

	public void serializeRecipeData(JsonObject jx) {
		jx.add("spice",spice.toJson());
		if(effect!=null) {
			JsonObject jo=new JsonObject();
			jo.addProperty("level",effect.getAmplifier());
			jo.addProperty("time",effect.getDuration());
			jo.addProperty("effect",effect.getEffect().getRegistryName().toString());
			jx.add("effect", jo);
		}
	}
	public static int getMaxUse(ItemStack spice) {
		return spice.getMaxDamage()-spice.getDamageValue();
	}
	public static ItemStack handle(ItemStack spice,int cnt) {
		int cdmg=spice.getDamageValue();
		cdmg+=cnt;
		if(cdmg>=spice.getMaxDamage()) {
			return spice.getContainerItem();
		}
		spice.setDamageValue(cdmg);
		return spice;
	}
	public static SpiceRecipe find(ItemStack spice) {
		return recipes.stream().filter(e->e.spice.test(spice)).findFirst().orElse(null);
	}
	public static boolean isValid(ItemStack spice) {
		return recipes.stream().anyMatch(e->e.spice.test(spice));
	}

}
