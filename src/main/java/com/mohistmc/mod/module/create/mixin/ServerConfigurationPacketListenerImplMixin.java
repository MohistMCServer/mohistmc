package com.mohistmc.mod.module.create.mixin;

import com.zurrtum.create.infrastructure.config.SyncConfigTask;
import java.util.Queue;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerConfigurationPacketListenerImpl.class)
public abstract class ServerConfigurationPacketListenerImplMixin {
    @Shadow
    @Final
    private Queue<ConfigurationTask> configurationTasks;

    @Shadow
    protected abstract void finishCurrentTask(ConfigurationTask.Type taskTypeToFinish);

    @Inject(method = "addOptionalTasks()V", at = @At("TAIL"))
    private void queueSendResourcePackTask(CallbackInfo ci) {
        configurationTasks.add(new SyncConfigTask(this::finishCurrentTask));
    }
}
