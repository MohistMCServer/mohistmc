package com.mohistmc.mod.module.create.client.content.fluids.hosePulley;

import com.mohistmc.mod.module.create.client.AllPartialModels;
import com.mohistmc.mod.module.create.client.AllSpriteShifts;
import com.mohistmc.mod.module.create.client.catnip.render.CachedBuffers;
import com.mohistmc.mod.module.create.client.catnip.render.SpriteShiftEntry;
import com.mohistmc.mod.module.create.client.catnip.render.SuperByteBuffer;
import com.mohistmc.mod.module.create.client.content.contraptions.pulley.AbstractPulleyRenderer;
import com.mohistmc.mod.module.create.client.flywheel.lib.model.baked.PartialModel;
import com.mohistmc.mod.module.create.content.fluids.hosePulley.HosePulleyBlock;
import com.mohistmc.mod.module.create.content.fluids.hosePulley.HosePulleyBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction.Axis;

public class HosePulleyRenderer extends AbstractPulleyRenderer<HosePulleyBlockEntity> {

    public HosePulleyRenderer(BlockEntityRendererProvider.Context context) {
        super(context, AllPartialModels.HOSE_HALF, AllPartialModels.HOSE_HALF_MAGNET);
    }

    @Override
    protected Axis getShaftAxis(HosePulleyBlockEntity be) {
        return be.getBlockState().getValue(HosePulleyBlock.HORIZONTAL_FACING).getClockWise().getAxis();
    }

    @Override
    protected PartialModel getCoil() {
        return AllPartialModels.HOSE_COIL;
    }

    @Override
    protected SuperByteBuffer renderRope(HosePulleyBlockEntity be) {
        return CachedBuffers.partial(AllPartialModels.HOSE, be.getBlockState());
    }

    @Override
    protected SuperByteBuffer renderMagnet(HosePulleyBlockEntity be) {
        return CachedBuffers.partial(AllPartialModels.HOSE_MAGNET, be.getBlockState());
    }

    @Override
    protected float getOffset(HosePulleyBlockEntity be, float partialTicks) {
        return be.getInterpolatedOffset(partialTicks);
    }

    @Override
    protected SpriteShiftEntry getCoilShift() {
        return AllSpriteShifts.HOSE_PULLEY_COIL;
    }

    @Override
    protected boolean isRunning(HosePulleyBlockEntity be) {
        return true;
    }

}
