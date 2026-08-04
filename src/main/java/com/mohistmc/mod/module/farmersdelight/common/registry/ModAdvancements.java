package com.mohistmc.mod.module.farmersdelight.common.registry;

import com.mohistmc.mod.module.farmersdelight.FarmersDelight;
import com.mohistmc.mod.module.farmersdelight.common.advancement.CuttingBoardTrigger;
import java.util.function.Supplier;
import net.minecraft.advancements.triggers.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModAdvancements
{
	public static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, FarmersDelight.MODID);

	public static final Supplier<CuttingBoardTrigger> USE_CUTTING_BOARD = TRIGGERS.register("use_cutting_board", CuttingBoardTrigger::new);
}
