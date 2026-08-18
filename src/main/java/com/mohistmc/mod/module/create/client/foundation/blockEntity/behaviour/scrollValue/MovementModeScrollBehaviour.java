package com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.scrollValue;

import com.mohistmc.mod.module.create.client.catnip.lang.Lang;
import com.mohistmc.mod.module.create.client.content.contraptions.DirectionalExtenderScrollOptionSlot;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.mohistmc.mod.module.create.client.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.mohistmc.mod.module.create.client.foundation.gui.AllIcons;
import com.mohistmc.mod.module.create.client.foundation.utility.CreateLang;
import com.mohistmc.mod.module.create.content.contraptions.IControlContraption.MovementMode;
import com.mohistmc.mod.module.create.content.contraptions.piston.MechanicalPistonBlock;
import com.mohistmc.mod.module.create.content.kinetics.base.IRotate;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;

public class MovementModeScrollBehaviour extends ScrollOptionBehaviour<MovementMode> {
    public MovementModeScrollBehaviour(SmartBlockEntity be, Component title, ValueBoxTransform slot) {
        super(MovementModeIcon.class, MovementModeIcon::from, title, be, slot);
    }

    public static MovementModeScrollBehaviour pulley(SmartBlockEntity be) {
        return new MovementModeScrollBehaviour(
            be,
            CreateLang.translateDirect("contraptions.movement_mode"),
            new CenteredSideValueBoxTransform((state, d) -> d == Direction.UP)
        );
    }

    public static MovementModeScrollBehaviour piston(SmartBlockEntity be) {
        return new MovementModeScrollBehaviour(
            be,
            CreateLang.translateDirect("contraptions.movement_mode"),
            new DirectionalExtenderScrollOptionSlot((state, d) -> {
                Axis axis = d.getAxis();
                Axis extensionAxis = state.getValue(MechanicalPistonBlock.FACING).getAxis();
                Axis shaftAxis = ((IRotate) state.getBlock()).getRotationAxis(state);
                return extensionAxis != axis && shaftAxis != axis;
            })
        );
    }

    private enum MovementModeIcon implements INamedIconOptions {
        MOVE_PLACE(AllIcons.I_MOVE_PLACE),
        MOVE_PLACE_RETURNED(AllIcons.I_MOVE_PLACE_RETURNED),
        MOVE_NEVER_PLACE(AllIcons.I_MOVE_NEVER_PLACE);

        private final String translationKey;
        private final AllIcons icon;

        MovementModeIcon(AllIcons icon) {
            this.icon = icon;
            translationKey = "create.contraptions.movement_mode." + Lang.asId(name());
        }

        public static MovementModeIcon from(MovementMode mode) {
            return switch (mode) {
                case MOVE_PLACE -> MOVE_PLACE;
                case MOVE_PLACE_RETURNED -> MOVE_PLACE_RETURNED;
                case MOVE_NEVER_PLACE -> MOVE_NEVER_PLACE;
            };
        }

        @Override
        public AllIcons getIcon() {
            return icon;
        }

        @Override
        public String getTranslationKey() {
            return translationKey;
        }
    }
}
