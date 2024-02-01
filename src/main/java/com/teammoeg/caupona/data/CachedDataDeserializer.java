package com.teammoeg.caupona.data;

import java.util.HashMap;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.teammoeg.caupona.util.CacheMap;

import net.minecraft.network.FriendlyByteBuf;

public class CachedDataDeserializer<T extends Writeable,U extends JsonElement> {
	private HashMap<String, Deserializer<U,T>> deserializers = new HashMap<>();
	private CacheMap<T> cache = new CacheMap<>();
	public void register(String name, Deserializer<U,T> des) {
		deserializers.put(name, des);
	}
	public void register(String name, Codec<? extends T> rjson,
			Function<FriendlyByteBuf, T> rpacket) {
		register(name, new Deserializer<>((Codec<T>)(Codec)rjson, rpacket));
	}
	public Deserializer<U,T> getDeserializer(String type){
		return deserializers.get(type);
	}
	public T of(FriendlyByteBuf buffer) {
		return cache.of(getDeserializer(buffer.readUtf()).read(buffer));
	}
	public void clearCache() {
		cache.clear();
	}
	public Codec<T> getCodec(String t) {
		Deserializer<U,T> des=getDeserializer(t);
		if(des==null)
			return null;
		return des.fromJson;
	}
	
}
