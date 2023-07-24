package com.teammoeg.caupona.data.loot;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class AddPoolLootModifier extends LootModifier{
	public static final Codec<AddPoolLootModifier> CODEC = 
			RecordCodecBuilder.create(inst -> codecStart(inst)
		.and(ResourceLocation.CODEC.fieldOf("loot_table").forGetter(lm->lm.lootTable)).apply(inst, AddPoolLootModifier::new));
	ResourceLocation lootTable;
	protected AddPoolLootModifier(LootItemCondition[] conditionsIn,ResourceLocation table) {
		super(conditionsIn);
		this.lootTable=table;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
			LootContext context) {
		LootTable lt=context.getResolver().getLootTable(lootTable);
		//if(context.pushVisitedElement(LootContext.createVisitedEntry(lt))) {
		lt.getRandomItemsRaw(new LootContext.Builder(context).withQueriedLootTableId(lootTable).create(null),generatedLoot::add);
		//}
		return generatedLoot;
	}
	public static Builder builder(ResourceLocation table) {
		return new Builder(table);
	}
	public static class Builder{
		List<LootItemCondition> cond=new ArrayList<>();
		ResourceLocation table;
		Builder(ResourceLocation table) {
			super();
			this.table = table;
		}
		public Builder when(LootItemCondition.Builder builder) {
			cond.add(builder.build());
			return this;
		}
		public AddPoolLootModifier build() {
			return new AddPoolLootModifier(cond.toArray(LootItemCondition[]::new),table);
		}
	}
}
