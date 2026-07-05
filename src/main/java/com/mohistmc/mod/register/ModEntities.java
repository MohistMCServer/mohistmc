package com.mohistmc.mod.register;

import com.mohistmc.mod.MohistMC;
import com.mohistmc.mod.entity.BulletEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * @author Mgazul
 * @date 2026/3/31
 */
public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MohistMC.MODID);

    public static final Supplier<EntityType<BulletEntity>> BULLET = ENTITY_TYPES.register("bullet",
            () -> {
                var location = Identifier.fromNamespaceAndPath(MohistMC.MODID, "bullet");
                var key = ResourceKey.create(Registries.ENTITY_TYPE, location);
                return EntityType.Builder.<BulletEntity>of(BulletEntity::new, MobCategory.MISC)
                        .noSummon().noSave().fireImmune().sized(0.0625F, 0.0625F).clientTrackingRange(5).updateInterval(5).setShouldReceiveVelocityUpdates(false)
                        .build(key);
            });

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
