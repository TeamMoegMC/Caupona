/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Caupona.
 *
 * Caupona is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Caupona is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.client;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPEntityTypes;
import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CPClientRegistry {
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		LayerDefinition layer=BoatModel.createBodyModel();
		for(String wood:CPBlocks.woods)
		ForgeHooksClient.registerLayerDefinition(new ModelLayerLocation(new ResourceLocation(Main.MODID,"boat/" + wood), "main"),()->layer);
		MenuScreens.register(CPGui.STEWPOT.get(), StewPotScreen::new);
		MenuScreens.register(CPGui.STOVE.get(),KitchenStoveScreen::new);
		MenuScreens.register(CPGui.DOLIUM.get(), DoliumScreen::new);
		MenuScreens.register(CPGui.BRAZIER.get(), PortableBrazierScreen::new);
		MenuScreens.register(CPGui.PAN.get(), PanScreen::new);
		
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stew_pot, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove1, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove2, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove3, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove4, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove5, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.bowl, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.GRAVY_BOAT, RenderType.translucent());
		
		
		for(Block bl:CPBlocks.transparentBlocks)
			ItemBlockRenderTypes.setRenderLayer(bl, RenderType.cutout());
		BlockEntityRenderers.register(CPTileTypes.STEW_POT.get(), StewPotRenderer::new);
		BlockEntityRenderers.register(CPTileTypes.BOWL.get(), BowlRenderer::new);
		BlockEntityRenderers.register(CPTileTypes.SIGN.get(), SignRenderer::new);
		BlockEntityRenderers.register(CPTileTypes.DOLIUM.get(),CounterDoliumRenderer::new);
		BlockEntityRenderers.register(CPTileTypes.PAN.get(),PanRenderer::new);
		Sheets.addWoodType(CPBlocks.WALNUT);
		EntityRenderers.register(CPEntityTypes.BOAT.get(), CPBoatRenderer::new);
		
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(Particles.STEAM.get(), SteamParticle.Factory::new);
		Minecraft.getInstance().particleEngine.register(Particles.SOOT.get(), SootParticle.Factory::new);
	}
	@SubscribeEvent
	public static void onTint(ColorHandlerEvent.Block ev) {
		ev.getBlockColors().register((p_92626_, p_92627_, p_92628_, p_92629_) -> {
	         return p_92627_ != null && p_92628_ != null ? BiomeColors.getAverageFoliageColor(p_92627_, p_92628_) : FoliageColor.getDefaultColor();
	      },CPBlocks.WALNUT_LEAVE,CPBlocks.FIG_LEAVE,CPBlocks.WOLFBERRY_LEAVE);
	}
	@SubscribeEvent
	public static void onTint(ColorHandlerEvent.Item ev) {
		ev.getItemColors().register((i,t)->0x5bd449, CPBlocks.WALNUT_LEAVE,CPBlocks.FIG_LEAVE,CPBlocks.WOLFBERRY_LEAVE);
	}
}