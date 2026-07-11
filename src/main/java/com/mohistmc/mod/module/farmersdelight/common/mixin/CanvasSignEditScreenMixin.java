package com.mohistmc.mod.module.farmersdelight.common.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mohistmc.mod.module.farmersdelight.client.gui.CanvasSignEditScreen;
import com.mohistmc.mod.module.farmersdelight.client.gui.HangingCanvasSignEditScreen;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.CanvasSignBlockEntity;
import com.mohistmc.mod.module.farmersdelight.common.block.entity.HangingCanvasSignBlockEntity;

@Mixin(LocalPlayer.class)
public class CanvasSignEditScreenMixin
{
	@Shadow
	@Final
	protected Minecraft minecraft;

	@Inject(at = @At(value = "HEAD"), method = "openTextEdit", cancellable = true)
	private void openCanvasSignEditScreen(SignBlockEntity sign, boolean isFront, CallbackInfo ci) {
		if (sign instanceof CanvasSignBlockEntity) {
			minecraft.gui.setScreen(new CanvasSignEditScreen(sign, isFront, minecraft.isTextFilteringEnabled()));
			ci.cancel();
		}
		if (sign instanceof HangingCanvasSignBlockEntity) {
			minecraft.gui.setScreen(new HangingCanvasSignEditScreen(sign, isFront, minecraft.isTextFilteringEnabled()));
			ci.cancel();
		}
	}
}
