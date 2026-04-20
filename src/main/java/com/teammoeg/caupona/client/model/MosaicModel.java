package com.teammoeg.caupona.client.model;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.MosaicData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class MosaicModel implements ItemModel {
	public static final MosaicModel INSTANCE=new MosaicModel();
	@Override
	public void update(ItemStackRenderState output, ItemStack item, ItemModelResolver resolver,
			ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
		@Nullable MosaicData tag=item.get(CPCapability.MOSAIC_DATA);
		if(tag==null)
			return;
		
		BlockState bs=tag.createBlock();
		BlockStateModelSet rd = Minecraft.getInstance().getModelManager().getBlockStateModelSet();

        output.appendModelIdentityElement(this);
        ItemStackRenderState.LayerRenderState layer = output.newLayer();

		List<BlockStateModelPart> parts=new ArrayList<>();
		rd.get(bs).collectParts(level, BlockPos.ZERO, bs, level.getRandom(), parts);
		for(BlockStateModelPart a:parts) {
			layer.prepareQuadList().addAll(a.getQuads(null));
		}

	}
    public record Unbaked() implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new MosaicModel.Unbaked());

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
            return MosaicModel.INSTANCE;
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
        }
    }
}
