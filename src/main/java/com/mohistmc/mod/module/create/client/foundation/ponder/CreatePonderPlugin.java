package com.mohistmc.mod.module.create.client.foundation.ponder;

import com.mohistmc.mod.module.create.AllBlocks;
import com.mohistmc.mod.module.create.client.infrastructure.ponder.AllCreatePonderScenes;
import com.mohistmc.mod.module.create.client.infrastructure.ponder.AllCreatePonderTags;
import com.mohistmc.mod.module.ponder.api.level.PonderLevel;
import com.mohistmc.mod.module.ponder.api.registration.*;
import com.mohistmc.mod.module.create.content.kinetics.crank.ValveHandleBlock;
import com.mohistmc.mod.module.create.content.logistics.packagePort.postbox.PostboxBlock;
import com.mohistmc.mod.module.create.content.logistics.tableCloth.TableClothBlock;
import net.minecraft.resources.Identifier;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class CreatePonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<Identifier> helper) {
        AllCreatePonderScenes.register(helper);
    }

    @Override
    public void registerTags(PonderTagRegistrationHelper<Identifier> helper) {
        AllCreatePonderTags.register(helper);
    }

    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
        helper.registerSharedText("rpm8", "8 RPM");
        helper.registerSharedText("rpm16", "16 RPM");
        helper.registerSharedText("rpm16_source", "Source: 16 RPM");
        helper.registerSharedText("rpm32", "32 RPM");

        helper.registerSharedText("movement_anchors", "With the help of Super Glue, larger structures can be moved.");
        helper.registerSharedText(
            "behaviour_modify_value_panel",
            "This behaviour can be modified using the value panel"
        );
        helper.registerSharedText(
            "storage_on_contraption",
            "Inventories attached to the Contraption will pick up their drops automatically"
        );
    }

    @Override
    public void onPonderLevelRestore(PonderLevel ponderLevel) {
        PonderWorldBlockEntityFix.fixControllerBlockEntities(ponderLevel);
    }

    @Override
    public void indexExclusions(IndexExclusionHelper helper) {
        helper.excludeBlockVariants(ValveHandleBlock.class, AllBlocks.COPPER_VALVE_HANDLE);
        helper.excludeBlockVariants(PostboxBlock.class, AllBlocks.POSTBOX.white());
        helper.excludeBlockVariants(TableClothBlock.class, AllBlocks.TABLE_CLOTH.white());
    }
}