package com.teammoeg.caupona.client.renderer;

import com.teammoeg.caupona.client.util.DynamicBlockModelReference;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;

public class KitchenStoveRenderState extends BlockEntityRenderState {
	DynamicBlockModelReference stock;
	DynamicBlockModelReference ash;
	Direction dir;
}
