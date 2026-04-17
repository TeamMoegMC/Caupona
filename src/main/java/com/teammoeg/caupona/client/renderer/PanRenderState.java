package com.teammoeg.caupona.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;

public class PanRenderState extends BlockEntityRenderState {
	public static enum LayerType{
		PAN(""),PLATE("");
		final String pathSuffix;


		private LayerType(String pathSuffix) {
			this.pathSuffix = pathSuffix;
		}
		public String getPathSuffix() {
			return pathSuffix;
		}
	}
	Identifier model;
	LayerType layer;
}
