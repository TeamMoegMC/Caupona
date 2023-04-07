package com.teammoeg.caupona.client.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teammoeg.caupona.client.util.LayeredBakedModel.Builder;

import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public class LayeredElementsModel implements IUnbakedGeometry<LayeredElementsModel>
{

    private final Map<String,BlockElement> elements;
    private final Map<String,Set<Integer>> groups;
  
    public LayeredElementsModel(Map<String, BlockElement> elements, Map<String, Set<Integer>> groups) {
		super();
		this.elements = elements;
		this.groups = groups;
	}

	@Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));

        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
        Builder builder = new LayeredBakedModel.Builder(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(),
                context.getTransforms(), overrides).particle(particle);

        addQuads(context, builder, baker, spriteGetter, modelState, modelLocation);

        return builder.build(renderTypes);
    }

    protected void addQuads(IGeometryBakingContext context, Builder builder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation)
    {
        // If there is a root transform, undo the ModelState transform, apply it, then re-apply the ModelState transform.
        // This is necessary because of things like UV locking, which should only respond to the ModelState, and as such
        // that is the only transform that should be applied during face bake.
        var postTransform = context.getRootTransform().isIdentity() ? QuadTransformers.empty() :
                QuadTransformers.applying(modelState.getRotation().compose(context.getRootTransform()).compose(modelState.getRotation().inverse()));
        int no=0;
        for (BlockElement element : elements.values())
        {
        	String groupName="root";
        	for(Entry<String, Set<Integer>> i:groups.entrySet()) {
        		if(i.getValue().contains(no)) {
        			groupName=i.getKey();
        			break;
        		}
        	}
            for (Direction direction : element.faces.keySet())
            {
                var face = element.faces.get(direction);
                var sprite = spriteGetter.apply(context.getMaterial(face.texture));
                var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState, modelLocation);
                postTransform.processInPlace(quad);
                builder.addUnculledFace(quad,groupName);
            }
            no++;
        }
    }

    public static final class Loader implements IGeometryLoader<LayeredElementsModel>
    {
        public Loader(){
        }

        @Override
        public LayeredElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            if (!jsonObject.has("elements"))
                throw new JsonParseException("An element model must have an \"elements\" member.");

            Map<String,BlockElement> elements = new LinkedHashMap<>();
            Map<String,Set<Integer>> groups=new LinkedHashMap<>();
            int i=0;
            for (JsonElement group : GsonHelper.getAsJsonArray(jsonObject, "groups"))
            {
            	
            	if(group.isJsonObject()) {
            		String name="group_"+(i++);
            		JsonObject g=group.getAsJsonObject();
            		name=GsonHelper.getAsString(g,"name",name);
            		Set<Integer> set=new HashSet<>();
            		GsonHelper.getAsJsonArray(jsonObject,"children").forEach(e->{
            			set.add(e.getAsInt());
            		});
            		groups.put(name, set);
            	}
            }
            i=0;
            for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements"))
            {
            	String name="layer_"+(i++);
            	if(element.isJsonObject()) {
            		name=GsonHelper.getAsString(element.getAsJsonObject(),"name",name);
            	}
                elements.put(name,deserializationContext.deserialize(element, BlockElement.class));
            }

            return new LayeredElementsModel(elements,groups);
        }
    }
}