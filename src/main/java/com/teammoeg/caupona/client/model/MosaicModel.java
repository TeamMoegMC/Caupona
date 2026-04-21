package com.teammoeg.caupona.client.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPCapability;
import com.teammoeg.caupona.components.MosaicData;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public record MosaicModel(Matrix4fc transform,ModelRenderProperties properties) implements ItemModel {
	  public static Vector3fc[] computeExtents(List<BakedQuad> quads) {
	        Set<Vector3fc> result = new HashSet<>();

	        for (BakedQuad quad : quads) {
	            for (int vertex = 0; vertex < 4; vertex++) {
	                result.add(quad.position(vertex));
	            }
	        }

	        return result.toArray(Vector3fc[]::new);
	    }
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
		output.appendModelIdentityElement(tag);
		BlockStateModel bsm=rd.get(bs);
		bsm.collectParts(level, BlockPos.ZERO, bs, level.getRandom(), parts);
		List<BakedQuad> quads=new ArrayList<>();
		for(BlockStateModelPart a:parts) {
			quads.addAll(a.getQuads(null));
		}
		layer.setUsesBlockLight(true);
		layer.setExtents(()->computeExtents(quads));
		layer.setLocalTransform(transform());
		properties.applyToLayer(layer, displayContext);
		layer.prepareQuadList().addAll(quads);
		

	}
    public record Unbaked(Optional<Identifier> parent,Optional<Transformation> transformation) implements ItemModel.Unbaked {
        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(t->
        	t.group(
        			Identifier.CODEC.optionalFieldOf("parent").forGetter(Unbaked::parent),
                    Transformation.EXTENDED_CODEC.optionalFieldOf("transformation").forGetter(Unbaked::transformation)
               
        		).apply(t,Unbaked::new));
        		
        public static final Unbaked INSTANCE=new Unbaked(Optional.empty(),Optional.empty());
        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
        	ResolvedModel parentModel=context.blockModelBaker().getModel(parent.orElseGet(()->Identifier.withDefaultNamespace("block/block")));
            return new MosaicModel(Transformation.compose(transformation, this.transformation),ModelRenderProperties.fromResolvedModel(context.blockModelBaker(), parentModel, parentModel.getTopTextureSlots()));
        }

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {

        	parent.ifPresent(resolver::markDependency);
        }
    }
}
