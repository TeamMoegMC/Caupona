package com.teammoeg.caupona.data.recipes;

import java.util.List;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teammoeg.caupona.CPBlocks;
import com.teammoeg.caupona.CPMain;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.CommonHooks;

public class LoafBowlDigRecipe extends ShapelessRecipe {
    public static final MapCodec<LoafBowlDigRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
        i -> i.group(
                Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.bookInfo),
                ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result),
                com.mojang.serialization.Codec.lazyInitialized(() -> Ingredient.CODEC.listOf(1, ShapedRecipePattern.getMaxHeight() * ShapedRecipePattern.getMaxWidth())).fieldOf("ingredients").forGetter(o -> o.ingredients)
            )
            .apply(i, LoafBowlDigRecipe::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, LoafBowlDigRecipe> STREAM_CODEC = StreamCodec.composite(
        Recipe.CommonInfo.STREAM_CODEC,
        o -> o.commonInfo,
        CraftingRecipe.CraftingBookInfo.STREAM_CODEC,
        o -> o.bookInfo,
        ItemStackTemplate.STREAM_CODEC,
        o -> o.result,
        Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()),
        o -> o.ingredients,
        LoafBowlDigRecipe::new
    );


	ItemStackTemplate result;
    List<Ingredient> ingredients;
    public static final RecipeSerializer<LoafBowlDigRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
	public LoafBowlDigRecipe(CommonInfo commonInfo, CraftingBookInfo bookInfo, ItemStackTemplate result, List<Ingredient> ingredients) {
		super(commonInfo, bookInfo, result, ingredients);
		this.result=result;
		this.ingredients=ingredients;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		NonNullList<ItemStack> nnl=super.getRemainingItems(input);
		for(int slot=0;slot<nnl.size();slot++) {
			if(input.getItem(slot).is(CPBlocks.LOAF.get().asItem())) {
				nnl.set(slot, new ItemStack(BuiltInRegistries.ITEM.getValue(CPMain.rl("crumb"))));
			}else
			if(input.getItem(slot).isDamageableItem()) {
				ItemStack tool=input.getItem(slot).copy();
				if (CommonHooks.getCraftingPlayer().level() instanceof ServerLevel serverLevel) {
					tool.hurtAndBreak(1,serverLevel, CommonHooks.getCraftingPlayer(),_->{});
					nnl.set(slot, tool);
				}
			}
		}
		return nnl;
	}
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public RecipeSerializer getSerializer() {
		return SERIALIZER;
	}
}
