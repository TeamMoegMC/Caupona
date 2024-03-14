package com.teammoeg.caupona;

import com.teammoeg.caupona.util.IFoodInfo;
import com.teammoeg.caupona.util.SauteedFoodInfo;
import com.teammoeg.caupona.util.StewInfo;
import com.teammoeg.caupona.util.Utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CPCapability {
	public static final ItemCapability<IFoodInfo, Void> FOOD_INFO=ItemCapability.createVoid(new ResourceLocation(CPMain.MODID,"food_info"), IFoodInfo.class);
	public static final DeferredRegister<AttachmentType<?>> REGISTRY=DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CPMain.MODID);
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<StewInfo>> STEW_INFO=REGISTRY.register("stew_info", ()->AttachmentType.serializable(t->new StewInfo(getLegacyStewTag(t))).copyOnDeath().comparator(Object::equals).build());
	public static final DeferredHolder<AttachmentType<?>, AttachmentType<SauteedFoodInfo>> SAUTEED_INFO=REGISTRY.register("sauteed_info", ()->AttachmentType.serializable(t->new SauteedFoodInfo(getLegacyDishTag(t))).copyOnDeath().comparator(Object::equals).build());
	public CPCapability() {
	}
	public static CompoundTag getLegacyDishTag(IAttachmentHolder obj) {
		if(obj instanceof ItemStack stack) {
			if (stack.hasTag()) {
				CompoundTag soupTag = stack.getTagElement("dish");
				if (soupTag != null)
					return soupTag;
			}
		}
		return new CompoundTag();
	}
	public static CompoundTag getLegacyStewTag(IAttachmentHolder h) {
		if(h instanceof ItemStack t) {
			if (t.hasTag()) {
				CompoundTag soupTag = Utils.extractDataElement(t, "soup");
				return soupTag == null ? new StewInfo(Utils.getFluidTypeRL(t)).serializeNBT()
						: soupTag;
			}
		}else if(h instanceof FluidStack stack) {
			if (stack.hasTag()) {
				CompoundTag nbt = stack.getChildTag("soup");
				if (nbt != null)
					return nbt;
			}
			return new StewInfo(Utils.getRegistryName(stack.getFluid())).serializeNBT();
		}
		return new StewInfo().serializeNBT();
	}

}
