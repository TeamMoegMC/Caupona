package com.teammoeg.caupona.client;


import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.util.LayeredElementsModel;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = CPMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void registerModels(ModelEvent.RegisterAdditional ev)
	{
		Minecraft.getInstance().getResourceManager().listResources("models/block/dynamic",e->e.getPath().endsWith(".json")).keySet().forEach(ev::register);
	}
	@SubscribeEvent
	public static void registerLoaders(ModelEvent.RegisterGeometryLoaders ev)
	{
		ev.register("layered",new LayeredElementsModel.Loader());
	}
	
}
