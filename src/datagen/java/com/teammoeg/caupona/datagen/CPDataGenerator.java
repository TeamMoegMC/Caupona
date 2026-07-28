/*
 * Copyright (c) 2024 TeamMoeg
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

package com.teammoeg.caupona.datagen;

import java.util.concurrent.CompletableFuture;

import com.teammoeg.caupona.CPMain;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.Util;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CPMain.MODID)
public class CPDataGenerator {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Server event) {
		System.out.println("Gather server data");
		DataGenerator gen = event.getGenerator();

		
		CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
		gen.addProvider(true,new CPItemTagGenerator(gen, CPMain.MODID,event.getLookupProvider()));
		gen.addProvider(true,new CPBlockTagGenerator(gen, CPMain.MODID,event.getLookupProvider()));
		gen.addProvider(true,new CPFluidTagGenerator(gen, CPMain.MODID,event.getLookupProvider()));
		gen.addProvider(true,new CPGlobalLootModifiersGenerator(gen.getPackOutput(),completablefuture,CPMain.MODNAME+" global_modifiers"));
		gen.addProvider(true,new CPLootGenerator(gen,completablefuture));
		/*gen.addProvider(true||true,new PackMetadataGenerator(gen.getPackOutput()).add(PackMetadataSection.TYPE,new PackMetadataSection(MutableComponent.create(new TranslatableContents("pack.caupona.title",CPMain.MODNAME+" Data",new Object[0])),
            DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
            Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));*/
		gen.addProvider(true,new CPRegistryGenerator(gen.getPackOutput(),completablefuture));
		gen.addProvider(true, new RegistryJavaGenerator(gen.getPackOutput(),event.getResourceManager(PackType.CLIENT_RESOURCES)));
		gen.addProvider(true,new CPRecipeProvider.Runner(gen.getPackOutput(),event.getLookupProvider(),CPMain.MODID));
		gen.addProvider(true,new CPBookGenerator(gen.getPackOutput(), event.getResourceManager(PackType.CLIENT_RESOURCES)));
		
	}
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		System.out.println("Gather client data");
		DataGenerator gen = event.getGenerator();
		@SuppressWarnings("unused")
		CompletableFuture<HolderLookup.Provider> completablefuture = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor());
		gen.addProvider(true,new CPModelProvider(gen.getPackOutput(), CPMain.MODID,event.getResourceManager(PackType.CLIENT_RESOURCES)));
		
		/*gen.addProvider(true||true,new PackMetadataGenerator(gen.getPackOutput()).add(PackMetadataSection.TYPE,new PackMetadataSection(MutableComponent.create(new TranslatableContents("pack.caupona.title",CPMain.MODNAME+" Data",new Object[0])),
            DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA),
            Optional.of(new InclusiveRange<>(0, Integer.MAX_VALUE)))));*/
		gen.addProvider(true,new FluidAnimationGenerator(gen.getPackOutput(),event.getResourceManager(PackType.CLIENT_RESOURCES)));
		
	}
}
