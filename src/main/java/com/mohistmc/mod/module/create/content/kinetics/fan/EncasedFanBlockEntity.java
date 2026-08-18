package com.mohistmc.mod.module.create.content.kinetics.fan;

import com.mohistmc.mod.module.create.AllAdvancements;
import com.mohistmc.mod.module.create.AllBlockEntityTypes;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import com.mohistmc.mod.module.create.content.logistics.chute.ChuteBlockEntity;
import com.mohistmc.mod.module.create.foundation.advancement.CreateTrigger;
import com.mohistmc.mod.module.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import org.jspecify.annotations.Nullable;

public class EncasedFanBlockEntity extends KineticBlockEntity implements IAirCurrentSource {

    public AirCurrent airCurrent;
    protected int airCurrentUpdateCooldown;
    protected int entitySearchCooldown;
    protected boolean updateAirFlow;

    public EncasedFanBlockEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.ENCASED_FAN, pos, state);
        airCurrent = new AirCurrent(this);
        updateAirFlow = true;
    }

    @Override
    public List<CreateTrigger> getAwardables() {
        return List.of(AllAdvancements.ENCASED_FAN, AllAdvancements.FAN_PROCESSING);
    }

    @Override
    protected void read(ValueInput view, boolean clientPacket) {
        super.read(view, clientPacket);
        if (clientPacket) {
            airCurrent.rebuild();
        }
    }

    @Override
    public AirCurrent getAirCurrent() {
        return airCurrent;
    }

    @Nullable
    @Override
    public Level getAirCurrentWorld() {
        return level;
    }

    @Override
    public BlockPos getAirCurrentPos() {
        return worldPosition;
    }

    @Override
    public Direction getAirflowOriginSide() {
        return getBlockState().getValue(EncasedFanBlock.FACING);
    }

    @Override
    @Nullable
    public Direction getAirFlowDirection() {
        float speed = getSpeed();
        if (speed == 0) {
            return null;
        }
        Direction facing = getBlockState().getValue(BlockStateProperties.FACING);
        speed = convertToDirection(speed, facing);
        return speed > 0 ? facing : facing.getOpposite();
    }

    @Override
    public void remove() {
        super.remove();
        updateChute();
    }

    @Override
    public boolean isSourceRemoved() {
        return remove;
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        updateAirFlow = true;
        updateChute();
    }

    public void updateChute() {
        Direction direction = getBlockState().getValue(EncasedFanBlock.FACING);
        if (!direction.getAxis().isVertical()) {
            return;
        }
        BlockEntity poweredChute = level.getBlockEntity(worldPosition.relative(direction));
        if (!(poweredChute instanceof ChuteBlockEntity chuteBE)) {
            return;
        }
        if (direction == Direction.DOWN) {
            chuteBE.updatePull();
        } else {
            chuteBE.updatePush(1);
        }
    }

    public void blockInFrontChanged() {
        updateAirFlow = true;
    }

    @Override
    public void tick() {
        super.tick();

        boolean server = !level.isClientSide() || isVirtual();

        if (server && airCurrentUpdateCooldown-- <= 0) {
            airCurrentUpdateCooldown = AllConfigs.server().kinetics.fanBlockCheckRate.get();
            updateAirFlow = true;
        }

        if (updateAirFlow) {
            updateAirFlow = false;
            airCurrent.rebuild();
            if (airCurrent.maxDistance > 0) {
                award(AllAdvancements.ENCASED_FAN);
            }
            sendData();
        }

        if (getSpeed() == 0) {
            return;
        }

        if (entitySearchCooldown-- <= 0) {
            entitySearchCooldown = 5;
            airCurrent.findEntities();
        }

        airCurrent.tick();
    }

}