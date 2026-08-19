package com.mohistmc.mod.module.flywheel.backend.compile;

import com.mohistmc.mod.module.flywheel.api.instance.Instance;
import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.flywheel.backend.gl.shader.GlProgram;
import com.mohistmc.mod.module.flywheel.backend.glsl.SourceComponent;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record Pipeline(Identifier vertexMain, Identifier fragmentMain, InstanceAssembler assembler,
                       String compilerMarker, Consumer<GlProgram> onLink) {

    @FunctionalInterface
    public interface InstanceAssembler {
        /**
         * Generate the source component necessary to convert a packed {@link Instance} into its shader representation.
         *
         * @return A source component defining functions that unpack a representation of the given instance type.
         */
        SourceComponent assemble(InstanceType<?> instanceType);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        @Nullable
        private Identifier vertexMain;
        @Nullable
        private Identifier fragmentMain;
        @Nullable
        private InstanceAssembler assembler;
        @Nullable
        private String compilerMarker;
        @Nullable
        private Consumer<GlProgram> onLink;

        public Builder vertexMain(Identifier shader) {
            vertexMain = shader;
            return this;
        }

        public Builder fragmentMain(Identifier shader) {
            fragmentMain = shader;
            return this;
        }

        public Builder assembler(InstanceAssembler assembler) {
            this.assembler = assembler;
            return this;
        }

        public Builder compilerMarker(String compilerMarker) {
            this.compilerMarker = compilerMarker;
            return this;
        }

        public Builder onLink(Consumer<GlProgram> onLink) {
            this.onLink = onLink;
            return this;
        }

        public Pipeline build() {
            Objects.requireNonNull(vertexMain);
            Objects.requireNonNull(fragmentMain);
            Objects.requireNonNull(assembler);
            Objects.requireNonNull(compilerMarker);
            Objects.requireNonNull(onLink);
            return new Pipeline(vertexMain, fragmentMain, assembler, compilerMarker, onLink);
        }
    }
}
