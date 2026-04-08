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
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.state.gui.TiledBlitRenderState;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

/**
 * Fluid render codes adapted from Immersive Engineering and modified.
 * Related codes fall under their license and open-sourced.
 * 
 * @author BluSunrize
 * @author khjxiaogu
 */
public class GuiUtils {

	private GuiUtils() {
	}


	public static Quaternionf rotate90=new Quaternionf().rotateX((float) (Math.PI/2));
	public static void handleGuiTank(GuiGraphicsExtractor transform, ResourceHandler<FluidResource> tank, int x, int y, int w, int h) {
		FluidStack fluid = tank.getResource(0).toStack(tank.getAmountAsInt(0));
		if (fluid != null && fluid.getFluid() != null) {
			int fluidHeight = (int) (h * (tank.getAmountAsInt(0) / (float) tank.getAmountAsLong(0)));
			FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet()
				.get(fluid.getFluid().defaultFluidState());
			int color = model.fluidTintSource().colorAsStack(fluid);
			TextureAtlasSprite sprite=model.stillMaterial().sprite();
			AbstractTexture spriteTexture = Minecraft.getInstance().getTextureManager().getTexture(sprite.atlasLocation());
			GpuTextureView texture = spriteTexture.getTextureView();
			transform.submitGuiElementRenderState(new TiledBlitRenderState(
				RenderPipelines.GUI,
                    TextureSetup.singleTexture(texture, spriteTexture.getSampler()),
                    new Matrix3x2f(transform.pose()),
                    16,16,
                    x,y,x+w,y+fluidHeight,
                    0,1,
                    0,1,
                    color,
                    transform.peekScissorStack()
                ));
			
		}
	}


	private static void buildVertex(VertexConsumer bu, PoseStack transform, float r, float g, float b, float a,
			float p1, float p2, float u0, float u1, int light, int overlay) {
		bu.addVertex(transform.last().pose(), p1, p2, 0).setColor(r, g, b, a).setUv(u0, u1).setOverlay(overlay).setLight(light)
				.setNormal(1f, 1f, 1f);
	}



	public static void drawRepeatedSprite(VertexConsumer builder, PoseStack transform, float x, float y, float w,
			float h, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax, float r, float g,
			float b, float alpha, int light, int overlay) {
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
						iconHeight, r, g, b, alpha, uMin, uMax, vMin, vMax, light, overlay);
			drawTexturedColoredRect(builder, transform, x + ww * iconWidth, y + iterMaxH * iconHeight, iconWidth,
					leftoverH, r, g, b, alpha, uMin, uMax, vMin, (vMin + iconVDif * leftoverHf), light, overlay);
		}
		if (leftoverW > 0) {
			for (int hh = 0; hh < iterMaxH; hh++)
				drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + hh * iconHeight, leftoverW,
						iconHeight, r, g, b, alpha, uMin, (uMin + iconUDif * leftoverWf), vMin, vMax, light, overlay);
			drawTexturedColoredRect(builder, transform, x + iterMaxW * iconWidth, y + iterMaxH * iconHeight, leftoverW,
					leftoverH, r, g, b, alpha, uMin, (uMin + iconUDif * leftoverWf), vMin,
					(vMin + iconVDif * leftoverHf), light, overlay);
		}
	}

	public static void drawTexturedColoredRect(VertexConsumer builder, PoseStack transform, float x, float y, float w,
			float h, float r, float g, float b, float alpha, float u0, float u1, float v0, float v1, int light,
			int overlay) {
		buildVertex(builder, transform, r, g, b, alpha, x, y + h, u0, v1, light, overlay);
		buildVertex(builder, transform, r, g, b, alpha, x + w, y + h, u1, v1, light, overlay);
		buildVertex(builder, transform, r, g, b, alpha, x + w, y, u1, v0, light, overlay);
		buildVertex(builder, transform, r, g, b, alpha, x, y, u0, v0, light, overlay);
	}
}
