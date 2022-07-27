package com.teammoeg.caupona.client;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.Main;
import com.teammoeg.caupona.entity.CPBoat;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
@OnlyIn(Dist.CLIENT)
public class CPBoatRenderer extends BoatRenderer {
	private final Map<String, Pair<ResourceLocation, BoatModel>> boatResources;
	
    public CPBoatRenderer(Context p_173936_) {
		super(p_173936_);
		boatResources=Stream.of(CPBlocks.woods).collect(ImmutableMap.toImmutableMap(
				s->s,
				s->Pair.of(new ResourceLocation(Main.MODID,"textures/entity/boat/"+s+".png"),
						new BoatModel(p_173936_.bakeLayer(
								new ModelLayerLocation(new ResourceLocation(Main.MODID,"boat/" + s), "main")))
				)));
		
		
	}

	@Override
	public Pair<ResourceLocation, BoatModel> getModelWithLocation(Boat boat) {
		if(boat instanceof CPBoat) {
			return boatResources.get(((CPBoat) boat).getWoodType());
		}
		return super.getModelWithLocation(boat);
	}





}