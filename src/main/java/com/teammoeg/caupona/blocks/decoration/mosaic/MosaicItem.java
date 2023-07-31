package com.teammoeg.caupona.blocks.decoration.mosaic;

import java.util.List;
import java.util.function.Consumer;

import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.client.renderer.MosaicRenderer;
import com.teammoeg.caupona.item.CPBlockItem;
import com.teammoeg.caupona.util.CreativeTabItemHelper;
import com.teammoeg.caupona.util.TabType;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class MosaicItem extends CPBlockItem {

	public MosaicItem(Properties props) {
		super(CPBlocks.MOSAIC.get(), props,TabType.DECORATION);
	}
	@Override
	public void fillItemCategory(CreativeTabItemHelper helper) {
		/*if(helper.isType(TabType.DECORATION)) {
			for(MosaicMaterial m1:MosaicMaterial.values())
				for(MosaicMaterial m2:MosaicMaterial.values())
					for(MosaicPattern pattern:MosaicPattern.values()) {
						if(m1==m2)continue;
						ItemStack stack=new ItemStack(this);
						setMosaic(stack, m1, m2, pattern);
						helper.accept(stack);
					}
			
		}*/
			
	}
	@Override
	public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		// TODO Auto-generated method stub
		CompoundTag tag=pStack.getTagElement("caupona:mosaic");
		if(tag!=null) {
			MosaicPattern pattern=MosaicPattern.valueOf(tag.getString("pattern"));
			MosaicMaterial m1=MosaicMaterial.valueOf(tag.getString("mat1"));
			MosaicMaterial m2=MosaicMaterial.valueOf(tag.getString("mat2"));
			pTooltip.add(Utils.translate("tooltip.caupona.mosaic.material_1",Utils.translate("item.caupona."+m1+"_tesserae")));
			pTooltip.add(Utils.translate("tooltip.caupona.mosaic.material_2",Utils.translate("item.caupona."+m2+"_tesserae")));
			pTooltip.add(Utils.translate("tooltip.caupona.mosaic.pattern."+pattern));
		}
		super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
		
	}
	public static void setMosaic(ItemStack stack,MosaicMaterial m1,MosaicMaterial m2,MosaicPattern p) {
		CompoundTag tag=stack.getOrCreateTagElement("caupona:mosaic");
		tag.putString("pattern", p.name());
		tag.putString("mat1", m1.name());
		tag.putString("mat2", m2.name());

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
