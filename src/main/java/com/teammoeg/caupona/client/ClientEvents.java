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

package com.teammoeg.caupona.client;


import com.teammoeg.caupona.CPMain;
import com.teammoeg.caupona.client.util.DynamicBlockModelReference;
import com.teammoeg.caupona.data.RecipeReloadListener;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;

@EventBusSubscriber(modid = CPMain.MODID, value = Dist.CLIENT)
public class ClientEvents {
	@SubscribeEvent
	public static void registerModels(ModelEvent.RegisterStandalone ev)
	{
		Minecraft.getInstance().getResourceManager().listResources("models/block/dynamic",e->e.getPath().endsWith(".json")).keySet().forEach(rl->{
			//remove models/ and .json
			String name=rl.getPath().substring(0,rl.getPath().lastIndexOf(".")).substring(7);
			
			ev.register(DynamicBlockModelReference.createKey(Identifier.fromNamespaceAndPath(rl.getNamespace(), name)).name(),SimpleUnbakedStandaloneModel.quadCollection(CPMain.rl(name)));
					
					
					
		});
	}
	@SubscribeEvent
	public static void onRecipeSynced(RecipesReceivedEvent ev) {
		RecipeReloadListener.collectRecipeIndices(ev.getRecipeMap());
	}
}
