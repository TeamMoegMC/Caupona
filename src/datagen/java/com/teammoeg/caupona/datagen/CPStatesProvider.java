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

package com.teammoeg.caupona.datagen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableList;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPItems;
import com.teammoeg.caupona.blocks.KitchenStove;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Vec3i;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder;
import net.minecraftforge.client.model.generators.MultiPartBlockStateBuilder.PartBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CPStatesProvider extends BlockStateProvider {
    protected static final List<Vec3i> COLUMN_THREE = ImmutableList.of(BlockPos.ZERO, BlockPos.ZERO.above(), BlockPos.ZERO.above(2));

    protected static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;
    String modid;
    public CPStatesProvider(DataGenerator gen,String modid,ExistingFileHelper exFileHelper)
    {
        super(gen,modid, exFileHelper);
        this.modid=modid;
        this.existingFileHelper = exFileHelper;
    }

	@Override
	protected void registerStatesAndModels() {
		horizontalAxisBlock(CPBlocks.stew_pot,bmf("stew_pot"));
		
		stove(CPBlocks.stove1,"mud_kitchen_stove");
		stove(CPBlocks.stove2,"brick_kitchen_stove");
		itemModel(CPBlocks.stew_pot,bmf("stew_pot"));
		
	}
	public void stove(Block block,String mainmodel) {
		horizontalMultipart(horizontalMultipart(horizontalMultipart(this.getMultipartBuilder(block),bmf(mainmodel))
				.part().modelFile(bmf("kitchen_stove_cold_ash")).addModel().condition(KitchenStove.LIT,false).condition(KitchenStove.ASH,true).end()
				.part().modelFile(bmf("kitchen_stove_hot_ash")).addModel().condition(KitchenStove.LIT,true).end()
				,bmf("kitchen_stove_charcoal"),i->i.condition(KitchenStove.FUELED,1))
				,bmf("kitchen_stove_firewoods"),i->i.condition(KitchenStove.FUELED,2));
		itemModel(block,bmf(mainmodel));
	}
    public ModelFile bmf(String name) {
    	return new ModelFile.ExistingModelFile(new ResourceLocation(this.modid,"block/"+name),existingFileHelper);
    }
    public void simpleBlockItem(Block b, ModelFile model)
    {
        simpleBlockItem(b, new ConfiguredModel(model));
    }

    protected void simpleBlockItem(Block b, ConfiguredModel model)
    {
        simpleBlock(b, model);
        itemModel(b, model.model);
    }
    public void horizontalAxisBlock(Block block, ModelFile mf) {
        getVariantBuilder(block)
            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z)
                .modelForState().modelFile(mf).addModel()
            .partialState().with(BlockStateProperties.HORIZONTAL_AXIS, Axis.X)
                .modelForState().modelFile(mf).rotationY(90).addModel();
    }
    public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block,ModelFile mf){
    	return horizontalMultipart(block,mf,UnaryOperator.identity());
    }
    public MultiPartBlockStateBuilder horizontalMultipart(MultiPartBlockStateBuilder block,ModelFile mf,UnaryOperator<PartBuilder> act) {
    	for(Direction d:BlockStateProperties.HORIZONTAL_FACING.getPossibleValues())
    		block=act.apply(block.part().modelFile(mf)
            .rotationY(((int) d.toYRot()) % 360)
            .addModel().condition(BlockStateProperties.HORIZONTAL_FACING,d)).end();
    	return block;
    }
    protected void itemModel(Block block, ModelFile model)
    {
        itemModels().getBuilder(block.getRegistryName().getPath()).parent(model);
    }
}
