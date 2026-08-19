package com.mohistmc.mod.module.flywheel.lib.model.baked;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mohistmc.mod.module.flywheel.api.material.Material;
import com.mohistmc.mod.module.flywheel.api.model.Model;
import com.mohistmc.mod.module.flywheel.lib.model.SimpleModel;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import java.util.function.Function;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.jetbrains.annotations.UnknownNullability;
import org.jspecify.annotations.Nullable;

class MeshEmitterManager<T extends MeshEmitter> {
    private static final ChunkSectionLayer[] CHUNK_LAYERS = ChunkSectionLayer.values();

    private final Reference2ReferenceMap<ChunkSectionLayer, T> emitterMap = new Reference2ReferenceArrayMap<>();
    private final ByteBufferBuilderStack byteBufferBuilderStack = new ByteBufferBuilderStack();

    @UnknownNullability
    private BlockMaterialFunction blockMaterialFunction;

    MeshEmitterManager(Function<ByteBufferBuilderStack, T> meshEmitterFactory) {
        for (ChunkSectionLayer renderType : CHUNK_LAYERS) {
            emitterMap.put(renderType, meshEmitterFactory.apply(byteBufferBuilderStack));
        }
    }

    public T getEmitter(ChunkSectionLayer renderType) {
        return emitterMap.get(renderType);
    }

    public void prepare(BlockMaterialFunction blockMaterialFunction) {
        this.blockMaterialFunction = blockMaterialFunction;
        byteBufferBuilderStack.reset();
    }

    public void prepareForBlock() {
        for (MeshEmitter emitter : emitterMap.values()) {
            emitter.prepareForBlock();
        }
    }

    public SimpleModel end() {
        blockMaterialFunction = null;

        ImmutableList.Builder<Model.ConfiguredMesh> meshes = ImmutableList.builder();

        for (MeshEmitter emitter : emitterMap.values()) {
            emitter.end(meshes);
        }

        return new SimpleModel(meshes.build());
    }

    @Nullable
    public BufferBuilder getBuffer(ChunkSectionLayer renderType, boolean shade, boolean ao) {
        Material key = blockMaterialFunction.apply(renderType, shade, ao);
        if (key != null) {
            return emitterMap.get(renderType).getBuffer(key);
        }
        return null;
    }
}
