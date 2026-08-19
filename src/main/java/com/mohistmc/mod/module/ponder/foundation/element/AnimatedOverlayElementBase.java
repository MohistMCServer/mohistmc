package com.mohistmc.mod.module.ponder.foundation.element;

import com.mohistmc.mod.module.create.catnip.animation.LerpedFloat;
import com.mohistmc.mod.module.ponder.api.element.AnimatedOverlayElement;

public abstract class AnimatedOverlayElementBase extends PonderElementBase implements AnimatedOverlayElement {

    protected LerpedFloat fade;

    public AnimatedOverlayElementBase() {
        fade = LerpedFloat.linear().startWithValue(0);
    }

    @Override
    public void setFade(float fade) {
        this.fade.setValue(fade);
    }

    @Override
    public float getFade(float partialTicks) {
        return fade.getValue(partialTicks);
    }

}