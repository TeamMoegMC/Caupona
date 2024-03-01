package com.teammoeg.caupona.data.recipes.baseconditions;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.recipes.StewBaseCondition;

import net.minecraft.network.FriendlyByteBuf;

public class BaseConditions {

	private static DataDeserializerRegistry<StewBaseCondition> numbers=new DataDeserializerRegistry<>();
	public static final Codec<StewBaseCondition> CODEC=numbers.createCodec();
	static {
		register("tag", FluidTag.class, FluidTag.CODEC, FluidTag::new);
		register("fluid", FluidType.class, FluidType.CODEC, FluidType::new);
		register("fluid_type", FluidTypeType.class, FluidTypeType.CODEC, FluidTypeType::new);
	}
	public static void register(String name, Deserializer<StewBaseCondition> des) {
		numbers.register(name, des);
	}

	public static <R extends StewBaseCondition> void register(String name,Class<R> cls, Codec<R> rjson,
			Function<FriendlyByteBuf, R> rpacket) {
		numbers.register(name, cls, rjson, rpacket);
	}

	public static StewBaseCondition of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}

	public static void write(StewBaseCondition e, FriendlyByteBuf buffer) {
		numbers.write(buffer, e);
	}

	public static void clearCache() {
		numbers.clearCache();
	}
}
