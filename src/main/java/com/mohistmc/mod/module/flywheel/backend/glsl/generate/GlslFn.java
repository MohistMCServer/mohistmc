package com.mohistmc.mod.module.flywheel.backend.glsl.generate;

import com.mohistmc.mod.module.flywheel.lib.util.StringUtil;

import java.util.function.Consumer;

public class GlslFn implements GlslBuilder.Declaration {
    private FnSignature signature;
    private GlslBlock body = new GlslBlock();

    public GlslFn signature(FnSignature signature) {
        this.signature = signature;
        return this;
    }

    public GlslFn body(GlslBlock block) {
        body = block;
        return this;
    }

    public GlslFn body(Consumer<GlslBlock> f) {
        f.accept(body);
        return this;
    }

    @Override
    public String prettyPrint() {
        return """
            %s {
            %s
            }""".formatted(signature.fullDeclaration(), StringUtil.indent(body.prettyPrint(), 4));
    }
}
