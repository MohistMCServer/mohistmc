package com.mohistmc.mod.module.create.content.contraptions.bearing;

import com.mohistmc.mod.module.create.content.contraptions.IControlContraption;

public interface IBearingBlockEntity extends IControlContraption {

    float getInterpolatedAngle(float partialTicks);

    boolean isWoodenTop();

    void setAngle(float forcedAngle);

}
