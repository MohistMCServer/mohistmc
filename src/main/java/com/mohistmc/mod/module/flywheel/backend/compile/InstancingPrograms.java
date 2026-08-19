package com.mohistmc.mod.module.flywheel.backend.compile;

import com.google.common.collect.ImmutableList;
import com.mohistmc.mod.module.flywheel.api.instance.InstanceType;
import com.mohistmc.mod.module.flywheel.api.material.Material;
import com.mohistmc.mod.module.flywheel.backend.gl.GlCompat;
import com.mohistmc.mod.module.flywheel.backend.gl.shader.GlProgram;
import com.mohistmc.mod.module.flywheel.backend.glsl.GlslVersion;
import com.mohistmc.mod.module.flywheel.backend.glsl.ShaderSources;
import com.mohistmc.mod.module.flywheel.backend.glsl.SourceComponent;
import com.mohistmc.mod.module.flywheel.backend.util.AtomicReferenceCounted;
import java.util.List;
import org.jspecify.annotations.Nullable;

public class InstancingPrograms extends AtomicReferenceCounted {
    private static final List<String> EXTENSIONS = getExtensions(GlCompat.MAX_GLSL_VERSION);

    @Nullable
    private static InstancingPrograms instance;

    private final PipelineCompiler pipeline;

    private final OitPrograms oitPrograms;

    private InstancingPrograms(PipelineCompiler pipeline, OitPrograms oitPrograms) {
        this.pipeline = pipeline;
        this.oitPrograms = oitPrograms;
    }

    private static List<String> getExtensions(GlslVersion glslVersion) {
        var extensions = ImmutableList.<String>builder();
        if (glslVersion.compareTo(GlslVersion.V330) < 0) {
            extensions.add("GL_ARB_shader_bit_encoding");
        }
        return extensions.build();
    }

    static void reload(
        ShaderSources sources,
        List<SourceComponent> vertexComponents,
        List<SourceComponent> fragmentComponents
    ) {
        if (!GlCompat.SUPPORTS_INSTANCING) {
            return;
        }

        var pipelineCompiler = PipelineCompiler.create(
            sources,
            Pipelines.INSTANCING,
            vertexComponents,
            fragmentComponents,
            EXTENSIONS
        );
        var fullscreen = OitPrograms.createFullscreenCompiler(sources);
        InstancingPrograms newInstance = new InstancingPrograms(pipelineCompiler, fullscreen);

        setInstance(newInstance);
    }

    static void setInstance(@Nullable InstancingPrograms newInstance) {
        if (instance != null) {
            instance.release();
        }
        if (newInstance != null) {
            newInstance.acquire();
        }
        instance = newInstance;
    }

    @Nullable
    public static InstancingPrograms get() {
        return instance;
    }

    public static boolean allLoaded() {
        return instance != null;
    }

    public static void kill() {
        setInstance(null);
    }

    public GlProgram get(
        InstanceType<?> instanceType,
        ContextShader contextShader,
        Material material,
        PipelineCompiler.OitMode mode
    ) {
        return pipeline.get(instanceType, contextShader, material, mode);
    }

    public OitPrograms oitPrograms() {
        return oitPrograms;
    }

    @Override
    protected void _delete() {
        pipeline.delete();
        oitPrograms.delete();
    }
}
