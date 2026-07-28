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

import java.util.Arrays;
import java.util.function.Consumer;

import org.joml.Matrix3x2f;
import org.joml.Quaternionf;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teammoeg.caupona.util.Utils;

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
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
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

	@Deprecated
	public static final Quaternionf rotate90=new Quaternionf().rotateX((float) (Math.PI/2));
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
	public static void handleGuiTank(GuiGraphicsExtractor transform, ResourceHandler<FluidResource> tank, int x, int y, int w, int h,int mouseX,int mouseY,Consumer<Component> tooltip) {
		FluidRenderHelper.handleGuiTank(transform, tank, 0, x, y, w, h, mouseX, mouseY, tooltip);
	}
	public static void handleGuiTank(GuiGraphicsExtractor transform, ResourceHandler<FluidResource> tank,int index, int x, int y, int w, int h,int mouseX,int mouseY,Consumer<Component> tooltip) {
		FluidResource fr=tank.getResource(index);
		if(fr.isEmpty())return;
		FluidStack fluid = fr.toStack(tank.getAmountAsInt(index));
		if (fluid != null && fluid.getFluid() != null) {
			
			int fluidHeight = (int) (h * (tank.getAmountAsInt(index) / (float) tank.getCapacityAsInt(index,tank.getResource(index))));
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
			if (mouseX >= x && mouseY >= y && mouseX < x + w && mouseY < y + h) {
				Player p=Minecraft.getInstance().player;
				tooltip.accept(fluid.getHoverName());
				for(TypedDataComponent<?> o:fluid.getComponents()) {
					if(o.value() instanceof TooltipProvider tt) {
						tt.addToTooltip(TooltipContext.of(p.level(), p), tooltip, TooltipFlag.NORMAL, fluid);
					}
				}
				tooltip.accept(Utils.string(tank.getAmountAsInt(index)+"/"+tank.getCapacityAsInt(index, fr)));
			}
			
		}
	}
	public static void submitColoredTexturedRect(SubmitNodeCollector buffer,PoseStack poseStack,TextureAtlasSprite sprite, 
		float x0,float y0, 
		float w,float h, 
		int color,int packedLight,int packedOverlay) {
		submitColoredTexturedRect(buffer, poseStack, sprite, x0, y0, 0, w, h, 0, color, packedLight, packedOverlay);
	}
	public static void submitColoredTexturedRect(SubmitNodeCollector buffer, PoseStack poseStack, TextureAtlasSprite sprite, 
		float x0,float y0,float z0,float w,float h,float d,int color, int packedLight, int packedOverlay) {
		buffer.submitCustomGeometry(poseStack, RenderTypes.translucentMovingBlock(), (matrixStack, builder) -> {
			FluidRenderHelper.drawTexturedColoredRect(builder, matrixStack, x0, y0, z0, w, h, d,
					color,
					sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1(), packedLight,
					packedOverlay);
		});
	}

	private static void buildVertex(VertexConsumer bu, Pose transform, 
		int color,
		float x, float y, float z, 
		float u, float v, float nx,float ny,float nz,
		int light, int overlay) {
		bu.addVertex(transform, x, y, z).setColor(color).setUv(u, v).setOverlay(overlay).setLight(light)
				.setNormal(nx, ny, nz);
	}
    public static float[] computeNormal(
    	float x1, float y1, float z1,
    	float x2, float y2, float z2,
    	float x3, float y3, float z3) {
    	float v1x = x2 - x1;
    	float v1y = y2 - y1;
    	float v1z = z2 - z1;
        
    	float v2x = x3 - x1;
        float v2y = y3 - y1;
        float v2z = z3 - z1;
        
        float nx = v1y * v2z - v1z * v2y;
        float ny = v1z * v2x - v1x * v2z;
        float nz = v1x * v2y - v1y * v2x;
        
        return new float[]{nx, ny, -nz};
    }
    public static void main(String[] args) {
    	System.out.println(Arrays.toString(computeNormal(1,0,1,1,0,0,0,0,0)));
    }
	public static void drawRepeatedSprite(VertexConsumer builder, Pose transform, 
		float x, float y, float w, float h, 
		int iconWidth, int iconHeight, 
		float uMin, float uMax, float vMin, float vMax, 
		int color, int light, int overlay) {
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
	public static void drawTexturedColoredRect(VertexConsumer builder, Pose transform, 
		float x, float y, float w, float h, 
		int color, 
		float u0, float u1, float v0, float v1, 
		int light, int overlay) {
		FluidRenderHelper.drawTexturedColoredRect(builder, transform, x, y, 0, w, h, 0, color, u0, u1, v0, v1, light, overlay);
	}
	public static void drawTexturedColoredRect(VertexConsumer builder, Pose transform, 
		float x, float y, float z, 
		float w,float h, float d, 
		int color, 
		float u0, float u1, float v0, float v1, 
		int light, int overlay) {
		float[] normal=computeNormal(x + w, y + h, z + d,
									  x + w, y    , z    ,
									  x    , y    , z    );
		buildVertex(builder, transform, color, x + w, y + h, z + d, u1, v1, normal[0], normal[1], normal[2], light, overlay);
		buildVertex(builder, transform, color, x + w, y    , z    , u1, v0, normal[0], normal[1], normal[2], light, overlay);
		buildVertex(builder, transform, color, x    , y    , z    , u0, v0, normal[0], normal[1], normal[2], light, overlay);
		buildVertex(builder, transform, color, x    , y + h, z + d, u0, v1, normal[0], normal[1], normal[2], light, overlay);
	}
}
