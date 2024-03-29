package com.teammoeg.caupona.client.util;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import com.teammoeg.caupona.CPMain;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.client.model.data.ModelData;

public class DynamicBlockModelReference implements Supplier<BakedModel>,Function<ModelData,List<BakedQuad>>
{

	private final ResourceLocation name;
	private static final RandomSource RANDOM_SOURCE=RandomSource.create();
	static {
		RANDOM_SOURCE.setSeed(42L);
	}
	public DynamicBlockModelReference(String name)
	{
		this.name = new ResourceLocation(CPMain.MODID, "block/dynamic/"+name);
	}
	public DynamicBlockModelReference(ResourceLocation rl)
	{
		this.name = rl;
	}
	@Override
	public BakedModel get()
	{
		return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getModelManager().getModel(name);
	}

	public List<BakedQuad> getAllQuads()
	{
		return apply(ModelData.EMPTY);
	}
	@Override
	public List<BakedQuad> apply(ModelData data)
	{
		return get().getQuads(null, null,RANDOM_SOURCE, data, null);
	}
	public static RandomSource getRandomSource() {
		return RANDOM_SOURCE;
	}

}