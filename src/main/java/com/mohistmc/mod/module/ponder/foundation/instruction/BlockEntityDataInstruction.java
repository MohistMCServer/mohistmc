package com.mohistmc.mod.module.ponder.foundation.instruction;

import com.mohistmc.mod.module.ponder.Ponder;
import com.mohistmc.mod.module.ponder.api.level.PonderLevel;
import com.mohistmc.mod.module.ponder.api.scene.Selection;
import com.mohistmc.mod.module.ponder.foundation.PonderScene;
import java.util.function.UnaryOperator;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

public class BlockEntityDataInstruction extends WorldModifyInstruction {

    private final boolean redraw;
    private final UnaryOperator<CompoundTag> data;
    private final Class<? extends BlockEntity> type;

    public BlockEntityDataInstruction(
        Selection selection,
        Class<? extends BlockEntity> type,
        UnaryOperator<CompoundTag> data,
        boolean redraw
    ) {
        super(selection);
        this.type = type;
        this.data = data;
        this.redraw = redraw;
    }

    @Override
    protected void runModification(Selection selection, PonderScene scene) {
        PonderLevel level = scene.getLevel();
        selection.forEach(pos -> {
            if (!level.getBounds().isInside(pos)) {
                return;
            }
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!type.isInstance(blockEntity)) {
                return;
            }
            RegistryAccess registryManager = level.registryAccess();
            CompoundTag apply = data.apply(blockEntity.saveWithFullMetadata(registryManager));
            //if (blockEntity instanceof SyncedBlockEntity) //TODO
            //	((SyncedBlockEntity) blockEntity).readClient(apply);
            try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(
                blockEntity.problemPath(),
                Ponder.LOGGER
            )) {
                ValueInput view = TagValueInput.create(logging, registryManager, apply);
                blockEntity.loadWithComponents(view);
            }
        });
    }

    @Override
    protected boolean needsRedraw() {
        return redraw;
    }

}
