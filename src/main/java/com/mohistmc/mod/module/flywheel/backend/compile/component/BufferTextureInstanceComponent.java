package com.mohistmc.mod.module.flywheel.backend.compile.component;

import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.flywheel.api.layout.Layout;
import com.mohistmc.mod.module.flywheel.backend.glsl.generate.FnSignature;
import com.mohistmc.mod.module.flywheel.backend.glsl.generate.GlslBlock;
import com.mohistmc.mod.module.flywheel.backend.glsl.generate.GlslBuilder;
import com.mohistmc.mod.module.flywheel.backend.glsl.generate.GlslExpr;
import com.mohistmc.mod.module.flywheel.backend.glsl.generate.GlslStmt;
import com.mohistmc.mod.module.flywheel.lib.math.MoreMath;
import com.mohistmc.mod.module.flywheel.lib.util.ResourceUtil;
import java.util.ArrayList;

public class BufferTextureInstanceComponent extends InstanceAssemblerComponent {
    private static final String[] SWIZZLE_SELECTORS = {"x", "y", "z", "w"};

    public BufferTextureInstanceComponent(InstanceType<?> type) {
        super(type);
    }

    @Override
    public String name() {
        return ResourceUtil.rl("buffer_texture_instance_assembler").toString();
    }

    @Override
    protected void generateUnpacking(GlslBuilder builder) {
        var fnBody = new GlslBlock();

        int texels = MoreMath.ceilingDiv(layout.byteSize(), 16);

        fnBody.add(GlslStmt.raw("int base = " + UNPACK_ARG + " * " + texels + ";"));

        for (int i = 0; i < texels; i++) {
            // Fetch all the texels for the given instance ahead of time to simplify the unpacking generators.
            fnBody.add(GlslStmt.raw("uvec4 u" + i + " = texelFetch(_flw_instances, base + " + i + ");"));
        }

        var unpackArgs = new ArrayList<GlslExpr>();
        for (Layout.Element element : layout.elements()) {
            unpackArgs.add(unpackElement(element));
        }

        fnBody.ret(GlslExpr.call(STRUCT_NAME, unpackArgs));

        builder.uniform().type("usamplerBuffer").name("_flw_instances");
        builder.blankLine();
        builder.function()
            .signature(FnSignature.create().returnType(STRUCT_NAME).name(UNPACK_FN_NAME).arg("int", UNPACK_ARG).build())
            .body(fnBody);
    }

    @Override
    protected GlslExpr access(int uintOffset) {
        return GlslExpr.variable("u" + (uintOffset >> 2)).swizzle(SWIZZLE_SELECTORS[uintOffset & 3]);
    }
}
