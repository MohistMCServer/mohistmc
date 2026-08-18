package com.mohistmc.mod.mixin.create;

import com.mohistmc.mod.module.create.content.kinetics.deployer.DeployerPlayer;
import com.mohistmc.mod.module.create.infrastructure.config.AllConfigs;
import com.mohistmc.mod.module.create.infrastructure.config.CKinetics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "setTarget(Lnet/minecraft/world/entity/LivingEntity;)V", at = @At("HEAD"), cancellable = true)
    private void ignoreAttack(LivingEntity target, CallbackInfo ci) {
        if (target instanceof DeployerPlayer) {
            CKinetics.DeployerAggroSetting setting = AllConfigs.server().kinetics.ignoreDeployerAttacks.get();
            switch (setting) {
                case ALL -> ci.cancel();
                case CREEPERS -> {
                    if ((Mob) (Object) this instanceof Creeper) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
