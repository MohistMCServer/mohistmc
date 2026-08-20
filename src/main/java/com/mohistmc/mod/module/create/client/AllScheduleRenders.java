package com.mohistmc.mod.module.create.client;

import com.mohistmc.mod.module.create.AllSchedules;
import com.mohistmc.mod.module.create.client.content.trains.schedule.IScheduleInput;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.FluidThresholdConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.IdleCargoConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.ItemThresholdConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.PlayerPassengerConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.RedstoneLinkConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.ScheduledDelayRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.StationPoweredConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.StationUnloadedConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.condition.TimeOfDayConditionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.destination.ChangeThrottleInstructionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.destination.ChangeTitleInstructionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.destination.DeliverPackagesInstructionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.destination.DestinationInstructionRender;
import com.mohistmc.mod.module.create.client.content.trains.schedule.destination.FetchPackagesInstructionRender;
import com.mohistmc.mod.module.create.content.trains.schedule.ScheduleDataEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.Identifier;

public class AllScheduleRenders {
    public static final Map<Identifier, IScheduleInput<?>> ALL = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends ScheduleDataEntry> IScheduleInput<T> get(T entry) {
        return (IScheduleInput<T>) ALL.get(entry.getId());
    }

    private static void register(Identifier schedule, Supplier<IScheduleInput<?>> render) {
        ALL.put(schedule, render.get());
    }

    public static void register() {
        register(AllSchedules.DESTINATION, DestinationInstructionRender::new);
        register(AllSchedules.PACKAGE_DELIVERY, DeliverPackagesInstructionRender::new);
        register(AllSchedules.PACKAGE_RETRIEVAL, FetchPackagesInstructionRender::new);
        register(AllSchedules.RENAME, ChangeTitleInstructionRender::new);
        register(AllSchedules.THROTTLE, ChangeThrottleInstructionRender::new);
        register(AllSchedules.DELAY, ScheduledDelayRender::new);
        register(AllSchedules.TIME_OF_DAY, TimeOfDayConditionRender::new);
        register(AllSchedules.FLUID_THRESHOLD, FluidThresholdConditionRender::new);
        register(AllSchedules.ITEM_THRESHOLD, ItemThresholdConditionRender::new);
        register(AllSchedules.REDSTONE_LINK, RedstoneLinkConditionRender::new);
        register(AllSchedules.PLAYER_COUNT, PlayerPassengerConditionRender::new);
        register(AllSchedules.IDLE, IdleCargoConditionRender::new);
        register(AllSchedules.UNLOADED, StationUnloadedConditionRender::new);
        register(AllSchedules.POWERED, StationPoweredConditionRender::new);
    }
}
