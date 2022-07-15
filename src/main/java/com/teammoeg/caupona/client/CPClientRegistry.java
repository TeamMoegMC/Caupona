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
import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.CPTileTypes;
import com.teammoeg.caupona.Main;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CPClientRegistry {
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		MenuScreens.register(CPGui.STEWPOT.get(), StewPotScreen::new);
		MenuScreens.register(CPGui.STOVE.get(),KitchenStoveScreen::new);
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stew_pot, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove1, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove2, RenderType.cutout());
		BlockEntityRenderers.register(CPTileTypes.STEW_POT.get(), StewPotRenderer::new);
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerParticleFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particleEngine.register(Particles.STEAM.get(), SteamParticle.Factory::new);
	}
}