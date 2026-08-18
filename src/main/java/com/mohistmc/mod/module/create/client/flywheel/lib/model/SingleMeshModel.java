package com.mohistmc.mod.module.create.client.flywheel.lib.model;

import com.google.common.collect.ImmutableList;
import com.mohistmc.mod.module.create.client.flywheel.api.material.Material;
import com.mohistmc.mod.module.create.client.flywheel.api.model.Mesh;
import com.mohistmc.mod.module.create.client.flywheel.api.model.Model;
import java.util.List;
import org.joml.Vector4fc;

public class SingleMeshModel implements Model {
    private final Mesh mesh;
    private final ImmutableList<ConfiguredMesh> meshList;

    public SingleMeshModel(Mesh mesh, Material material) {
        this.mesh = mesh;
        meshList = ImmutableList.of(new ConfiguredMesh(material, mesh));
    }

    @Override
    public List<ConfiguredMesh> meshes() {
        return meshList;
    }

    @Override
    public Vector4fc boundingSphere() {
        return mesh.boundingSphere();
    }
}
