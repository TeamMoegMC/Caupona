package com.teammoeg.caupona.datagen;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.resources.ResourceManager;

public class CPModelProvider extends ModelProvider {
	ResourceManager resource;
	public CPModelProvider(PackOutput output, String modId,ResourceManager resource) {
		super(output, modId);
		this.resource=resource;
	}
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        new CPStatesProvider(resource, blockModels.blockStateOutput,blockModels.itemModelOutput,blockModels.modelOutput,modId).run();
    }
}
