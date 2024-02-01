package com.teammoeg.caupona.data.recipes.conditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.DataDeserializerRegistry;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.recipes.IngredientCondition;

import net.minecraft.network.FriendlyByteBuf;

public class Conditions {
	private static DataDeserializerRegistry<IngredientCondition> numbers=new DataDeserializerRegistry<>();
	public static final Codec<IngredientCondition> CODEC=numbers.createCodec();
	static {
		register("half",Halfs.class, Halfs.CODEC, Halfs::new);
		register("mainly",Mainly.class, Mainly.CODEC, Mainly::new);
		register("contains",Must.class, Must.CODEC, Must::new);
		register("mainlyOf",MainlyOfType.class, MainlyOfType.CODEC, MainlyOfType::new);
		register("only",Only.class, Only.CODEC, Only::new);
	}
	public static void register(String name, Deserializer<IngredientCondition> des) {
		numbers.register(name, des);
	}

	public static <R extends IngredientCondition> void register(String name,Class<R> cls, Codec<R> rjson,
			Function<FriendlyByteBuf, R> rpacket) {
		numbers.register(name,cls, rjson, rpacket);
	}

	public static IngredientCondition of(FriendlyByteBuf buffer) {
		return numbers.of(buffer);
	}

	public static void checkConditions(Collection<IngredientCondition> allow) {
		if(allow==null)return;
		boolean foundMajor=false;
		Set<Class<? extends IngredientCondition>> conts=new HashSet<>();
		
		for(IngredientCondition c:allow) {
			if(c.isMajor()) {
				if(foundMajor)
					throw new InvalidRecipeException("There must be less than one major condition. (Current: "+c.getType()+")");
				foundMajor=true;
			}else if(c.isExclusive()) {
				if(conts.contains(c.getClass()))
					throw new InvalidRecipeException("There must be less than one "+c.getType()+" condition.");
				conts.add(c.getClass());
			}
		}
	}

	public static void write(IngredientCondition e, FriendlyByteBuf buffer) {
		numbers.write(buffer, e);
	}

	public static void clearCache() {
		numbers.clearCache();
	}
}
