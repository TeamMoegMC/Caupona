package com.teammoeg.caupona.util;

import java.util.HashMap;
import java.util.Map;

import com.mojang.serialization.Codec;

public class AutoSerializationUtil {
	public static final Map<Class<?>,Codec<?>> typedCodec=new HashMap<>();
	public static <T> Codec<T> registerCodecType(Class<T> cls,Codec<T> codec){
		typedCodec.put(cls, codec);
		return codec;
	};
	@SuppressWarnings("unchecked")
	public static <T> Codec<T> getAutoCodec(Class<T> cls){
		return (Codec<T>) typedCodec.computeIfAbsent(cls, AutoSerializationUtil::createAutoCodec);
		
	}
	private static <T> Codec<T> createAutoCodec(Class<T> cls){
		
		return null;
		
	}
}
