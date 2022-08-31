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

package com.teammoeg.caupona.client;

import java.util.List;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.blocks.others.CPSignTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class CPSignRenderer implements BlockEntityRenderer<CPSignTileEntity> {
	public static final int MAX_LINE_WIDTH = 90;
	private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
	private final SignRenderer.SignModel signrenderer$signmodel;
	private final Font font;
	Material material;

	public CPSignRenderer(BlockEntityRendererProvider.Context pContext) {
		signrenderer$signmodel = new SignRenderer.SignModel(
				pContext.bakeLayer(ModelLayers.createSignModelName(CPBlocks.WALNUT)));
		material = Sheets.getSignMaterial(CPBlocks.WALNUT);
		this.font = pContext.getFont();
	}

	@SuppressWarnings("resource")
	public void render(CPSignTileEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
			MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
		BlockState blockstate = pBlockEntity.getBlockState();
		pPoseStack.pushPose();
		if (blockstate.getBlock() instanceof StandingSignBlock) {
			pPoseStack.translate(0.5D, 0.5D, 0.5D);
			float f1 = -(blockstate.getValue(StandingSignBlock.ROTATION) * 360 / 16.0F);
			pPoseStack.mulPose(Vector3f.YP.rotationDegrees(f1));
			signrenderer$signmodel.stick.visible = true;
		} else {
			pPoseStack.translate(0.5D, 0.5D, 0.5D);
			float f4 = -blockstate.getValue(WallSignBlock.FACING).toYRot();
			pPoseStack.mulPose(Vector3f.YP.rotationDegrees(f4));
			pPoseStack.translate(0.0D, -0.3125D, -0.4375D);
			signrenderer$signmodel.stick.visible = false;
		}

		pPoseStack.pushPose();
		pPoseStack.scale(0.6666667F, -0.6666667F, -0.6666667F);

		VertexConsumer vertexconsumer = material.buffer(pBufferSource, signrenderer$signmodel::renderType);
		signrenderer$signmodel.root.render(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay);
		pPoseStack.popPose();
		pPoseStack.translate(0.0D, 0.33333334F, 0.046666667F);
		pPoseStack.scale(0.010416667F, -0.010416667F, 0.010416667F);
		int i = getDarkColor(pBlockEntity);
		FormattedCharSequence[] aformattedcharsequence = pBlockEntity
				.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (p_173653_) -> {
					List<FormattedCharSequence> list = this.font.split(p_173653_, 90);
					return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
				});
		int k;
		boolean flag;
		int l;
		if (pBlockEntity.hasGlowingText()) {
			k = pBlockEntity.getColor().getTextColor();
			flag = isOutlineVisible(pBlockEntity, k);
			l = 15728880;
		} else {
			k = i;
			flag = false;
			l = pPackedLight;
		}

		for (int i1 = 0; i1 < 4; ++i1) {
			FormattedCharSequence formattedcharsequence = aformattedcharsequence[i1];
			float f3 = -this.font.width(formattedcharsequence) / 2;
			if (flag) {
				this.font.drawInBatch8xOutline(formattedcharsequence, f3, i1 * 10 - 20, k, i, pPoseStack.last().pose(),
						pBufferSource, l);
			} else {
				this.font.drawInBatch(formattedcharsequence, f3, i1 * 10 - 20, k, false, pPoseStack.last().pose(),
						pBufferSource, false, 0, l);
			}
		}

		pPoseStack.popPose();
	}

	@SuppressWarnings("resource")
	private static boolean isOutlineVisible(CPSignTileEntity pBlockEntity, int pTextColor) {
		if (pTextColor == DyeColor.BLACK.getTextColor()) {
			return true;
		}
		Minecraft minecraft = Minecraft.getInstance();
		LocalPlayer localplayer = minecraft.player;
		if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
			return true;
		}
		Entity entity = minecraft.getCameraEntity();
		return entity != null
				&& entity.distanceToSqr(Vec3.atCenterOf(pBlockEntity.getBlockPos())) < OUTLINE_RENDER_DISTANCE;
	}

	private static int getDarkColor(CPSignTileEntity pBlockEntity) {
		int i = pBlockEntity.getColor().getTextColor();
		int j = (int) (NativeImage.getR(i) * 0.4D);
		int k = (int) (NativeImage.getG(i) * 0.4D);
		int l = (int) (NativeImage.getB(i) * 0.4D);
		return i == DyeColor.BLACK.getTextColor() && pBlockEntity.hasGlowingText() ? -988212
				: NativeImage.combine(0, l, k, j);
	}

	public static SignRenderer.SignModel createSignModel(EntityModelSet pEntityModelSet, WoodType pWoodType) {
		return new SignRenderer.SignModel(pEntityModelSet.bakeLayer(ModelLayers.createSignModelName(pWoodType)));
	}

	public static LayerDefinition createSignLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();
		partdefinition.addOrReplaceChild("sign",
				CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F),
				PartPose.ZERO);
		partdefinition.addOrReplaceChild("stick",
				CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
		return LayerDefinition.create(meshdefinition, 64, 32);
	}

}