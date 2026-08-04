package com.mohistmc.mod.module.farmersdelight.data;

import com.mohistmc.mod.module.farmersdelight.data.advancement.FDAdvancementGenerator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

@ParametersAreNonnullByDefault
public class Advancements extends AdvancementProvider
{
	public Advancements(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(output, lookupProvider, List.of(new FDAdvancementGenerator()));
	}
}
