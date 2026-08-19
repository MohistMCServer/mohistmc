package com.mohistmc.mod.module.flywheel.lib.model;

import com.mohistmc.mod.module.flywheel.api.model.IndexSequence;
import com.mohistmc.mod.module.flywheel.api.model.Mesh;
import com.mohistmc.mod.module.flywheel.api.vertex.MutableVertexList;
import com.mohistmc.mod.module.flywheel.lib.vertex.VertexTransformations;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Vector4fc;

public record RetexturedMesh(Mesh mesh, TextureAtlasSprite sprite) implements Mesh {
    @Override
    public int vertexCount() {
        return mesh.vertexCount();
    }

    @Override
    public void write(MutableVertexList vertexList) {
        mesh.write(vertexList);
        VertexTransformations.retexture(vertexList, sprite);
    }

    @Override
    public IndexSequence indexSequence() {
        return mesh.indexSequence();
    }

    @Override
    public int indexCount() {
        return mesh.indexCount();
    }

    @Override
    public Vector4fc boundingSphere() {
        return mesh.boundingSphere();
    }
}

