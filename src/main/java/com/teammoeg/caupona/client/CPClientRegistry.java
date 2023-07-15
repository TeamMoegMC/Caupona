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
 * Specially, we allow this software to be used alongside with closed source software Minecraft(R) and Forge or other modloader.
 * Any mods or plugins can also use apis provided by forge or com.teammoeg.caupona.api without using GPL or open source.
 *
 * You should have received a copy of the GNU General Public License
 * along with Caupona. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.caupona.client;

import com.teammoeg.caupona.CPBlockEntityTypes;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPEntityTypes;
import com.teammoeg.caupona.CPGui;
import com.teammoeg.caupona.CPMain;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CPMain.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CPClientRegistry {
	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		LayerDefinition layer = BoatModel.createBodyModel(false);
		for (String wood : CPBlocks.woods)
			ForgeHooksClient.registerLayerDefinition(
					new ModelLayerLocation(new ResourceLocation(CPMain.MODID, "boat/" + wood), "main"), () -> layer);
		MenuScreens.register(CPGui.STEWPOT.get(), StewPotScreen::new);
		MenuScreens.register(CPGui.STOVE.get(), KitchenStoveScreen::new);
		MenuScreens.register(CPGui.DOLIUM.get(), DoliumScreen::new);
		MenuScreens.register(CPGui.BRAZIER.get(), PortableBrazierScreen::new);
		MenuScreens.register(CPGui.PAN.get(), PanScreen::new);

		/*ItemBlockRenderTypes.setRenderLayer(CPBlocks.stew_pot, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove1, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove2, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove3, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove4, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.stove5, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.bowl, RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(CPBlocks.GRAVY_BOAT, RenderType.translucent());*/

		BlockEntityRenderers.register(CPBlockEntityTypes.STEW_POT.get(), StewPotRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.BOWL.get(), BowlRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.SIGN.get(), SignRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.DOLIUM.get(), CounterDoliumRenderer::new);
		BlockEntityRenderers.register(CPBlockEntityTypes.PAN.get(), PanRenderer::new);
		Sheets.addWoodType(CPBlocks.WALNUT);
		EntityRenderers.register(CPEntityTypes.BOAT.get(), c->new CPBoatRenderer(c,false));

	}

	@SuppressWarnings({ "unused", "resource" })
	@SubscribeEvent
	public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
		event.register(Particles.STEAM.get(), SteamParticle.Factory::new);
		event.register(Particles.SOOT.get(), SootParticle.Factory::new);
	}

	@SubscribeEvent
	public static void onTint(RegisterColorHandlersEvent.Block ev) {
		ev.register((p_92626_, p_92627_, p_92628_, p_92629_) -> {
			return p_92627_ != null && p_92628_ != null ? BiomeColors.getAverageFoliageColor(p_92627_, p_92628_)
					: FoliageColor.getDefaultColor();
		}, CPBlocks.WALNUT_LEAVE.get(), CPBlocks.FIG_LEAVE.get(), CPBlocks.WOLFBERRY_LEAVE.get());
	}

	@SubscribeEvent
	public static void onTint(RegisterColorHandlersEvent.Item ev) {
		ev.register((i, t) -> 0x5bd449, CPBlocks.WALNUT_LEAVE.get(), CPBlocks.FIG_LEAVE.get(),
				CPBlocks.WOLFBERRY_LEAVE.get());
	}
}