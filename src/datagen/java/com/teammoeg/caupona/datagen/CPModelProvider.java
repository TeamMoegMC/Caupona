package com.teammoeg.caupona.datagen;

import java.util.stream.Stream;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class CPModelProvider extends ModelProvider {
	ResourceManager resource;
	public CPModelProvider(PackOutput output, String modId,ResourceManager resource) {
		super(output, modId);
		this.resource=resource;
	}
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        new CPStatesProvider(resource, blockModels.blockStateOutput,blockModels.itemModelOutput,blockModels.modelOutput,modId).run();
        new CPItemModelProvider(blockModels.itemModelOutput,blockModels.modelOutput).run();
    
    }
    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Block>> getKnownBlocks() {
        return CPBlocks.BLOCKS.getEntries().stream().filter(t->!resource.getResource(t.getId().withPrefix("blockstates/").withSuffix(".json")).isPresent());
    }

    protected java.util.stream.Stream<? extends net.minecraft.core.Holder<Item>> getKnownItems() {
    	return CPItems.ITEMS.getEntries().stream();
    }
}
