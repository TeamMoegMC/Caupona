package com.teammoeg.caupona.client.util;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraftforge.client.model.data.ModelData;

public class ModelUtils {

	public static void renderQuads(List<BakedQuad> quads, VertexConsumer renderer, PoseStack transform,
			int color, int light, int overlay) {
		float red = 1;
		float green = 1;
		float blue = 1;
		if (color >= 0) {
			red = (color >> 16 & 255) / 255F;
			green = (color >> 8 & 255) / 255F;
			blue = (color & 255) / 255F;
		}
		for (BakedQuad quad : quads)
			renderer.putBulkData(transform.last(), quad, red, green, blue, light, overlay);
	}
	public static DynamicBlockModelReference getModel(String name) {
		return new DynamicBlockModelReference(name);
	}
	public static void renderModelGroups(DynamicBlockModelReference model, VertexConsumer renderer,ImmutableSet<String> groups,PoseStack transform,
			int color, int light, int overlay) {
		renderQuads(model.apply(ModelData.builder().with(DisplayGroupProperty.PROPERTY,groups).build()),renderer,transform,color,light,overlay);
	}
	public static void renderModel(DynamicBlockModelReference model, VertexConsumer renderer, PoseStack transform,
			int color, int light, int overlay) {
		renderQuads(model.getAllQuads(),renderer,transform,color,light,overlay);
	}
	public static void renderModelGroups(DynamicBlockModelReference model, VertexConsumer renderer,ImmutableSet<String> groups,PoseStack transform, int light, int overlay) {
		renderModelGroups(model, renderer, groups, transform, 0, light, overlay);
	}
	public static void renderModel(DynamicBlockModelReference model, VertexConsumer renderer, PoseStack transform, int light, int overlay) {
		renderModel(model, renderer, transform, 0, light, overlay);
	}
}
