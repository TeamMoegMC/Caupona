package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.function.Consumer;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.client.renderer.MosaicRenderer;
import com.teammoeg.caupona.item.CPBlockItem;
import com.teammoeg.caupona.util.TabType;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class MosaicItem extends CPBlockItem {

	public MosaicItem(Properties props) {
		super(CPBlocks.MOSAIC.get(), props,TabType.DECORATION);
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			MosaicRenderer renderer=new MosaicRenderer();
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return renderer;
			}
			
		});
	}

}
