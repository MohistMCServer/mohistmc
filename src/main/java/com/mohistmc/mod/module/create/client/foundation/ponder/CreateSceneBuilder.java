package com.mohistmc.mod.module.create.client.foundation.ponder;

import com.mohistmc.mod.module.create.catnip.math.VecHelper;
import com.mohistmc.mod.module.create.client.catnip.data.FunctionalHelper;
import com.mohistmc.mod.module.create.client.content.contraptions.glue.SuperGlueSelectionHandler;
import com.mohistmc.mod.module.create.client.foundation.ponder.element.BeltItemElement;
import com.mohistmc.mod.module.create.client.foundation.ponder.element.ExpandedParrotElement;
import com.mohistmc.mod.module.create.client.foundation.ponder.instruction.AnimateBlockEntityInstruction;
import com.mohistmc.mod.module.create.client.ponder.api.element.ElementLink;
import com.mohistmc.mod.module.create.client.ponder.api.element.ParrotElement;
import com.mohistmc.mod.module.create.client.ponder.api.element.ParrotPose;
import com.mohistmc.mod.module.create.client.ponder.api.element.WorldSectionElement;
import com.mohistmc.mod.module.create.client.ponder.api.level.PonderLevel;
import com.mohistmc.mod.module.create.client.ponder.api.scene.SceneBuilder;
import com.mohistmc.mod.module.create.client.ponder.api.scene.Selection;
import com.mohistmc.mod.module.create.client.ponder.foundation.PonderScene;
import com.mohistmc.mod.module.create.client.ponder.foundation.PonderSceneBuilder;
import com.mohistmc.mod.module.create.client.ponder.foundation.element.ElementLinkImpl;
import com.mohistmc.mod.module.create.client.ponder.foundation.instruction.CreateParrotInstruction;
import com.mohistmc.mod.module.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.mohistmc.mod.module.create.content.fluids.pump.PumpBlockEntity;
import com.mohistmc.mod.module.create.content.kinetics.base.IRotate;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlock;
import com.mohistmc.mod.module.create.content.kinetics.base.KineticBlockEntity;
import com.mohistmc.mod.module.create.content.kinetics.belt.BeltBlockEntity;
import com.mohistmc.mod.module.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.mohistmc.mod.module.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.mohistmc.mod.module.create.content.kinetics.crafter.ConnectedInputHandler;
import com.mohistmc.mod.module.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.mohistmc.mod.module.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.mohistmc.mod.module.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.mohistmc.mod.module.create.content.logistics.funnel.FunnelBlockEntity;
import com.mohistmc.mod.module.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.mohistmc.mod.module.create.content.redstone.displayLink.LinkWithBulbBlockEntity;
import com.mohistmc.mod.module.create.content.trains.display.FlapDisplayBlockEntity;
import com.mohistmc.mod.module.create.content.trains.signal.SignalBlockEntity;
import com.mohistmc.mod.module.create.content.trains.station.StationBlockEntity;
import com.mohistmc.mod.module.create.foundation.blockEntity.SmartBlockEntity;
import com.mohistmc.mod.module.create.infrastructure.particle.RotationIndicatorParticleData;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class CreateSceneBuilder extends PonderSceneBuilder {

    private final EffectInstructions effects;
    private final WorldInstructions world;
    private final SpecialInstructions special;

    public CreateSceneBuilder(SceneBuilder baseSceneBuilder) {
        this(baseSceneBuilder.getScene());
    }

    private CreateSceneBuilder(PonderScene ponderScene) {
        super(ponderScene);
        effects = new EffectInstructions();
        world = new WorldInstructions();
        special = new SpecialInstructions();
    }

    @Override
    public EffectInstructions effects() {
        return effects;
    }

    @Override
    public WorldInstructions world() {
        return world;
    }

    @Override
    public SpecialInstructions special() {
        return special;
    }

    public class EffectInstructions extends PonderEffectInstructions {

        public void superGlue(BlockPos pos, Direction side, boolean fullBlock) {
            addInstruction(scene -> SuperGlueSelectionHandler.spawnParticles(scene.getLevel(), pos, side, fullBlock));
        }

        private void rotationIndicator(BlockPos pos, boolean direction, BlockPos displayPos) {
            addInstruction(scene -> {
                BlockState blockState = scene.getLevel().getBlockState(pos);
                BlockEntity blockEntity = scene.getLevel().getBlockEntity(pos);

                if (!(blockState.getBlock() instanceof KineticBlock kb)) {
                    return;
                }
                if (!(blockEntity instanceof KineticBlockEntity kbe)) {
                    return;
                }

                Direction.Axis rotationAxis = kb.getRotationAxis(blockState);

                float speed = kbe.getTheoreticalSpeed();
                IRotate.SpeedLevel speedLevel = IRotate.SpeedLevel.of(speed);
                int color = direction ? speed > 0 ? 0xeb5e0b : 0x1687a7 : speedLevel.getColor();
                int particleSpeed = speedLevel.getParticleSpeed();
                particleSpeed *= Math.signum(speed);

                Vec3 location = VecHelper.getCenterOf(displayPos);
                RotationIndicatorParticleData particleData = new RotationIndicatorParticleData(
                    color,
                    particleSpeed,
                    kb.getParticleInitialRadius(),
                    kb.getParticleTargetRadius(),
                    20,
                    rotationAxis
                );

                for (int i = 0; i < 20; i++) {
                    scene.getLevel().addParticle(particleData, location.x, location.y, location.z, 0, 0, 0);
                }
            });
        }

        public void rotationSpeedIndicator(BlockPos pos) {
            rotationIndicator(pos, false, pos);
        }

        public void rotationDirectionIndicator(BlockPos pos) {
            rotationIndicator(pos, true, pos);
        }


    }

    public class WorldInstructions extends PonderWorldInstructions {

        public void rotateBearing(BlockPos pos, float angle, int duration) {
            addInstruction(AnimateBlockEntityInstruction.bearing(pos, angle, duration));
        }

        public void movePulley(BlockPos pos, float distance, int duration) {
            addInstruction(AnimateBlockEntityInstruction.pulley(pos, distance, duration));
        }

        public void animateBogey(BlockPos pos, float distance, int duration) {
            addInstruction(AnimateBlockEntityInstruction.bogey(pos, distance, duration + 1));
        }

        public void moveDeployer(BlockPos pos, float distance, int duration) {
            addInstruction(AnimateBlockEntityInstruction.deployer(pos, distance, duration));
        }

        public void createItemOnBeltLike(BlockPos location, Direction insertionSide, ItemStack stack) {
            addInstruction(scene -> {
                PonderLevel world = scene.getLevel();
                BlockEntity blockEntity = world.getBlockEntity(location);
                if (!(blockEntity instanceof SmartBlockEntity beltBlockEntity)) {
                    return;
                }
                DirectBeltInputBehaviour behaviour = beltBlockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE);
                if (behaviour == null) {
                    return;
                }
                behaviour.handleInsertion(stack, insertionSide.getOpposite(), false);
            });
            flapFunnel(location.above(), true);
        }

        public ElementLink<BeltItemElement> createItemOnBelt(
            BlockPos beltLocation,
            Direction insertionSide,
            ItemStack stack
        ) {
            ElementLink<BeltItemElement> link = new ElementLinkImpl<>(BeltItemElement.class);
            addInstruction(scene -> {
                PonderLevel world = scene.getLevel();
                BlockEntity blockEntity = world.getBlockEntity(beltLocation);
                if (!(blockEntity instanceof BeltBlockEntity beltBlockEntity)) {
                    return;
                }

                DirectBeltInputBehaviour behaviour = beltBlockEntity.getBehaviour(DirectBeltInputBehaviour.TYPE);
                behaviour.handleInsertion(stack, insertionSide.getOpposite(), false);

                BeltBlockEntity controllerBE = beltBlockEntity.getControllerBE();
                if (controllerBE != null) {
                    controllerBE.tick();
                }

                TransportedItemStackHandlerBehaviour transporter = beltBlockEntity.getBehaviour(
                    TransportedItemStackHandlerBehaviour.TYPE);
                transporter.handleProcessingOnAllItems(tis -> {
                    BeltItemElement tracker = new BeltItemElement(tis);
                    scene.addElement(tracker);
                    scene.linkElement(tracker, link);
                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                });
            });
            flapFunnel(beltLocation.above(), true);
            return link;
        }

        public void removeItemsFromBelt(BlockPos beltLocation) {
            addInstruction(scene -> {
                PonderLevel world = scene.getLevel();
                BlockEntity blockEntity = world.getBlockEntity(beltLocation);
                if (!(blockEntity instanceof SmartBlockEntity beltBlockEntity)) {
                    return;
                }
                TransportedItemStackHandlerBehaviour transporter = beltBlockEntity.getBehaviour(
                    TransportedItemStackHandlerBehaviour.TYPE);
                if (transporter == null) {
                    return;
                }
                transporter.handleCenteredProcessingOnAllItems(
                    0.52f,
                    tis -> TransportedItemStackHandlerBehaviour.TransportedResult.removeItem()
                );
            });
        }

        public void stallBeltItem(ElementLink<BeltItemElement> link, boolean stalled) {
            addInstruction(scene -> {
                BeltItemElement resolve = scene.resolve(link);
                if (resolve != null) {
                    resolve.ifPresent(tis -> tis.locked = stalled);
                }
            });
        }

        public void changeBeltItemTo(ElementLink<BeltItemElement> link, ItemStack newStack) {
            addInstruction(scene -> {
                BeltItemElement resolve = scene.resolve(link);
                if (resolve != null) {
                    resolve.ifPresent(tis -> tis.stack = newStack);
                }
            });
        }

        public void setKineticSpeed(Selection selection, float speed) {
            modifyKineticSpeed(selection, f -> speed);
        }

        public void multiplyKineticSpeed(Selection selection, float modifier) {
            modifyKineticSpeed(selection, f -> f * modifier);
        }

        public void modifyKineticSpeed(Selection selection, UnaryOperator<Float> speedFunc) {
            modifyBlockEntityNBT(
                selection, SpeedGaugeBlockEntity.class, nbt -> {
                    float newSpeed = speedFunc.apply(nbt.getFloatOr("Speed", 0));
                    nbt.putFloat("Value", SpeedGaugeBlockEntity.getDialTarget(newSpeed));
                }
            );
            modifyBlockEntityNBT(
                selection, KineticBlockEntity.class, nbt -> {
                    nbt.putFloat("Speed", speedFunc.apply(nbt.getFloatOr("Speed", 0)));
                }
            );
        }

        public void propagatePipeChange(BlockPos pos) {
            modifyBlockEntity(pos, PumpBlockEntity.class, be -> be.onSpeedChanged(0));
        }

        public void setFilterData(Selection selection, Class<? extends BlockEntity> teType, ItemStack filter) {
            modifyBlockEntityNBT(
                selection, teType, nbt -> {
                    if (!filter.isEmpty()) {
                        RegistryOps<Tag> ops = world().getHolderLookupProvider()
                            .createSerializationContext(NbtOps.INSTANCE);
                        nbt.store("Filter", ItemStack.CODEC, ops, filter);
                    }
                }
            );
        }

        public void instructArm(
            BlockPos armLocation,
            ArmBlockEntity.Phase phase,
            ItemStack heldItem,
            int targetedPoint
        ) {
            modifyBlockEntityNBT(
                scene.getSceneBuildingUtil().select().position(armLocation), ArmBlockEntity.class, compound -> {
                    compound.store("Phase", ArmBlockEntity.Phase.CODEC, phase);
                    if (!heldItem.isEmpty()) {
                        RegistryOps<Tag> ops = world().getHolderLookupProvider()
                            .createSerializationContext(NbtOps.INSTANCE);
                        compound.store("HeldItem", ItemStack.CODEC, ops, heldItem);
                    } else {
                        compound.remove("HeldItem");
                    }
                    compound.putInt("TargetPointIndex", targetedPoint);
                    compound.putFloat("MovementProgress", 0);
                }
            );
        }

        public void flapFunnel(BlockPos position, boolean outward) {
            modifyBlockEntity(position, FunnelBlockEntity.class, funnel -> funnel.flap(!outward));
        }

        public void setCraftingResult(BlockPos crafter, ItemStack output) {
            modifyBlockEntity(crafter, MechanicalCrafterBlockEntity.class, mct -> mct.setScriptedResult(output));
        }

        public void connectCrafterInvs(BlockPos position1, BlockPos position2) {
            addInstruction(s -> {
                ConnectedInputHandler.toggleConnection(s.getLevel(), position1, position2);
                s.forEach(WorldSectionElement.class, WorldSectionElement::queueRedraw);
            });
        }

        public void toggleControls(BlockPos position) {
            cycleBlockProperty(position, ControlsBlock.VIRTUAL);
        }

        public void animateTrainStation(BlockPos position, boolean trainPresent) {
            modifyBlockEntityNBT(
                getScene().getSceneBuildingUtil().select().position(position),
                StationBlockEntity.class,
                c -> c.putBoolean("ForceFlag", trainPresent)
            );
        }

        public void conductorBlaze(BlockPos position, boolean conductor) {
            modifyBlockEntityNBT(
                getScene().getSceneBuildingUtil().select().position(position),
                BlazeBurnerBlockEntity.class,
                c -> c.putBoolean("TrainHat", conductor)
            );
        }

        public void changeSignalState(BlockPos position, SignalBlockEntity.SignalState state) {
            modifyBlockEntityNBT(
                getScene().getSceneBuildingUtil().select().position(position),
                SignalBlockEntity.class,
                c -> c.store("State", SignalBlockEntity.SignalState.CODEC, state)
            );
        }

        public void setDisplayBoardText(BlockPos position, int line, Component text) {
            modifyBlockEntity(position, FlapDisplayBlockEntity.class, t -> t.applyTextManually(line, text));
        }

        public void dyeDisplayBoard(BlockPos position, int line, DyeColor color) {
            modifyBlockEntity(position, FlapDisplayBlockEntity.class, t -> t.setColour(line, color));
        }

        public void flashDisplayLink(BlockPos position) {
            modifyBlockEntity(position, LinkWithBulbBlockEntity.class, LinkWithBulbBlockEntity::pulse);
        }

        @Override
        public void restoreBlocks(Selection selection) {
            super.restoreBlocks(selection);
            markSmartBlockEntityVirtual(selection);
        }

        @Override
        public void setBlocks(Selection selection, BlockState state, boolean spawnParticles) {
            super.setBlocks(selection, state, spawnParticles);
            markSmartBlockEntityVirtual(selection);
        }

        @Override
        public void modifyBlocks(Selection selection, UnaryOperator<BlockState> stateFunc, boolean spawnParticles) {
            super.modifyBlocks(selection, stateFunc, spawnParticles);
            markSmartBlockEntityVirtual(selection);
        }

        private void markSmartBlockEntityVirtual(Selection selection) {
            addInstruction(scene -> selection.forEach(pos -> {
                if (scene.getLevel().getBlockEntity(pos) instanceof SmartBlockEntity smartBlockEntity) {
                    smartBlockEntity.markVirtual();
                }
            }));
        }
    }

    public class SpecialInstructions extends PonderSpecialInstructions {

        @Override
        public ElementLink<ParrotElement> createBirb(Vec3 location, Supplier<? extends ParrotPose> pose) {
            ElementLink<ParrotElement> link = new ElementLinkImpl<>(ParrotElement.class);
            ParrotElement parrot = ExpandedParrotElement.create(location, pose);
            addInstruction(new CreateParrotInstruction(10, Direction.DOWN, parrot));
            addInstruction(scene -> scene.linkElement(parrot, link));
            return link;
        }

        public ElementLink<ParrotElement> birbOnTurntable(BlockPos pos) {
            return createBirb(VecHelper.getCenterOf(pos), () -> new ParrotSpinOnComponentPose(pos));
        }

        public ElementLink<ParrotElement> birbOnSpinnyShaft(BlockPos pos) {
            return createBirb(VecHelper.getCenterOf(pos).add(0, 0.5, 0), () -> new ParrotSpinOnComponentPose(pos));
        }

        public void conductorBirb(ElementLink<ParrotElement> birb, boolean conductor) {
            addInstruction(scene -> scene.resolveOptional(birb)
                .map(FunctionalHelper.filterAndCast(ExpandedParrotElement.class))
                .ifPresent(expandedBirb -> expandedBirb.setConductor(conductor)));
        }

        public static class ParrotSpinOnComponentPose extends ParrotPose {
            private final BlockPos componentPos;

            public ParrotSpinOnComponentPose(BlockPos componentPos) {
                this.componentPos = componentPos;
            }

            @Override
            public void tick(PonderScene scene, Parrot entity, Vec3 location) {
                BlockEntity blockEntity = scene.getLevel().getBlockEntity(componentPos);
                if (!(blockEntity instanceof KineticBlockEntity)) {
                    return;
                }
                float rpm = ((KineticBlockEntity) blockEntity).getSpeed();
                entity.yRotO = entity.getYRot();
                entity.setYRot(entity.yRotO + rpm * 0.3f);
            }
        }
    }

}