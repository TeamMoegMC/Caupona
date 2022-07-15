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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.data.IDataRecipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FoodValueRecipe extends IDataRecipe {
	public static Map<Item, FoodValueRecipe> recipes;
	public static RecipeType<?> TYPE;
	public static RegistryObject<RecipeSerializer<?>> SERIALIZER;
	public static Set<FoodValueRecipe> recipeset;

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
	public final Map<Item, Integer> processtimes;
	private ItemStack repersent;
	public transient Set<ResourceLocation> tags;

	public FoodValueRecipe(ResourceLocation id, int heal, float sat, ItemStack rps, Item... types) {
		super(id);
		this.heal = heal;
		this.sat = sat;
		processtimes = new HashMap<>();
		repersent = rps;
		for (Item i : types)
			processtimes.put(i, 0);
	}

	public FoodValueRecipe(ResourceLocation id, JsonObject jo) {
		super(id);
		heal = jo.get("heal").getAsInt();
		sat = jo.get("sat").getAsFloat();
		processtimes = SerializeUtil.parseJsonList(jo.get("items"), x -> {
			ResourceLocation rl=new ResourceLocation(x.get("item").getAsString());
			if(ForgeRegistries.ITEMS.containsKey(rl)) {
				Item i = ForgeRegistries.ITEMS.getValue(rl);
				int f = 0;
				if (x.has("time"))
					f = x.get("time").getAsInt();
				if(i==Items.AIR)
					return null;
				return new Pair<Item, Integer>(i, f);
			}
			return null;
		}).stream().filter(Objects::nonNull).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		if(processtimes.isEmpty())
			throw new InvalidRecipeException();
		effects = SerializeUtil.parseJsonList(jo.get("effects"), x -> {
			ResourceLocation rl=new ResourceLocation(x.get("effect").getAsString());
			if(ForgeRegistries.POTIONS.containsKey(rl)) {
				int amplifier = 0;
				if (x.has("level"))
					amplifier = x.get("level").getAsInt();
				int duration = 0;
				if (x.has("time"))
					duration = x.get("time").getAsInt();
				MobEffect eff = ForgeRegistries.MOB_EFFECTS.getValue(rl);
				if(eff==null)
					return null;
				MobEffectInstance effect = new MobEffectInstance(eff, duration, amplifier);
				float f = 1;
				if (x.has("chance"))
					f = x.get("chance").getAsInt();
				return new Pair<>(effect, f);
			}return null;
		}).stream().filter(Objects::nonNull).collect(Collectors.toList());
		if(effects!=null)
			effects.removeIf(e->e==null);
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
		if(processtimes!=null&&!processtimes.isEmpty())
		json.add("items",SerializeUtil.toJsonList(processtimes.entrySet(),e->{
			JsonObject jo=new JsonObject();
			jo.addProperty("item",e.getKey().getRegistryName().toString());
			if(e.getValue()!=0)
				jo.addProperty("time",e.getValue());
		return jo;}));
		if(effects!=null&&!effects.isEmpty())
		json.add("effects",SerializeUtil.toJsonList(effects,x->{
			JsonObject jo=new JsonObject();
			jo.addProperty("level",x.getFirst().getAmplifier());
			jo.addProperty("time",x.getFirst().getDuration());
			jo.addProperty("effect",x.getFirst().getEffect().getRegistryName().toString());
			jo.addProperty("chance",x.getSecond());
			return jo;
		}));
		if(repersent!=null)
		json.add("item",Ingredient.of(repersent).toJson());
				
			
	}

	public FoodValueRecipe(ResourceLocation id, FriendlyByteBuf data) {
		super(id);
		heal = data.readVarInt();
		sat = data.readFloat();
		processtimes = SerializeUtil.readList(data, d -> new Pair<>(d.<Item>readRegistryId(), d.readVarInt())).stream()
				.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
		effects = SerializeUtil.readList(data,
				d -> new Pair<>(MobEffectInstance.load(d.readNbt()), d.readFloat()));
		repersent = SerializeUtil.readOptional(data, d -> ItemStack.of(d.readNbt())).orElse(null);
	}

	public void write(FriendlyByteBuf data) {
		data.writeVarInt(heal);
		data.writeFloat(sat);
		SerializeUtil.writeList2(data, processtimes.entrySet(), (d, e) -> {
			d.writeRegistryId(e.getKey());

			d.writeVarInt(e.getValue());
		});
		SerializeUtil.writeList2(data, effects, (d, e) -> {
			CompoundTag nc = new CompoundTag();
			e.getFirst().save(nc);
			d.writeNbt(nc);
			d.writeFloat(e.getSecond());
		});
		SerializeUtil.writeOptional(data, repersent, (d, e) -> e.writeNbt(d.serializeNBT()));
	}

	public void clearCache() {
		tags=null;
	}
	public Set<ResourceLocation> getTags() {
		if (tags == null)
			tags = processtimes.keySet().stream().flatMap(i->(i.builtInRegistryHolder().getTagKeys().map(TagKey::location)))
					.filter(CountingTags.tags::contains).collect(Collectors.toSet());
		return tags;
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
