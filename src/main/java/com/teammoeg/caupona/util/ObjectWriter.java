package com.teammoeg.caupona.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public class ObjectWriter {
	private static class TypedValue{
		int type;
		Object value;
		public TypedValue(int type, Object value) {
			super();
			this.type = type;
			this.value = value;
		}
		public TypedValue(int type) {
			super();
			this.type = type;
		}
	}
	public ObjectWriter() {
	}
	public static TypedValue getTyped(Object input) {
		if(input instanceof Byte) {
			return new TypedValue(1,input);
		}else if(input instanceof Short) {
			return new TypedValue(2,input);
		}else if(input instanceof Integer) {
			return new TypedValue(3,input);
		}else if(input instanceof Long) {
			return new TypedValue(4,input);
		}else if(input instanceof Float) {
			return new TypedValue(5,input);
		}else if(input instanceof Double) {
			return new TypedValue(6,input);
		}else if(input instanceof String) {
			return new TypedValue(7,input);
		}else if(input instanceof Map) {
			return new TypedValue(8,input);
		}else if(input instanceof List) {
			Class<?> cls=DataOps.getElmClass(((List<Object>)input));
			if(cls==Byte.class) {
				return new TypedValue(9,input);
			}else if(cls==Integer.class) {
				return new TypedValue(10,input);
			}else if(cls==Long.class) {
				return new TypedValue(11,input);
			}else if(cls==String.class) {
				return new TypedValue(12,input);
			}else if(cls==Map.class) {
				return new TypedValue(13,input);
			}else {
				return new TypedValue(14,input);
			}
		}else {
			return new TypedValue(0,input);
		}
	}
	public static void writeTyped(FriendlyByteBuf pb,TypedValue input) {
		switch(input.type) {
		case 1:pb.writeByte((Byte)input.value);break;
		case 2:pb.writeShort((Short) input.value);break;
		case 3:pb.writeVarInt((Integer) input.value);break;
		case 4:pb.writeLong((Long) input.value);break;
		case 5:pb.writeFloat((Float) input.value);break;
		case 6:pb.writeDouble((Double) input.value);break;
		case 7:pb.writeUtf((String) input.value);break;
		case 8:writeEntry(pb, ((Map<Object,Object>)input.value),(t,p)->{
			TypedValue key   = getTyped(t.getKey());
			TypedValue value = getTyped(t.getValue());
			pb.writeByte((key.type<<4)+value.type);
			writeTyped(pb,key);
			writeTyped(pb,value);
		});break;
		case 9:byte[] bs=DataOps.INSTANCE.getByteArray(input).result().get();
		pb.writeByteArray(bs);break;
		case 10:writeList(pb, ((List<Integer>)input.value), (t,p)->p.writeVarInt(t));break;
		case 11:writeList(pb, ((List<Long>)input.value), (t,p)->p.writeLong(t));break;
		case 12:writeList(pb, ((List<String>)input.value), (t,p)->p.writeUtf(t));break;
		case 13:writeList(pb, ((List<Map>)input.value), (t,p)->writeTyped(p,new TypedValue(8,t)));break;
		case 14:{
			List<Object> obj=(List<Object>) input.value;
			List<TypedValue> typed=obj.stream().map(o->getTyped(o)).collect(Collectors.toList());
			pb.writeVarInt(typed.size());
			
			if(typed.size()%2==1)
				typed.add(new TypedValue(0));
			for(int i=0;i<(typed.size())/2;i++) {
	        	pb.writeByte((typed.get(i*2).type<<4)+typed.get(i*2+1).type);
	        }
	        typed.forEach(t->writeTyped(pb,t));
		}break;
		}
	}
    public static Object readWithType(int type,FriendlyByteBuf pb) {
    	switch(type) {
    	case 1:return pb.readByte();
    	case 2:return pb.readShort();
    	case 3:return pb.readVarInt();
    	case 4:return pb.readLong();
    	case 5:return pb.readFloat();
    	case 6:return pb.readDouble();
    	case 7:return pb.readUtf();
    	case 8:return readEntry(pb, new HashMap<>(),(p,c)->{
    		int byt=pb.readByte();
    		Object key=readWithType((byt>>4)&15,pb);
    		Object value=readWithType(byt&15,pb);
    		c.accept(key, value);
    	})
    	;
    	case 9:return pb.readByteArray();
    	case 10:return readList(pb, FriendlyByteBuf::readVarInt);
    	case 11:return readList(pb, FriendlyByteBuf::readLong);
    	case 12:return readList(pb, FriendlyByteBuf::readUtf);
    	case 13:return readList(pb, p->readWithType(8,p));
    	case 14:{
			List<Object> obj=new ArrayList<>();
			int size=pb.readVarInt();
			ByteBuf crnbytes=pb.readBytes((size+1)/2);
			for(int i=0;i<size;i++) {
				if(i%2==1) {
					obj.add(readWithType(crnbytes.getByte(i/2)&15,pb));
				}else {
					obj.add(readWithType(crnbytes.getByte(i/2)>>4,pb));
				}
			}
			return obj;
		}
    	}
    	return null;
    }
    public static void writeObject(FriendlyByteBuf pb,Object input) {
    	TypedValue value=getTyped(input);
    	pb.writeByte(value.type);
    	writeTyped(pb,value);
    }
    public static Object readObject(FriendlyByteBuf pb) {
    	return readWithType(pb.readByte(),pb);
    }
	public static <T> List<T> readList(FriendlyByteBuf buffer, Function<FriendlyByteBuf, T> func) {
		int cnt = buffer.readVarInt();
		List<T> nums = new ArrayList<>(cnt);
		for (int i = 0; i < cnt; i++)
			nums.add(func.apply(buffer));
		return nums;
	}

	public static <T> void writeList(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<T, FriendlyByteBuf> func) {
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(e, buffer));
	}

	public static <T> void writeList2(FriendlyByteBuf buffer, Collection<T> elms, BiConsumer<FriendlyByteBuf, T> func) {
		buffer.writeVarInt(elms.size());
		elms.forEach(e -> func.accept(buffer, e));
	}
    public static <K, V> Map<K, V> readEntry(FriendlyByteBuf buffer, Map<K, V> map, BiConsumer<FriendlyByteBuf, BiConsumer<K,V>> reader) {
        map.clear();
        int cnt = buffer.readVarInt();
        for (int i = 0; i < cnt; i++)
        	reader.accept(buffer, map::put);
        return map;
    }
    public static <K,V> void writeEntry(FriendlyByteBuf buffer, Map<K,V> elms, BiConsumer<Map.Entry<K,V>, FriendlyByteBuf> func) {
        buffer.writeVarInt(elms.size());
        elms.entrySet().forEach(e -> func.accept(e, buffer));
    }
}
