package com.teammoeg.caupona.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class StewPotRenderState extends BlockEntityRenderState {
	TextureAtlasSprite inModel;
	TextureAtlasSprite outModel;
	int inColor;
	int outColor;
	float level;
	public StewPotRenderState() {
	}

}
