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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidTag;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidType;
import com.teammoeg.caupona.data.recipes.baseconditions.FluidTypeType;
import com.teammoeg.caupona.data.recipes.conditions.Halfs;
import com.teammoeg.caupona.data.recipes.conditions.Mainly;
import com.teammoeg.caupona.data.recipes.conditions.MainlyOfType;
import com.teammoeg.caupona.data.recipes.conditions.Must;
import com.teammoeg.caupona.data.recipes.numbers.Add;
import com.teammoeg.caupona.data.recipes.numbers.ConstNumber;
import com.teammoeg.caupona.data.recipes.numbers.ItemIngredient;
import com.teammoeg.caupona.data.recipes.numbers.ItemTag;
import com.teammoeg.caupona.data.recipes.numbers.ItemType;
import com.teammoeg.caupona.data.recipes.numbers.NopNumber;
import com.teammoeg.caupona.util.CacheMap;

import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class SerializeUtil {
	public static class Deserializer<T extends JsonElement, U extends Writeable> {
		private int id;
		public Function<T, U> fromJson;
		public Function<FriendlyByteBuf, U> fromPacket;

		public Deserializer(Function<T, U> fromJson, Function<FriendlyByteBuf, U> fromPacket) {
			super();
			this.fromJson = fromJson;
			this.fromPacket = fromPacket;
		}

		public U read(T json) {
			return fromJson.apply(json);
		}

		public U read(FriendlyByteBuf packet) {
			return fromPacket.apply(packet);
		}

		public void write(FriendlyByteBuf packet, U obj) {
			packet.writeVarInt(id);
			obj.write(packet);
		}

		public JsonElement serialize(U obj) {
			return obj.serialize();
		}
	}

	private static HashMap<String, Deserializer<JsonObject, StewCondition>> conditions = new HashMap<>();
	private static HashMap<String, Deserializer<JsonElement, StewNumber>> numbers = new HashMap<>();
	private static HashMap<String, Deserializer<JsonObject, StewBaseCondition>> basetypes = new HashMap<>();
	// do some cache to lower calculation cost
	private static CacheMap<StewCondition> sccache = new CacheMap<>();
	private static CacheMap<StewNumber> nmcache = new CacheMap<>();
	private static CacheMap<StewBaseCondition> bacache = new CacheMap<>();

	private SerializeUtil() {

	}

	public static void registerCondition(String name, Deserializer<JsonObject, StewCondition> des) {
		conditions.put(name, des);
	}
	public static void registerNumber(String name, Deserializer<JsonElement, StewNumber> des) {
		numbers.put(name, des);
	}

	public static void registerBase(String name, Deserializer<JsonObject, StewBaseCondition> des) {
		basetypes.put(name, des);
	}
	
	public static void registerCondition(String name,Function<JsonObject, StewCondition> rjson,Function<FriendlyByteBuf, StewCondition> rpacket) {
		registerCondition(name,new Deserializer<>(rjson,rpacket));
	}

	public static void registerNumber(String name,Function<JsonElement,StewNumber> rjson,Function<FriendlyByteBuf,StewNumber> rpacket) {
		registerNumber(name,new Deserializer<>(rjson,rpacket));
	}

	public static void registerBase(String name,Function<JsonObject,StewBaseCondition> rjson,Function<FriendlyByteBuf,StewBaseCondition> rpacket) {
		registerBase(name,new Deserializer<>(rjson,rpacket));
	}
	static {
		registerNumber("add", Add::new, Add::new);
		registerNumber("ingredient", ItemIngredient::new, ItemIngredient::new);
		registerNumber("item", ItemType::new, ItemType::new);
		registerNumber("tag", ItemTag::new, ItemTag::new);
		registerNumber("nop", NopNumber::of, NopNumber::of);
		registerNumber("const", ConstNumber::new, ConstNumber::new);
		registerCondition("half", Halfs::new, Halfs::new);
		registerCondition("mainly", Mainly::new, Mainly::new);
		registerCondition("contains", Must::new, Must::new);
		registerCondition("mainlyOf", MainlyOfType::new, MainlyOfType::new);
		registerBase("tag", FluidTag::new, FluidTag::new);
		registerBase("fluid", FluidType::new, FluidType::new);
		registerBase("fluid_type", FluidTypeType::new, FluidTypeType::new);
	}

	public static StewNumber ofNumber(JsonElement jsonElement) {
		return nmcache.of(internalOfNumber(jsonElement));
	}

	private static StewNumber internalOfNumber(JsonElement jsonElement) {
		if (jsonElement == null || jsonElement.isJsonNull())
			return NopNumber.INSTANCE;
		if (jsonElement.isJsonPrimitive()) {
			JsonPrimitive jp = jsonElement.getAsJsonPrimitive();
			if (jp.isString())
				return new ItemTag(jp);
			else if (jp.isNumber())
				return new ConstNumber(jp);
		}
		if (jsonElement.isJsonArray())
			return new Add(jsonElement);
		JsonObject jo = jsonElement.getAsJsonObject();
		if (jo.has("type")) {
			Deserializer<JsonElement, StewNumber> factory = numbers.get(jo.get("type").getAsString());
			if (factory == null)
				return NopNumber.INSTANCE;
			return factory.read(jo);
		}
		if (jo.has("item"))
			return new ItemType(jo);
		else if (jo.has("ingredient"))
			return new ItemIngredient(jo);
		else if (jo.has("types"))
			return new Add(jo);
		else if (jo.has("tag"))
			return new ItemTag(jo);
		return NopNumber.INSTANCE;
	}

	public static StewCondition ofCondition(JsonObject json) {
		return sccache.of(conditions.get(json.get("cond").getAsString()).read(json));
	}

	public static StewBaseCondition ofBase(JsonObject jo) {
		return bacache.of(internalOfBase(jo));
	}

	private static StewBaseCondition internalOfBase(JsonObject jo) {
		if (jo.has("type"))
			return basetypes.get(jo.get("type").getAsString()).read(jo);
		if (jo.has("tag"))
			return new FluidTag(jo);
		if (jo.has("fluid"))
			return new FluidType(jo);
		if (jo.has("base"))
			return new FluidTypeType(jo);
		return null;
	}

	public static StewNumber ofNumber(FriendlyByteBuf buffer) {
		return nmcache.of(numbers.get(buffer.readUtf()).read(buffer));
	}

	public static StewCondition ofCondition(FriendlyByteBuf buffer) {
		return sccache.of(conditions.get(buffer.readUtf()).read(buffer));
	}

	public static StewBaseCondition ofBase(FriendlyByteBuf buffer) {
		return bacache.of(basetypes.get(buffer.readUtf()).read(buffer));
	}

	public static void write(StewNumber e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void write(StewCondition e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void write(StewBaseCondition e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static <T> Optional<T> readOptional(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		if (buffer.readBoolean())
			return Optional.ofNullable(func.apply(buffer));
		return Optional.empty();
	}

	public static <T> void writeOptional(FriendlyByteBuf buffer, T data, BiConsumer<T, FriendlyByteBuf> func) {
		writeOptional(buffer, Optional.ofNullable(data), func);
	}

	public static <T> void writeOptional(FriendlyByteBuf buffer, Optional<T> data, BiConsumer<T, FriendlyByteBuf> func) {
		if (data.isPresent()) {
			buffer.writeBoolean(true);
			func.accept(data.get(), buffer);
			return;
		}
		buffer.writeBoolean(false);
	}

	public static <T> List<T> readList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		if (!buffer.readBoolean())
			return null;
		int cnt = buffer.readVarInt();
		List<T> nums = new ArrayList<>(cnt);
		for (int i = 0; i < cnt; i++)
			nums.add(func.apply(buffer));
		return nums;
	}

	public static <T> void writeList(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<T, FriendlyByteBuf> func) {
		if (elms == null) {
			buffer.writeBoolean(false);
			return;
		}
		buffer.writeBoolean(true);
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(e, buffer));
	}

	public static <T> void writeList2(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<FriendlyByteBuf, T> func) {
		if (elms == null) {
			buffer.writeBoolean(false);
			return;
		}
		buffer.writeBoolean(true);
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(buffer, e));
	}

	public static <T> List<T> parseJsonList(JsonElement elm, Function<JsonObject, T> mapper) {
		if (elm == null)
			return Lists.newArrayList();
		if (elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(), false).map(JsonElement::getAsJsonObject)
					.map(mapper).collect(Collectors.toList());
		return Lists.newArrayList(mapper.apply(elm.getAsJsonObject()));
	}

	public static <T> List<T> parseJsonElmList(JsonElement elm, Function<JsonElement, T> mapper) {
		if (elm == null)
			return Lists.newArrayList();
		if (elm.isJsonArray())
			return StreamSupport.stream(elm.getAsJsonArray().spliterator(), false).map(mapper)
					.collect(Collectors.toList());
		return Lists.newArrayList(mapper.apply(elm.getAsJsonObject()));
	}

	public static <T> JsonArray toJsonList(Collection<T> li, Function<T, JsonElement> mapper) {
		JsonArray ja = new JsonArray();
		li.stream().map(mapper).forEach(ja::add);
		return ja;
	}

	public static <T> ListTag toNBTList(Collection<T> stacks, Function<T, Tag> mapper) {
		ListTag nbt = new ListTag();
		stacks.stream().map(mapper).forEach(nbt::add);
		return nbt;
	}
}
