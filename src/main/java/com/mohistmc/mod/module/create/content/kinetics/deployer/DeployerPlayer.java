package com.mohistmc.mod.module.create.content.kinetics.deployer;

import com.mojang.authlib.GameProfile;
import com.mohistmc.mod.module.create.api.entity.FakePlayerHandler;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

public interface DeployerPlayer {
    UUID FALLBACK_ID = UUID.fromString("9e2faded-cafe-4ec2-c314-dad129ae971d");

    static DeployerPlayer create(ServerLevel world, @Nullable UUID owner, @Nullable String name) {
        GameProfile profile = new GameProfile(
            owner == null ? FALLBACK_ID : owner,
            owner == null || name == null ? "Deployer" : name
        );
        if (FakePlayerHandler.FABRIC) {
            return new DeployerFabricFakePlayer(world, profile);
        }
        return new DeployerFakePlayer(world, profile);
    }

    @Nullable Pair<BlockPos, Float> getBlockBreakingProgress();

    void setBlockBreakingProgress(@Nullable Pair<BlockPos, Float> blockBreakingProgress);

    @Nullable ItemStack getSpawnedItemEffects();

    void setSpawnedItemEffects(@Nullable ItemStack spawnedItemEffects);

    ServerPlayerGameMode getInteractionManager();

    boolean getPlacedTracks();

    void setPlacedTracks(boolean placedTracks);

    boolean isOnMinecartContraption();

    void setOnMinecartContraption(boolean onMinecartContraption);

    default ServerPlayer cast() {
        return (ServerPlayer) this;
    }
}
