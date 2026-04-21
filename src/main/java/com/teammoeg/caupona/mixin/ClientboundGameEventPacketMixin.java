package com.teammoeg.caupona.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;

@Mixin(ClientboundGameEventPacket.class)
public class ClientboundGameEventPacketMixin {
	@Shadow
	private ClientboundGameEventPacket.Type event;
	@Inject(at=@At("TAIL"),method="<init>(Lnet/minecraft/network/protocol/game/ClientboundGameEventPacket$Type;F)V")
    public void onInit(ClientboundGameEventPacket.Type p_event, float p_param,CallbackInfo cbi) {
		try {
			Field id=ClientboundGameEventPacket.Type.class.getDeclaredField("id");
			id.setAccessible(true);
			System.out.println("network created:"+id.getInt(p_event)+","+p_param);
		}catch(Throwable err) {
			err.printStackTrace();
		}
    }
	@Inject(at=@At("TAIL"),method="Lnet/minecraft/network/protocol/game/ClientboundGameEventPacket;<init>(Lnet/minecraft/network/FriendlyByteBuf;)V")
    public void onInit(FriendlyByteBuf p_param,CallbackInfo cbi) {
		try {
			Field id=ClientboundGameEventPacket.Type.class.getDeclaredField("id");
			id.setAccessible(true);
			System.out.println("network read:"+id.getInt(event)+","+p_param);
		}catch(Throwable err) {
			err.printStackTrace();
		}
    }
	@Overwrite
    public void handle(ClientGamePacketListener handler) throws Throwable {
		try {
			Field id=ClientboundGameEventPacket.Type.class.getDeclaredField("id");
			id.setAccessible(true);
			System.out.println("network stage:"+id.getInt(event));
			handler.handleGameEvent((ClientboundGameEventPacket)(Object)this);
		}catch(Throwable err) {
			err.printStackTrace();
			throw err;
		}
		
    }

}
