package com.teammoeg.caupona.data.recipes.baseconditions;

import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;

import net.minecraft.network.FriendlyByteBuf;

public class BaseConditions {

	private static CachedDataDeserializer<StewBaseCondition,JsonObject> numbers=new CachedDataDeserializer<>();
	public static final Codec<StewBaseCondition> CODEC=Codec.STRING.dispatch("type", t->t.getType(), t->numbers.getCodec(t));
	static {
		register("tag", FluidTag.CODEC, FluidTag::new);
		register("fluid", FluidType.CODEC, FluidType::new);
		register("fluid_type", FluidTypeType.CODEC, FluidTypeType::new);
	}
	public static void register(String name, Deserializer<JsonObject, StewBaseCondition> des) {
		numbers.register(name, des);
	}

	public static void register(String name, Codec<? extends StewBaseCondition> rjson,
			Function<FriendlyByteBuf, StewBaseCondition> rpacket) {
		numbers.register(name, rjson, rpacket);
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
