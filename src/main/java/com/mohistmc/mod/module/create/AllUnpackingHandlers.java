package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.api.packager.unpacking.UnpackingHandler;
import com.mohistmc.mod.module.create.impl.unpacking.BasinUnpackingHandler;
import com.mohistmc.mod.module.create.impl.unpacking.CrafterUnpackingHandler;
import com.mohistmc.mod.module.create.impl.unpacking.DefaultUnpackingHandler;
import com.mohistmc.mod.module.create.impl.unpacking.VoidingUnpackingHandler;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;

public class AllUnpackingHandlers {
    public static final UnpackingHandler DEFAULT = new DefaultUnpackingHandler();
    public static final UnpackingHandler BASIN = register(AllBlocks.BASIN, BasinUnpackingHandler::new);
    public static final UnpackingHandler CREATIVE_CRATE = register(
        AllBlocks.CREATIVE_CRATE,
        VoidingUnpackingHandler::new
    );
    public static final UnpackingHandler MECHANICAL_CRAFTER = register(
        AllBlocks.MECHANICAL_CRAFTER,
        CrafterUnpackingHandler::new
    );

    public static UnpackingHandler register(Block block, Supplier<UnpackingHandler> factory) {
        UnpackingHandler handler = factory.get();
        UnpackingHandler.REGISTRY.register(block, handler);
        return handler;
    }

    public static void register() {
    }
}
