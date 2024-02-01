package com.teammoeg.caupona.data.recipes.conditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.data.CachedDataDeserializer;
import com.teammoeg.caupona.data.Deserializer;
import com.teammoeg.caupona.data.InvalidRecipeException;
import com.teammoeg.caupona.data.recipes.IngredientCondition;

import net.minecraft.network.FriendlyByteBuf;

public class Conditions {
	private static CachedDataDeserializer<IngredientCondition,JsonObject> numbers=new CachedDataDeserializer<>();
	public static final MapCodec<IngredientCondition> CODEC=Codec.STRING.dispatchMap("type", t->t.getType(), t->numbers.getCodec(t));
	static {
		register("half", Halfs.CODEC, Halfs::new);
		register("mainly", Mainly.CODEC, Mainly::new);
		register("contains", Must.CODEC, Must::new);
		register("mainlyOf", MainlyOfType.CODEC, MainlyOfType::new);
		register("only", Only.CODEC, Only::new);
	}
	public static void register(String name, Deserializer<JsonObject, IngredientCondition> des) {
		numbers.register(name, des);
	}

	public static void register(String name, Codec<? extends IngredientCondition> rjson,
			Function<FriendlyByteBuf, IngredientCondition> rpacket) {
		numbers.register(name, rjson, rpacket);
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
		buffer.writeUtf(e.getType());
		e.write(buffer);
	}

	public static void clearCache() {
		numbers.clearCache();
	}
}
