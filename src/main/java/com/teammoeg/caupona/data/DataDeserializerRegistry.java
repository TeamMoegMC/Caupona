package com.teammoeg.caupona.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.teammoeg.caupona.util.CacheMap;

import net.minecraft.network.FriendlyByteBuf;

public class DataDeserializerRegistry<T extends Writeable> {
	private HashMap<String, Deserializer<T>> deserializers = new HashMap<>();
	private List<Deserializer<T>> byIdx=new ArrayList<>();
	private HashMap<Class<?>,String> nameOfClass=new HashMap<>();
	public void register(String name, Deserializer<T> des) {
		deserializers.put(name, des);
	}
	public <R extends T> void register(String name,Class<R> cls, Codec<? extends R> rjson,
			Function<FriendlyByteBuf, R> rpacket) {
		Deserializer<T> des=new Deserializer<T>((Codec<T>)(Codec)rjson, (Function<FriendlyByteBuf, T>)(Function)rpacket,byIdx.size());
		register(name, des);
		byIdx.add(des);
		nameOfClass.put(cls, name);
	}
	public Deserializer<T> getDeserializer(String type){
		return deserializers.get(type);
	}
	public T of(FriendlyByteBuf buffer) {
		return byIdx.get(buffer.readByte()).read(buffer);
	}
	public void clearCache() {
	}
	public void write(FriendlyByteBuf buffer,T obj) {
		deserializers.get(nameOfClass.get(obj.getClass())).write(buffer, obj);
	}
	public Codec<T> getCodec(String t) {
		Deserializer<T> des=getDeserializer(t);
		if(des==null)
			return null;
		return des.fromJson;
	}
	public Codec<T> createCodec(){
		return Codec.STRING.dispatch("type", t->t==null?null:nameOfClass.get(t.getClass()), t->getCodec(t));
	}
	
}
