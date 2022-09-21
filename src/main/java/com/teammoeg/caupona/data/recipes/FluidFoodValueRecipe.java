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

package com.teammoeg.caupona.data.recipes;

import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;
import com.teammoeg.caupona.data.SerializeUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FluidFoodValueRecipe extends IDataRecipe {
	public static Map<ResourceLocation, FluidFoodValueRecipe> recipes;
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

	public int heal;
	public float sat;
	public List<Pair<MobEffectInstance, Float>> effects;
	private ItemStack repersent;
	public int parts;
	public ResourceLocation f;

	public FluidFoodValueRecipe(ResourceLocation id, int heal, float sat, ItemStack repersent, int parts, Fluid f) {
		super(id);
		this.heal = heal;
		this.sat = sat;
		this.repersent = repersent;
		this.parts = parts;
		this.f = f.getRegistryName();
	}

	public FluidFoodValueRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		heal = jo.get("heal").getAsInt();
		sat = jo.get("sat").getAsFloat();
		f = new ResourceLocation(jo.get("fluid").getAsString());
		if (jo.has("parts"))
			parts = jo.get("parts").getAsInt();
		else
			parts = 1;
		effects = SerializeUtil.parseJsonList(jo.get("effects"), x -> {
			int amplifier = 0;
			if (x.has("level"))
				amplifier = x.get("level").getAsInt();
			int duration = 0;
			if (x.has("time"))
				duration = x.get("time").getAsInt();
			MobEffect eff = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(x.get("effect").getAsString()));
			if (eff == null)
				return null;
			MobEffectInstance effect = new MobEffectInstance(eff, duration, amplifier);
			float f = 1;
			if (x.has("chance"))
				f = x.get("chance").getAsInt();
			return new Pair<>(effect, f);
		});
		if (effects != null)
			effects.removeIf(e -> e == null);
		if (jo.has("item")) {
			ItemStack[] i = Ingredient.fromJson(jo.get("item")).getItems();
			if (i.length > 0)
				repersent = i[0];
		}
	}

	@Override
	public void serializeRecipeData(JsonObject json) {
		json.addProperty("heal", heal);
		json.addProperty("sat", sat);
		json.addProperty("parts", parts);
		json.addProperty("fluid", f.toString());
		if (effects != null && !effects.isEmpty())
			json.add("effects", SerializeUtil.toJsonList(effects, x -> {
				JsonObject jo = new JsonObject();
				jo.addProperty("level", x.getFirst().getAmplifier());
				jo.addProperty("time", x.getFirst().getDuration());
				jo.addProperty("effect", x.getFirst().getEffect().getRegistryName().toString());
				jo.addProperty("chance", x.getSecond());
				return jo;
			}));
		if (repersent != null)
			json.add("item", Ingredient.of(repersent).toJson());

	}

	public FluidFoodValueRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		heal = data.readVarInt();
		sat = data.readFloat();
		parts = data.readVarInt();
		f = data.readResourceLocation();
		effects = SerializeUtil.readList(data, d -> new Pair<>(MobEffectInstance.load(d.readNbt()), d.readFloat()));
		repersent = SerializeUtil.readOptional(data, d -> ItemStack.of(d.readNbt())).orElse(null);
	}

	public FluidFoodValueRecipe(ResourceLocation id, int heal, float sat, ItemStack repersent, int parts,
			ResourceLocation f) {
		super(id);
		this.heal = heal;
		this.sat = sat;
		this.repersent = repersent;
		this.parts = parts;
		this.f = f;
	}

	public void write(FriendlyByteBuf data) {
		data.writeVarInt(heal);
		data.writeFloat(sat);
		data.writeVarInt(parts);
		data.writeResourceLocation(f);
		SerializeUtil.writeList2(data, effects, (d, e) -> {
			CompoundTag nc = new CompoundTag();
			e.getFirst().save(nc);
			d.writeNbt(nc);
			d.writeFloat(e.getSecond());
		});
		SerializeUtil.writeOptional(data, repersent, (d, e) -> e.writeNbt(d.serializeNBT()));
	}

	public ItemStack getRepersent() {
		return repersent;
	}

	public void setRepersent(ItemStack repersent) {
		if (repersent != null)
			this.repersent = repersent.copy();
		else
			this.repersent = null;
	}
}
