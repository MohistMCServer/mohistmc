package com.mohistmc.mod.module.farmersdelight.data;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.tag.ModTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityTypeIds;

public class EntityTags extends EntityTypeTagsProvider
{
	public EntityTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, FarmersDelight.MODID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(ModTags.EntityTypes.DOG_FOOD_USERS).add(EntityTypeIds.WOLF);
		this.tag(ModTags.EntityTypes.HORSE_FEED_USERS).add(
				EntityTypeIds.HORSE,
				EntityTypeIds.SKELETON_HORSE,
				EntityTypeIds.ZOMBIE_HORSE,
				EntityTypeIds.DONKEY,
				EntityTypeIds.MULE,
				EntityTypeIds.LLAMA);
		this.tag(ModTags.EntityTypes.HORSE_FEED_TEMPTED).add(
				EntityTypeIds.HORSE,
				EntityTypeIds.DONKEY,
				EntityTypeIds.MULE);
	}

}
