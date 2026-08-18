package com.mohistmc.mod.module.create;

import com.mohistmc.mod.module.create.api.contraption.ContraptionType;
import com.mohistmc.mod.module.create.api.registry.CreateRegistries;
import com.mohistmc.mod.module.create.content.contraptions.Contraption;
import com.mohistmc.mod.module.create.content.contraptions.bearing.BearingContraption;
import com.mohistmc.mod.module.create.content.contraptions.bearing.ClockworkContraption;
import com.mohistmc.mod.module.create.content.contraptions.bearing.StabilizedContraption;
import com.mohistmc.mod.module.create.content.contraptions.elevator.ElevatorContraption;
import com.mohistmc.mod.module.create.content.contraptions.gantry.GantryContraption;
import com.mohistmc.mod.module.create.content.contraptions.mounted.MountedContraption;
import com.mohistmc.mod.module.create.content.contraptions.piston.PistonContraption;
import com.mohistmc.mod.module.create.content.contraptions.pulley.PulleyContraption;
import com.mohistmc.mod.module.create.content.trains.entity.CarriageContraption;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import static com.mohistmc.mod.module.create.Create.MOD_ID;

public class AllContraptionTypes {
    public static final ContraptionType PISTON = register("piston", PistonContraption::new);
    public static final ContraptionType PULLEY = register("pulley", PulleyContraption::new);
    public static final ContraptionType MOUNTED = register("mounted", MountedContraption::new);
    public static final ContraptionType STABILIZED = register("stabilized", StabilizedContraption::new);
    public static final ContraptionType BEARING = register("bearing", BearingContraption::new);
    public static final ContraptionType GANTRY = register("gantry", GantryContraption::new);
    public static final ContraptionType CLOCKWORK = register("clockwork", ClockworkContraption::new);
    public static final ContraptionType CARRIAGE = register("carriage", CarriageContraption::new);
    public static final ContraptionType ELEVATOR = register("elevator", ElevatorContraption::new);

    private static ContraptionType register(String name, Supplier<? extends Contraption> factory) {
        return Registry.register(
            CreateRegistries.CONTRAPTION_TYPE,
            Identifier.fromNamespaceAndPath(MOD_ID, name),
            new ContraptionType(factory)
        );
    }

    public static void register() {
    }
}
