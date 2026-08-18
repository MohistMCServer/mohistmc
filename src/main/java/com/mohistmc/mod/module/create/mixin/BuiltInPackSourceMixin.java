package com.mohistmc.mod.module.create.mixin;

import com.zurrtum.create.foundation.pack.DynamicPack;
import com.zurrtum.create.foundation.pack.RuntimeDataGenerator;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds Create's generated server data. Static mod resources are discovered by
 * NeoForge directly and do not need Fabric's manual pack path wiring.
 */
@Mixin(BuiltInPackSource.class)
public class BuiltInPackSourceMixin {
    @Inject(method = "loadPacks(Ljava/util/function/Consumer;)V", at = @At("TAIL"))
    private void create$addDynamicDataPack(Consumer<Pack> result, CallbackInfo ci) {
        if (!((Object) this instanceof ServerPacksSource)) {
            return;
        }
        DynamicPack dynamicPack = new DynamicPack(
            "create:dynamic_data",
            Component.translatable("advancement.create.root"),
            PackType.SERVER_DATA
        );
        RuntimeDataGenerator.insertIntoPack(dynamicPack);
        if (!dynamicPack.isEmpty()) {
            PackSelectionConfig selection = new PackSelectionConfig(false, Pack.Position.BOTTOM, false);
            result.accept(Pack.readMetaAndCreate(
                dynamicPack.location(),
                new Pack.ResourcesSupplier() {
                    @Override
                    public net.minecraft.server.packs.PackResources openPrimary(
                        PackLocationInfo info
                    ) {
                        return dynamicPack;
                    }

                    @Override
                    public net.minecraft.server.packs.PackResources openFull(
                        PackLocationInfo info,
                        Pack.Metadata metadata
                    ) {
                        return dynamicPack;
                    }
                },
                PackType.SERVER_DATA,
                selection
            ));
        }
    }
}
