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

package com.teammoeg.caupona.client.util;

import org.joml.Matrix3x2f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.gui.TiledBlitRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Fluid render codes modified.
 * 
 * @author khjxiaogu
 */
public class FluidRenderHelper {

	private FluidRenderHelper() {
	}


	public static Quaternionf rotate90=new Quaternionf().rotateX((float) (Math.PI/2));
	public static int getFluidColor(FluidModel model,FluidStack stack) {
		int color = 0xffffffff;
		if(model.fluidTintSource() != null)
			color=model.fluidTintSource().colorAsStack(stack);
		return color;
	}
	public static FluidModel getFluidModel(FluidStack stack) {
		return Minecraft.getInstance().getModelManager().getFluidStateModelSet()
			.get(stack.getFluid().defaultFluidState());
	}
	public static void handleGuiTank(GuiGraphicsExtractor transform, ResourceHandler<FluidResource> tank, int x, int y, int w, int h) {
		FluidStack fluid = tank.getResource(0).toStack(tank.getAmountAsInt(0));
		if (fluid != null && fluid.getFluid() != null) {
			int fluidHeight = (int) (h * (tank.getAmountAsInt(0) / (float) tank.getCapacityAsInt(0,tank.getResource(0))));
			FluidModel model = FluidRenderHelper.getFluidModel(fluid);
			int color = FluidRenderHelper.getFluidColor(model, fluid);
			TextureAtlasSprite sprite=model.stillMaterial().sprite();
			AbstractTexture spriteTexture = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation());
			GpuTextureView texture = spriteTexture.getTextureView();
			transform.submitGuiElementRenderState(new TiledBlitRenderState(
				RenderPipelines.GUI_TEXTURED,
                    TextureSetup.singleTexture(texture, spriteTexture.getSampler()),
                    new Matrix3x2f(transform.pose()),
                    16,16,
                    x,y+h-fluidHeight,x+w,y+h,
                    sprite.getU0(),sprite.getU1(),
                    sprite.getV0(),sprite.getV1(),
                    color,
                    transform.peekScissorStack()
                ));
			
		}
	}
	public static void submitColoredTexturedRect(SubmitNodeCollector buffer,PoseStack poseStack,TextureAtlasSprite sprite,float x0,float z0,float x1,float z1,int color,int packedLight,int packedOverlay) {
		buffer.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (matrixStack, builder) -> {
			FluidRenderHelper.drawTexturedColoredRect(builder, matrixStack, x0, z0, x1, z1,
					color,
					sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), packedLight,
					packedOverlay);
		});
	}

	private static void buildVertex(VertexConsumer bu, Pose transform, int color,
			float p1, float p2, float u0, float u1, int light, int overlay) {
		bu.addVertex(transform, p1, p2, 0).setColor(color).setUv(u0, u1).setOverlay(overlay).setLight(light)
				.setNormal(1f, 1f, 1f);
	}



	public static void drawRepeatedSprite(VertexConsumer builder, Pose transform, float x, float y, float w,
			float h, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax, int color, int light, int overlay) {
		int iterMaxW = (int) (w / iconWidth);
		int iterMaxH = (int) (h / iconHeight);
		float leftoverW = w % iconWidth;
		float leftoverH = h % iconHeight;
		float leftoverWf = leftoverW / iconWidth;
		float leftoverHf = leftoverH / iconHeight;
		float iconUDif = uMax - uMin;
		float iconVDif = vMax - vMin;
		for (int ww = 0; ww < iterMaxW; ww++) {
			for (int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x + ww * iconWidth, y + hh * iconHeight, iconWidth,
						iconHeight, color, uMin, uMax, vMin, vMax, light, overlay);
			drawTexturedColoredRect(builder, transform, x + ww * iconWidth, y + iterMaxH * iconHeight, iconWidth,
					leftoverH, color, uMin, uMax, vMin, (vMin + iconVDif * leftoverHf), light, overlay);
		}
		if (leftoverW > 0) {
			for (int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + hh * iconHeight, leftoverW,
						iconHeight, color, uMin, (uMin + iconUDif * leftoverWf), vMin, vMax, light, overlay);
			drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + iterMaxH * iconHeight, leftoverW,
					leftoverH, color, uMin, (uMin + iconUDif * leftoverWf), vMin,
					(vMin + iconVDif * leftoverHf), light, overlay);
		}
	}

	public static void drawTexturedColoredRect(VertexConsumer builder, Pose transform, float x, float y, float w,
			float h, int color, float u0, float u1, float v0, float v1, int light,
			int overlay) {
		buildVertex(builder, transform, color, x, y + h, u0, v1, light, overlay);
		buildVertex(builder, transform, color, x + w, y + h, u1, v1, light, overlay);
		buildVertex(builder, transform, color, x + w, y, u1, v0, light, overlay);
		buildVertex(builder, transform, color, x, y, u0, v0, light, overlay);
	}
}
