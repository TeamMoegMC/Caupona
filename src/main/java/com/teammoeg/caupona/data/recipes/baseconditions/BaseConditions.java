package com.teammoeg.caupona.data.recipes.baseconditions;

import java.util.function.Function;

import com.google.gson.JsonObject;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;

import net.minecraft.network.FriendlyByteBuf;

public class BaseConditions {

	private static CachedDataDeserializer<StewBaseCondition,JsonObject> numbers=new CachedDataDeserializer<>() {

		@Override
		protected StewBaseCondition internalOf(JsonObject jo) {
			if (jo.has("type"))
				return getDeserializer(jo.get("type").getAsString()).read(jo);
			if (jo.has("tag"))
				return new FluidTag(jo);
			if (jo.has("fluid"))
				return new FluidType(jo);
			if (jo.has("fluid_type"))
				return new FluidTypeType(jo);
			return null;
		}

	};
	static {
		register("tag", FluidTag::new, FluidTag::new);
		register("fluid", FluidType::new, FluidType::new);
		register("fluid_type", FluidTypeType::new, FluidTypeType::new);
	}
	public static void register(String name, Deserializer<JsonObject, StewBaseCondition> des) {
		numbers.register(name, des);
	}

	public static void register(String name, Function<JsonObject, StewBaseCondition> rjson,
			Function<FriendlyByteBuf, StewBaseCondition> rpacket) {
		numbers.register(name, rjson, rpacket);
	}

	public static StewBaseCondition of(JsonObject jsonElement) {
		return numbers.of(jsonElement);
	}

	public static StewBaseCondition of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}

	public static void write(StewBaseCondition e, FriendlyByteBuf buffer) {
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void clearCache() {
		numbers.clearCache();
	}
}
