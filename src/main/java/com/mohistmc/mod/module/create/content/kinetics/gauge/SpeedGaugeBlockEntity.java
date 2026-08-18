package com.mohistmc.mod.module.create.content.kinetics.gauge;

import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.catnip.theme.Color;
import com.mohistmc.mod.module.create.content.kinetics.base.IRotate.SpeedLevel;
import com.mohistmc.mod.module.create.infrastructure.config.AllConfigs;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;

public class SpeedGaugeBlockEntity extends GaugeBlockEntity {
    public SpeedGaugeBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.SPEEDOMETER, pos, state);
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        float speed = Math.abs(getSpeed());

        dialTarget = getDialTarget(speed);
        color = Color.mixColors(SpeedLevel.of(speed).getColor(), 0xffffff, 0.25f);

        setChanged();
    }

    public static float getDialTarget(float speed) {
        speed = Math.abs(speed);
        float medium = AllConfigs.server().kinetics.mediumSpeed.get();
        float fast = AllConfigs.server().kinetics.fastSpeed.get();
        float max = AllConfigs.server().kinetics.maxRotationSpeed.get().floatValue();
        float target;
        if (speed == 0) {
            target = 0;
        } else if (speed < medium) {
            target = Mth.lerp(speed / medium, 0, 0.45f);
        } else if (speed < fast) {
            target = Mth.lerp((speed - medium) / (fast - medium), 0.45f, 0.75f);
        } else {
            target = Mth.lerp((speed - fast) / (max - fast), 0.75f, 1.125f);
        }
        return target;
    }
}
