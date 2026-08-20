package com.mohistmc.mod.module.flywheel.backend.compile;

import com.mohistmc.mod.module.flywheel.backend.compile.core.Compilation;
import com.mojang.serialization.Codec;
import java.util.Locale;
import net.minecraft.util.StringRepresentable;

public enum LightSmoothness implements StringRepresentable {
    FLAT(0, false), TRI_LINEAR(1, false), SMOOTH(2, false), SMOOTH_INNER_FACE_CORRECTED(2, true);

    public static final Codec<LightSmoothness> CODEC = StringRepresentable.fromEnum(LightSmoothness::values);

    private final int smoothnessDefine;
    private final boolean innerFaceCorrection;

    LightSmoothness(int smoothnessDefine, boolean innerFaceCorrection) {
        this.smoothnessDefine = smoothnessDefine;
        this.innerFaceCorrection = innerFaceCorrection;
    }

    public void onCompile(Compilation comp) {
        comp.define("_FLW_LIGHT_SMOOTHNESS", Integer.toString(smoothnessDefine));
        if (innerFaceCorrection) {
            comp.define("_FLW_INNER_FACE_CORRECTION");
        }
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
